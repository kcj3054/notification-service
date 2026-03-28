# Backend

Spring Boot 기반 MSA 백엔드 서비스 모음입니다.

## 서비스 구성

| 서비스 | 포트 | 설명 |
|--------|------|------|
| auth-service | 8081 | 인증/인가 |
| mail-service | 8082 | 메일 발송 |
| notification-service | 8083 | 알림 (SSE, Kafka) |

## 패키지 구조

모든 서비스는 **DDD 기반 레이어드 아키텍처**를 따릅니다.

```
com.example.<service>/
├── api/
│   ├── controller/     # HTTP 엔드포인트
│   └── dto/            # Request / Response DTO
├── application/
│   ├── (Service)       # 비즈니스 로직
│   └── handler/        # 이벤트 핸들러 (해당 서비스)
├── domain/
│   ├── (Entity)        # JPA 엔티티
│   ├── (Enum)          # 도메인 열거형
│   └── (Repository)    # Repository 인터페이스
├── infrastructure/
│   ├── kafka/          # Kafka Producer / Consumer / Config
│   └── redis/          # Redis 관련 (해당 서비스)
├── common/             # 서비스 내 공유 객체 (Kafka 이벤트 등)
└── outbox/             # Outbox 패턴 (해당 서비스)
```

### 레이어 책임

- **api**: 외부 요청을 받아 application 레이어로 위임. 엔티티 직접 노출 금지.
- **application**: 유스케이스 구현. 트랜잭션 경계.
- **domain**: 핵심 비즈니스 규칙. 외부 의존성 없음.
- **infrastructure**: DB, Kafka, Redis 등 기술 세부사항 캡슐화.
- **common**: 서비스 내부에서 공유하는 이벤트/DTO.

## 기술 스택

- Java 25, Spring Boot 4.x
- Spring Data JPA, MySQL
- Apache Kafka
- Redis
- Lombok, springdoc-openapi
