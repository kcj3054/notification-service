// ============================================================
// API 연동 레이어
// 실제 백엔드와 통신하는 함수들을 모아놓은 파일입니다.
//
// [사용법]
// - 실제 백엔드 연결 시: USE_MOCK = false, BASE_URL 확인
// - Mock 데이터 테스트 시: USE_MOCK = true
// ============================================================

import axios from 'axios'
import type {
  MailRequest,
  SendMailRequest,
  SendMailResponse,
  MailListQuery,
  MailListResponse,
} from '../types/mail'

// ✅ 백엔드 URL 변경 포인트 - 실제 서버 주소로 바꾸세요
const BASE_URL = '/api/v1/mail'

// ✅ Mock 모드 전환 스위치 (true = 가짜 데이터 사용, false = 실제 API 호출)
const USE_MOCK = true

// ------------------------------------------------------------
// Axios 인스턴스
// ------------------------------------------------------------
const apiClient = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: { 'Content-Type': 'application/json' },
})

// ------------------------------------------------------------
// Mock 데이터 (USE_MOCK = true 일 때 사용)
// 실제 백엔드 연결 후에는 이 섹션 전체를 삭제해도 됩니다.
// ------------------------------------------------------------
let mockData: MailRequest[] = [
  {
    requestId: 'REQ-001',
    recipientEmail: 'alice@example.com',
    subject: '회원가입을 환영합니다',
    body: '안녕하세요, Alice님! 회원가입이 완료되었습니다.',
    templateType: 'WELCOME',
    status: 'SENT',
    retryCount: 0,
    createdAt: '2024-03-01T09:00:00Z',
    sentAt: '2024-03-01T09:00:05Z',
  },
  {
    requestId: 'REQ-002',
    recipientEmail: 'bob@example.com',
    subject: '비밀번호 재설정 안내',
    body: '비밀번호 재설정 링크입니다: https://example.com/reset/token123',
    templateType: 'PASSWORD_RESET',
    status: 'PENDING',
    retryCount: 0,
    createdAt: '2024-03-01T10:30:00Z',
  },
  {
    requestId: 'REQ-003',
    recipientEmail: 'charlie@example.com',
    subject: '이메일 인증',
    body: '이메일 인증 코드: 482910',
    templateType: 'VERIFICATION',
    status: 'FAILED',
    retryCount: 3,
    createdAt: '2024-03-01T11:00:00Z',
    errorMessage: 'SMTP 서버 연결 실패: Connection timeout',
  },
  {
    requestId: 'REQ-004',
    recipientEmail: 'dave@example.com',
    subject: '공지사항 안내',
    body: '서비스 점검 예정 안내입니다.',
    templateType: 'GENERAL',
    status: 'SENT',
    retryCount: 1,
    createdAt: '2024-03-01T12:00:00Z',
    sentAt: '2024-03-01T12:01:30Z',
  },
  {
    requestId: 'REQ-005',
    recipientEmail: 'eve@example.com',
    subject: '주문 확인 메일',
    body: '주문이 접수되었습니다. 주문번호: #ORD-20240301-005',
    status: 'FAILED',
    retryCount: 2,
    createdAt: '2024-03-01T13:00:00Z',
    errorMessage: '수신 서버 거부: 554 Spam detected',
  },
]

let mockIdCounter = 6

function mockDelay(ms = 300): Promise<void> {
  return new Promise((resolve) => setTimeout(resolve, ms))
}

// ------------------------------------------------------------
// API 함수들
// ------------------------------------------------------------

/** 메일 발송 요청 */
export async function sendMail(request: SendMailRequest): Promise<SendMailResponse> {
  if (USE_MOCK) {
    await mockDelay()
    const newItem: MailRequest = {
      requestId: `REQ-${String(mockIdCounter++).padStart(3, '0')}`,
      ...request,
      status: 'PENDING',
      retryCount: 0,
      createdAt: new Date().toISOString(),
    }
    mockData.unshift(newItem)
    return { requestId: newItem.requestId, status: 'PENDING', createdAt: newItem.createdAt }
  }

  const response = await apiClient.post<SendMailResponse>('', request)
  return response.data
}

/** 메일 목록 조회 */
export async function fetchMailList(query: MailListQuery = {}): Promise<MailListResponse> {
  if (USE_MOCK) {
    await mockDelay()
    let filtered = [...mockData]

    // 상태 필터
    if (query.status) {
      filtered = filtered.filter((m) => m.status === query.status)
    }
    // 이메일 검색
    if (query.recipientEmail) {
      filtered = filtered.filter((m) =>
        m.recipientEmail.toLowerCase().includes(query.recipientEmail!.toLowerCase())
      )
    }
    // requestId 검색
    if (query.requestId) {
      filtered = filtered.filter((m) =>
        m.requestId.toLowerCase().includes(query.requestId!.toLowerCase())
      )
    }

    const page = query.page ?? 0
    const size = query.size ?? 20
    const start = page * size
    const content = filtered.slice(start, start + size)

    return {
      content,
      totalElements: filtered.length,
      totalPages: Math.ceil(filtered.length / size),
      page,
      size,
    }
  }

  const response = await apiClient.get<MailListResponse>('', { params: query })
  return response.data
}

/** 메일 단건 조회 */
export async function fetchMailDetail(requestId: string): Promise<MailRequest> {
  if (USE_MOCK) {
    await mockDelay(200)
    const item = mockData.find((m) => m.requestId === requestId)
    if (!item) throw new Error(`요청 ID를 찾을 수 없습니다: ${requestId}`)
    return { ...item }
  }

  const response = await apiClient.get<MailRequest>(`/${requestId}`)
  return response.data
}

/** 실패 건 재발송 */
export async function retryMail(requestId: string): Promise<{ requestId: string; status: string }> {
  if (USE_MOCK) {
    await mockDelay(500)
    const item = mockData.find((m) => m.requestId === requestId)
    if (!item) throw new Error(`요청 ID를 찾을 수 없습니다: ${requestId}`)
    item.status = 'PENDING'
    item.retryCount += 1
    item.errorMessage = undefined
    return { requestId, status: 'PENDING' }
  }

  const response = await apiClient.post<{ requestId: string; status: string }>(
    `/${requestId}/retry`
  )
  return response.data
}
