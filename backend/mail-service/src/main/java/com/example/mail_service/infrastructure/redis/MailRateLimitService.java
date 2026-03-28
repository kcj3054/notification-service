package com.example.mail_service.infrastructure.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;


/*
* 악의적 반복 요청 방지
* */

@Slf4j
@Service
@RequiredArgsConstructor
public class MailRateLimitService {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.mail.rate-limit.max-requests:5}")
    private int maxRequests;

    @Value("${app.mail.rate-limit.window-seconds:60}")
    private long windowSeconds;

    private static final String KEY_PREFIX = "mail:rate-limit:";

    public boolean isAllowed(String recipientEmail) {
        String key = KEY_PREFIX + recipientEmail;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofSeconds(windowSeconds));
        }

        if (count > maxRequests) {
            log.warn("Rate limit exceeded for email: {}", recipientEmail);
            return false;
        }

        return true;
    }
}
