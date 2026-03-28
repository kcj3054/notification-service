<template>
  <!-- ============================================================
    StatusFilter 컴포넌트
    역할: 상태 탭(전체/PENDING/SENT/FAILED), 이메일/requestId 검색 입력을 제공합니다.
    부모로부터 현재 필터 값을 받고, 변경 시 이벤트를 emit합니다.
  ============================================================ -->
  <div class="filter-bar">
    <!-- 상태 탭 버튼 -->
    <div class="status-tabs">
      <button
        v-for="tab in statusTabs"
        :key="tab.value"
        :class="['tab-btn', { active: modelStatus === tab.value }]"
        @click="$emit('update:modelStatus', tab.value)"
      >
        {{ tab.label }}
        <span class="badge" :class="tab.value.toLowerCase()">{{ tab.count }}</span>
      </button>
    </div>

    <!-- 검색 입력 -->
    <div class="search-inputs">
      <div class="input-group">
        <label>이메일 검색</label>
        <input
          type="text"
          placeholder="example@email.com"
          :value="modelEmail"
          @input="$emit('update:modelEmail', ($event.target as HTMLInputElement).value)"
        />
      </div>
      <div class="input-group">
        <label>요청 ID 검색</label>
        <input
          type="text"
          placeholder="REQ-001"
          :value="modelRequestId"
          @input="$emit('update:modelRequestId', ($event.target as HTMLInputElement).value)"
        />
      </div>
      <button class="btn-search" @click="$emit('search')">검색</button>
      <button class="btn-reset" @click="onReset">초기화</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { MailStatus } from '../types/mail'

const props = defineProps<{
  modelStatus: MailStatus | ''
  modelEmail: string
  modelRequestId: string
  counts: { total: number; pending: number; sent: number; failed: number }
}>()

const emit = defineEmits<{
  'update:modelStatus': [value: MailStatus | '']
  'update:modelEmail': [value: string]
  'update:modelRequestId': [value: string]
  search: []
  reset: []
}>()

const statusTabs = [
  { value: '' as MailStatus | '', label: '전체', count: props.counts.total },
  { value: 'PENDING' as MailStatus, label: 'PENDING', count: props.counts.pending },
  { value: 'SENT' as MailStatus, label: 'SENT', count: props.counts.sent },
  { value: 'FAILED' as MailStatus, label: 'FAILED', count: props.counts.failed },
]

function onReset() {
  emit('update:modelStatus', '')
  emit('update:modelEmail', '')
  emit('update:modelRequestId', '')
  emit('reset')
}
</script>

<style scoped>
.filter-bar {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.status-tabs {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 6px 14px;
  border: 1px solid #cbd5e1;
  border-radius: 20px;
  background: #f8fafc;
  cursor: pointer;
  font-size: 13px;
  color: #475569;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.15s;
}
.tab-btn:hover { background: #e2e8f0; }
.tab-btn.active {
  background: #1e40af;
  color: #fff;
  border-color: #1e40af;
}

.badge {
  font-size: 11px;
  padding: 1px 6px;
  border-radius: 10px;
  background: #e2e8f0;
  color: #334155;
  font-weight: 600;
}
.tab-btn.active .badge { background: #3b82f6; color: #fff; }
.badge.pending { background: #fef9c3; color: #854d0e; }
.badge.sent { background: #dcfce7; color: #166534; }
.badge.failed { background: #fee2e2; color: #991b1b; }

.search-inputs {
  display: flex;
  gap: 10px;
  align-items: flex-end;
  flex-wrap: wrap;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.input-group label { font-size: 12px; color: #64748b; font-weight: 500; }
.input-group input {
  padding: 7px 10px;
  border: 1px solid #cbd5e1;
  border-radius: 6px;
  font-size: 13px;
  width: 180px;
  outline: none;
  transition: border-color 0.15s;
}
.input-group input:focus { border-color: #3b82f6; }

.btn-search, .btn-reset {
  padding: 7px 16px;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  border: none;
  transition: background 0.15s;
}
.btn-search { background: #1e40af; color: #fff; }
.btn-search:hover { background: #1d4ed8; }
.btn-reset { background: #f1f5f9; color: #475569; border: 1px solid #cbd5e1; }
.btn-reset:hover { background: #e2e8f0; }
</style>
