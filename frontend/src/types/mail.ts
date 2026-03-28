// ============================================================
// 타입 정의 파일
// 백엔드 API의 요청/응답 형태를 TypeScript 타입으로 정의합니다.
// 백엔드 DTO가 바뀌면 이 파일을 수정하면 됩니다.
// ============================================================

/** 메일 상태값 */
export type MailStatus = 'PENDING' | 'SENT' | 'FAILED'

/** 템플릿 타입 (백엔드에서 지원하는 값으로 변경) */
export type TemplateType = 'GENERAL' | 'WELCOME' | 'PASSWORD_RESET' | 'VERIFICATION'

/** 메일 요청 단건 */
export interface MailRequest {
  requestId: string
  recipientEmail: string
  subject: string
  body: string
  templateType?: TemplateType
  status: MailStatus
  retryCount: number
  createdAt: string   // ISO 8601 형식 (예: "2024-03-01T10:00:00Z")
  sentAt?: string     // 발송 완료 시각, 미발송 시 null
  errorMessage?: string // 실패 시 에러 메시지
}

/** 메일 발송 요청 Body (POST /api/v1/mail) */
export interface SendMailRequest {
  recipientEmail: string
  subject: string
  body: string
  templateType?: TemplateType
}

/** 메일 발송 응답 (POST /api/v1/mail) */
export interface SendMailResponse {
  requestId: string
  status: MailStatus
  createdAt: string
}

/** 목록 조회 쿼리 파라미터 (GET /api/v1/mail) */
export interface MailListQuery {
  status?: MailStatus | ''
  recipientEmail?: string
  requestId?: string
  page?: number
  size?: number
}

/** 목록 조회 응답 (페이지네이션) */
export interface MailListResponse {
  content: MailRequest[]
  totalElements: number
  totalPages: number
  page: number
  size: number
}
