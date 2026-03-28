<template>
  <!-- ============================================================
    App.vue - 루트 컴포넌트
    역할: 전체 레이아웃을 조합하고, 컴포넌트 간 상태를 연결합니다.
    구조: 헤더 / 좌측(폼+필터+테이블) / 우측(상세 패널)
  ============================================================ -->
  <div class="app-layout">
    <!-- 헤더 -->
    <header class="app-header">
      <div class="header-inner">
        <div class="logo">
          <span class="logo-icon">✉</span>
          <span class="logo-text">Mail Admin</span>
          <span class="logo-sub">메일 발송 관리 시스템</span>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <span class="stat-label">전체</span>
            <span class="stat-value">{{ stats.total }}</span>
          </div>
          <div class="stat-item pending">
            <span class="stat-label">PENDING</span>
            <span class="stat-value">{{ stats.pending }}</span>
          </div>
          <div class="stat-item sent">
            <span class="stat-label">SENT</span>
            <span class="stat-value">{{ stats.sent }}</span>
          </div>
          <div class="stat-item failed">
            <span class="stat-label">FAILED</span>
            <span class="stat-value">{{ stats.failed }}</span>
          </div>
        </div>
      </div>
    </header>

    <!-- 메인 컨텐츠 -->
    <main class="app-main">
      <!-- 좌측 영역 -->
      <div class="content-left">
        <!-- 메일 발송 폼 -->
        <MailRequestForm @sent="onSent" />

        <!-- 상태 필터 -->
        <StatusFilter
          v-model:modelStatus="filterStatus"
          v-model:modelEmail="filterEmail"
          v-model:modelRequestId="filterRequestId"
          :counts="stats"
          @search="load"
          @reset="load"
        />

        <!-- 목록 테이블 -->
        <MailRequestTable
          :items="items"
          :total-elements="totalElements"
          :is-loading="isLoading"
          :error="error"
          :auto-refresh="autoRefresh"
          :refresh-interval="refreshInterval"
          :selected-id="selectedItem?.requestId"
          @select="onSelectItem"
          @refresh="load"
          @toggle-auto-refresh="toggleAutoRefresh"
          @update:refresh-interval="updateRefreshInterval"
          @retried="load"
        />
      </div>

      <!-- 우측 상세 패널 -->
      <aside class="content-right">
        <MailRequestDetail
          :item="selectedItem"
          @close="selectedItem = null"
          @retried="load"
        />
      </aside>
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import MailRequestForm from './components/MailRequestForm.vue'
import MailRequestTable from './components/MailRequestTable.vue'
import MailRequestDetail from './components/MailRequestDetail.vue'
import StatusFilter from './components/StatusFilter.vue'
import { useMailList } from './composables/useMailList'
import type { MailRequest } from './types/mail'

// 목록 상태 및 필터 로직 (composable에서 가져옴)
const {
  items,
  totalElements,
  isLoading,
  error,
  filterStatus,
  filterEmail,
  filterRequestId,
  autoRefresh,
  refreshInterval,
  stats,
  load,
  toggleAutoRefresh,
  updateRefreshInterval,
} = useMailList()

// 선택된 상세 항목
const selectedItem = ref<MailRequest | null>(null)

// 필터 변경 시 자동 검색
watch([filterStatus], () => { load() })

// 항목 선택
function onSelectItem(item: MailRequest) {
  selectedItem.value = selectedItem.value?.requestId === item.requestId ? null : item
}

// 발송 성공 시 목록 새로고침
function onSent() {
  load()
}

// 초기 로드
onMounted(() => { load() })
</script>

<style>
/* 전역 스타일 */
*, *::before, *::after { box-sizing: border-box; }

body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Noto Sans KR', sans-serif;
  background: #f1f5f9;
  color: #1e293b;
  font-size: 14px;
  line-height: 1.5;
}

.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 헤더 */
.app-header {
  background: #1e293b;
  color: #fff;
  padding: 0 24px;
  height: 56px;
  flex-shrink: 0;
}
.header-inner {
  max-width: 1400px;
  margin: 0 auto;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo { display: flex; align-items: center; gap: 10px; }
.logo-icon { font-size: 20px; }
.logo-text { font-size: 16px; font-weight: 700; }
.logo-sub { font-size: 12px; color: #94a3b8; }

.header-stats { display: flex; gap: 20px; }
.stat-item { display: flex; flex-direction: column; align-items: center; }
.stat-label { font-size: 10px; color: #94a3b8; text-transform: uppercase; letter-spacing: 0.05em; }
.stat-value { font-size: 18px; font-weight: 700; color: #fff; }
.stat-item.pending .stat-value { color: #fbbf24; }
.stat-item.sent .stat-value { color: #34d399; }
.stat-item.failed .stat-value { color: #f87171; }

/* 메인 영역 */
.app-main {
  flex: 1;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
  padding: 20px 24px;
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
  align-items: start;
}

.content-left {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-width: 0;
}

.content-right {
  position: sticky;
  top: 20px;
  height: calc(100vh - 96px);
}

/* 반응형 */
@media (max-width: 900px) {
  .app-main {
    grid-template-columns: 1fr;
    padding: 16px;
  }
  .content-right {
    position: static;
    height: auto;
    min-height: 300px;
  }
  .header-stats { display: none; }
}
</style>
