package com.example.notification_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/*
* 알림 조회
* 알림 중복 체크
* 갯수 조회
* */
public interface NotificationRepository extends JpaRepository<Notifiaction, Long> {
}
