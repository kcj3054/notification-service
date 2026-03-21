# 읽음 처리 플로우

## API
`PATCH /api/v1/notifications/{id}/read`

## Sequence Diagram

```mermaid
sequenceDiagram
    actor User as 사용자 (Web/Mobile)
    participant Client as 클라이언트
    participant Controller as NotificationController
    participant Service as NotificationCommandService
    participant DB as MySQL (notification)

    User->>Client: 알림 클릭 or 스와이프
    Client->>Controller: PATCH /api/v1/notifications/{id}/read
    Controller->>Service: markAsRead(id)
    Service->>DB: SELECT * FROM notification WHERE id = {id}
    DB-->>Service: Notification 엔티티 반환

    alt readAt == null (아직 안 읽음)
        Service->>DB: UPDATE notification SET read_at = NOW() WHERE id = {id}
        DB-->>Service: 성공
        Service-->>Controller: 성공
        Controller-->>Client: 200 OK
    else readAt != null (이미 읽음)
        Service-->>Controller: 이미 읽음
        Controller-->>Client: 200 OK (멱등성 보장)
    end

    Client-->>User: 읽음 UI 반영
```

## 처리 규칙

- `read_at`이 `null`이면 → 현재 시각으로 업데이트
- `read_at`이 이미 있으면 → 그냥 200 반환 (중복 요청 허용)
- 본인 알림이 아닌 경우 → 403 Forbidden (추후 인증 연동 시 처리)

## 클라이언트 동작

읽음 처리는 클라이언트 단에서 선처리 함
