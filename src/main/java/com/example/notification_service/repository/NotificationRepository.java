package com.example.notification_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.management.Notification;

/*
* 알림 조회
* 알림 중복 체크
* 갯수 조회
* */
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
