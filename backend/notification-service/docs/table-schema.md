# Table Schema

## notification

알림 정보를 저장하는 테이블

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 알림 ID |
| event_id | VARCHAR(20) | NOT NULL, UNIQUE | 카프카 이벤트 ID |
| received_user_id | BIGINT | NOT NULL | 알림 수신 유저 ID |
| send_user_id | BIGINT | NOT NULL | 알림 발신 유저 ID |
| read_at | TIMESTAMP | NULL | 알림 읽은 시각 |
| create_at | TIMESTAMP | NOT NULL | 알림 생성 시각 |

- `event_id` 유니크 제약: `uq_notification_id`

---

## processed_event

Kafka 중복 메시지 방지를 위한 멱등성 처리 테이블

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 처리 이벤트 ID |
| consumer_group | VARCHAR | NOT NULL | Kafka consumer group 이름 |
| event_id | VARCHAR | NOT NULL | 카프카 이벤트 ID |
| processed_at | DATETIME | NOT NULL | 이벤트 처리 시각 |

- `consumer_group` + `event_id` 조합으로 중복 여부 판단

---

## user (auth-service 소유)

notification-service에서 직접 관리하지 않음. auth-service의 auth DB에 존재.

| 컬럼명 | 타입 | 제약조건 | 설명 |
|--------|------|----------|------|
| id | BIGINT | PK, AUTO_INCREMENT | 내부 식별자 |
| user_id | VARCHAR | NOT NULL, UNIQUE | 비즈니스 식별자 (UUID) |

- `notification.received_user_id` → `user.id` 참조
- `notification.send_user_id` → `user.id` 참조
