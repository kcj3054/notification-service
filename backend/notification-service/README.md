# Notification Service

실시간 알림 발송 및 구독을 처리하는 마이크로서비스입니다.
Kafka 기반 이벤트 처리와 SSE(Server-Sent Events)를 활용해 비동기 실시간 알림을 구현했습니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| Language | Java 25 |
| Framework | Spring Boot 4.x |
| Message Queue | Apache Kafka |
| Cache | Redis |
| Database | MySQL 8.0 |
| ORM | Spring Data JPA (Hibernate 7) |
| 실시간 통신 | SSE (Server-Sent Events) |
| API 문서 | Swagger (springdoc-openapi) |

---

## 아키텍처

> 전체 흐름 다이어그램 → [notification-flow.md](docs/notification-flow.md)
>
> 읽음 처리 흐름 → [read-notification-flow.md](docs/read-notification-flow.md)
>
> 테이블 스키마 → [table-schema.md](docs/table-schema.md)

```
Client
  │
  ├─ POST /api/v1/notification     → Kafka 이벤트 발행
  │       └─ Consumer → Handler → DB 저장 → SSE push
  │
  └─ GET  /notification/subscribe/{userId}  → SSE 실시간 구독
```

---

## 현재 한계 및 개선 방향

| 한계 | 원인 | 개선 방향 |
|------|------|----------|
| 다중 서버 환경에서 SSE push 불가 | SseEmitter를 서버 메모리(ConcurrentHashMap)에 저장 | Redis Pub/Sub 도입으로 서버 간 이벤트 전파 |
| 안 읽은 알림 수 조회 시 DB 직접 접근 | 캐싱 없음 | Redis incr/decr로 카운터 캐싱 |
| 이벤트 처리 실패 시 유실 | DLQ 미구현 | Kafka DLQ 토픽으로 실패 이벤트 보관 및 재처리 |

---

## API 명세

서버 실행 후 → [http://localhost:8080/swagger](http://localhost:8080/swagger)

| Method | URI | 설명 |
|--------|-----|------|
| POST | /api/v1/notification | 알림 이벤트 발행 |
| GET | /notification/subscribe/{userId} | 실시간 알림 SSE 구독 |

---

## 실행 방법

### 사전 조건
- MySQL 8.0 실행 중 + `notification` 데이터베이스 생성
- Kafka 실행 중 (localhost:9092)

```sql
CREATE DATABASE notification;
```

### 실행

```bash
./gradlew bootRun
```

테이블은 `ddl-auto: update` 설정으로 자동 생성됩니다.
