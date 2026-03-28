<template>
  <!-- ============================================================
    MailRequestDetail 컴포넌트
    역할: 테이블에서 선택한 메일 요청의 상세 정보를 우측 패널로 표시합니다.
    선택된 항목이 없으면 안내 메시지를 표시합니다.
  ============================================================ -->
  <div class="detail-panel">
    <div class="panel-header">
      <h3>상세 정보</h3>
      <button v-if="item" class="btn-close" @click="$emit('close')">✕</button>
    </div>

    <!-- 선택 없음 -->
    <div v-if="!item" class="empty-detail">
      <p>테이블에서 항목을 클릭하면<br />상세 정보가 표시됩니다.</p>
    </div>

    <!-- 상세 내용 -->
    <div v-else class="detail-content">
      <!-- 상태 헤더 -->
      <div class="detail-status-row">
        <span :class="['status-badge', item.status.toLowerCase()]">{{ item.status }}</span>
        <span class="request-id mono">{{ item.requestId }}</span>
      </div>

      <!-- 기본 정보 -->
      <section class="detail-section">
        <h4>기본 정보</h4>
        <dl>
          <dt>수신 이메일</dt>
          <dd>{{ item.recipientEmail }}</dd>

          <dt>제목</dt>
          <dd>{{ item.subject }}</dd>

          <dt>템플릿 타입</dt>
          <dd>{{ item.templateType ?? '없음' }}</dd>

          <dt>재시도 횟수</dt>
          <dd>{{ item.retryCount }}회</dd>
        </dl>
      </section>

      <!-- 본문 -->
      <section class="detail-section">
        <h4>본문</h4>
        <div class="body-preview">{{ item.body }}</div>
      </section>

      <!-- 시간 정보 -->
      <section class="detail-section">
        <h4>타임스탬프</h4>
        <dl>
          <dt>요청 일시</dt>
          <dd class="mono">{{ formatDatetime(item.createdAt) }}</dd>
          <dt>발송 일시</dt>
          <dd class="mono">{{ item.sentAt ? formatDatetime(item.sentAt) : '미발송' }}</dd>
        </dl>
      </section>

      <!-- 에러 메시지 (FAILED 시) -->
      <section v-if="item.errorMessage" class="detail-section">
        <h4>에러 메시지</h4>
        <div class="error-box">{{ item.errorMessage }}</div>
      </section>

      <!-- 재발송 버튼 -->
      <div v-if="item.status === 'FAILED'" class="retry-action">
        <button
          class="btn-retry-full"
          :disabled="isRetrying"
          @click="onRetry"
        >
          {{ isRetrying ? '재발송 중...' : '재발송 요청' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { retryMail } from '../api/mail'
import type { MailRequest } from '../types/mail'

const props = defineProps<{
  item: MailRequest | null
}>()

const emit = defineEmits<{
  close: []
  retried: []
}>()

const isRetrying = ref(false)

async function onRetry() {
  if (!props.item) return
  isRetrying.value = true
  try {
    await retryMail(props.item.requestId)
    emit('retried')
  } catch (e) {
    alert(e instanceof Error ? e.message : '재발송 요청 실패')
  } finally {
    isRetrying.value = false
  }
}

function formatDatetime(iso: string): string {
  try {
    return new Date(iso).toLocaleString('ko-KR', {
      year: 'numeric', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit', second: '2-digit',
      hour12: false,
    })
  } catch {
    return iso
  }
}
</script>

<style scoped>
.detail-panel {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: #f8fafc;
}
.panel-header h3 { margin: 0; font-size: 14px; font-weight: 600; color: #1e293b; }

.btn-close {
  background: none;
  border: none;
  cursor: pointer;
  color: #94a3b8;
  font-size: 16px;
  padding: 2px 6px;
  border-radius: 4px;
  transition: background 0.15s;
}
.btn-close:hover { background: #f1f5f9; color: #475569; }

.empty-detail {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
  line-height: 1.6;
  padding: 24px;
}

.detail-content {
  padding: 16px;
  overflow-y: auto;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-status-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.request-id {
  font-size: 13px;
  color: #64748b;
}

.detail-section h4 {
  font-size: 12px;
  font-weight: 600;
  color: #94a3b8;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin: 0 0 8px 0;
}

dl {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 6px 12px;
  margin: 0;
}
dt {
  font-size: 12px;
  color: #64748b;
  white-space: nowrap;
  font-weight: 500;
  align-self: start;
  padding-top: 1px;
}
dd {
  font-size: 13px;
  color: #1e293b;
  margin: 0;
  word-break: break-all;
}

.body-preview {
  background: #f8fafc;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 13px;
  color: #374151;
  white-space: pre-wrap;
  word-break: break-all;
  max-height: 150px;
  overflow-y: auto;
  line-height: 1.5;
}

.error-box {
  background: #fef2f2;
  border: 1px solid #fecaca;
  border-radius: 6px;
  padding: 10px 12px;
  font-size: 12px;
  color: #991b1b;
  font-family: 'Courier New', monospace;
  white-space: pre-wrap;
  word-break: break-all;
}

.status-badge {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}
.status-badge.pending { background: #fef9c3; color: #854d0e; }
.status-badge.sent { background: #dcfce7; color: #166534; }
.status-badge.failed { background: #fee2e2; color: #991b1b; }

.mono { font-family: 'Courier New', monospace; font-size: 12px; }

.retry-action { padding-top: 4px; }
.btn-retry-full {
  width: 100%;
  padding: 9px;
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.15s;
}
.btn-retry-full:hover:not(:disabled) { background: #fee2e2; }
.btn-retry-full:disabled { opacity: 0.6; cursor: not-allowed; }
</style>
