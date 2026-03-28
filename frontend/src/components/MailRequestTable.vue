<template>
  <!-- ============================================================
    MailRequestTable 컴포넌트
    역할: 메일 요청 목록을 테이블로 표시합니다.
    각 행 클릭 시 상세 보기 이벤트를 emit하고,
    FAILED 상태에는 재발송 버튼을 표시합니다.
  ============================================================ -->
  <div class="table-wrapper">
    <!-- 상단 바: 건수 + 자동 새로고침 -->
    <div class="table-toolbar">
      <span class="total-count">총 {{ totalElements }}건</span>
      <div class="refresh-controls">
        <select
          v-model="localInterval"
          :disabled="autoRefresh"
          class="interval-select"
          @change="$emit('update:refreshInterval', localInterval)"
        >
          <option :value="3">3초</option>
          <option :value="5">5초</option>
          <option :value="10">10초</option>
        </select>
        <button
          :class="['btn-refresh-toggle', { active: autoRefresh }]"
          @click="$emit('toggleAutoRefresh')"
        >
          {{ autoRefresh ? '자동 새로고침 중지' : '자동 새로고침 시작' }}
        </button>
        <button class="btn-manual-refresh" @click="$emit('refresh')" :disabled="isLoading">
          {{ isLoading ? '로딩 중...' : '새로고침' }}
        </button>
      </div>
    </div>

    <!-- 로딩 상태 -->
    <div v-if="isLoading && items.length === 0" class="loading-state">
      데이터를 불러오는 중...
    </div>

    <!-- 에러 상태 -->
    <div v-else-if="error" class="error-state">
      {{ error }}
    </div>

    <!-- 빈 상태 -->
    <div v-else-if="items.length === 0" class="empty-state">
      조회된 메일 요청이 없습니다.
    </div>

    <!-- 테이블 -->
    <div v-else class="table-scroll">
      <table>
        <thead>
          <tr>
            <th>요청 ID</th>
            <th>수신 이메일</th>
            <th>제목</th>
            <th>상태</th>
            <th>재시도</th>
            <th>요청일시</th>
            <th>발송일시</th>
            <th>작업</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="item in items"
            :key="item.requestId"
            :class="['data-row', { selected: selectedId === item.requestId }]"
            @click="$emit('select', item)"
          >
            <td class="mono">{{ item.requestId }}</td>
            <td>{{ item.recipientEmail }}</td>
            <td class="subject-cell">{{ item.subject }}</td>
            <td>
              <span :class="['status-badge', item.status.toLowerCase()]">
                {{ item.status }}
              </span>
            </td>
            <td class="center">{{ item.retryCount }}</td>
            <td class="mono datetime">{{ formatDatetime(item.createdAt) }}</td>
            <td class="mono datetime">{{ item.sentAt ? formatDatetime(item.sentAt) : '-' }}</td>
            <td @click.stop>
              <button
                v-if="item.status === 'FAILED'"
                class="btn-retry"
                :disabled="retryingId === item.requestId"
                @click="onRetry(item.requestId)"
              >
                {{ retryingId === item.requestId ? '처리 중...' : '재발송' }}
              </button>
              <span v-else class="no-action">-</span>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { retryMail } from '../api/mail'
import type { MailRequest } from '../types/mail'

const props = defineProps<{
  items: MailRequest[]
  totalElements: number
  isLoading: boolean
  error: string | null
  autoRefresh: boolean
  refreshInterval: number
  selectedId?: string
}>()

const emit = defineEmits<{
  select: [item: MailRequest]
  refresh: []
  toggleAutoRefresh: []
  'update:refreshInterval': [value: number]
  retried: []
}>()

const retryingId = ref<string | null>(null)
const localInterval = ref(props.refreshInterval)

watch(() => props.refreshInterval, (v) => { localInterval.value = v })

async function onRetry(requestId: string) {
  retryingId.value = requestId
  try {
    await retryMail(requestId)
    emit('retried')
  } catch (e) {
    alert(e instanceof Error ? e.message : '재발송 요청 실패')
  } finally {
    retryingId.value = null
  }
}

function formatDatetime(iso: string): string {
  try {
    return new Date(iso).toLocaleString('ko-KR', {
      year: '2-digit', month: '2-digit', day: '2-digit',
      hour: '2-digit', minute: '2-digit', second: '2-digit',
      hour12: false,
    })
  } catch {
    return iso
  }
}
</script>

<style scoped>
.table-wrapper {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  overflow: hidden;
}

.table-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  background: #f8fafc;
}

.total-count { font-size: 13px; color: #475569; font-weight: 600; }

.refresh-controls { display: flex; gap: 8px; align-items: center; }

.interval-select {
  padding: 5px 8px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 12px;
  background: #fff;
  cursor: pointer;
}

.btn-refresh-toggle, .btn-manual-refresh {
  padding: 5px 12px;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  border: 1px solid #d1d5db;
  transition: all 0.15s;
}
.btn-refresh-toggle { background: #f1f5f9; color: #475569; }
.btn-refresh-toggle.active { background: #fef3c7; color: #92400e; border-color: #fcd34d; }
.btn-manual-refresh { background: #fff; color: #374151; }
.btn-manual-refresh:hover:not(:disabled) { background: #f1f5f9; }
.btn-manual-refresh:disabled { opacity: 0.5; cursor: not-allowed; }

.loading-state, .error-state, .empty-state {
  padding: 48px 24px;
  text-align: center;
  font-size: 14px;
  color: #94a3b8;
}
.error-state { color: #ef4444; }

.table-scroll { overflow-x: auto; }

table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

thead th {
  padding: 10px 14px;
  text-align: left;
  background: #f8fafc;
  font-size: 12px;
  font-weight: 600;
  color: #64748b;
  border-bottom: 1px solid #e2e8f0;
  white-space: nowrap;
}

.data-row td {
  padding: 10px 14px;
  border-bottom: 1px solid #f1f5f9;
  vertical-align: middle;
  cursor: pointer;
}
.data-row:last-child td { border-bottom: none; }
.data-row:hover td { background: #f8fafc; }
.data-row.selected td { background: #eff6ff; }

.mono { font-family: 'Courier New', monospace; font-size: 12px; }
.datetime { color: #64748b; white-space: nowrap; }
.center { text-align: center; }
.subject-cell { max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  white-space: nowrap;
}
.status-badge.pending { background: #fef9c3; color: #854d0e; }
.status-badge.sent { background: #dcfce7; color: #166534; }
.status-badge.failed { background: #fee2e2; color: #991b1b; }

.btn-retry {
  padding: 4px 10px;
  background: #fef2f2;
  color: #991b1b;
  border: 1px solid #fecaca;
  border-radius: 5px;
  font-size: 12px;
  cursor: pointer;
  white-space: nowrap;
  transition: background 0.15s;
}
.btn-retry:hover:not(:disabled) { background: #fee2e2; }
.btn-retry:disabled { opacity: 0.6; cursor: not-allowed; }

.no-action { color: #cbd5e1; font-size: 12px; }
</style>
