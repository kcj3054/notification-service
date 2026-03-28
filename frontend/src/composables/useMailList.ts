// ============================================================
// useMailList Composable
// 메일 목록 조회, 필터, 자동 새로고침 로직을 담당합니다.
// Vue 컴포넌트에서 상태 로직을 분리하여 재사용 가능하게 만듭니다.
// ============================================================

import { ref, computed, onUnmounted } from 'vue'
import { fetchMailList } from '../api/mail'
import type { MailRequest, MailStatus, MailListQuery } from '../types/mail'

export function useMailList() {
  // --- 상태 ---
  const items = ref<MailRequest[]>([])
  const totalElements = ref(0)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // 필터 상태
  const filterStatus = ref<MailStatus | ''>('')
  const filterEmail = ref('')
  const filterRequestId = ref('')

  // 자동 새로고침 상태
  const autoRefresh = ref(false)
  const refreshInterval = ref(5) // 초 단위
  let intervalId: ReturnType<typeof setInterval> | null = null

  // --- 조회 ---
  async function load() {
    isLoading.value = true
    error.value = null
    try {
      const query: MailListQuery = {
        status: filterStatus.value || undefined,
        recipientEmail: filterEmail.value || undefined,
        requestId: filterRequestId.value || undefined,
        page: 0,
        size: 50,
      }
      const result = await fetchMailList(query)
      items.value = result.content
      totalElements.value = result.totalElements
    } catch (e) {
      error.value = e instanceof Error ? e.message : '목록 조회 중 오류가 발생했습니다.'
    } finally {
      isLoading.value = false
    }
  }

  // --- 자동 새로고침 토글 ---
  function toggleAutoRefresh() {
    autoRefresh.value = !autoRefresh.value
    if (autoRefresh.value) {
      intervalId = setInterval(load, refreshInterval.value * 1000)
    } else {
      if (intervalId) {
        clearInterval(intervalId)
        intervalId = null
      }
    }
  }

  function updateRefreshInterval(seconds: number) {
    refreshInterval.value = seconds
    if (autoRefresh.value) {
      // 인터벌 재설정
      if (intervalId) clearInterval(intervalId)
      intervalId = setInterval(load, seconds * 1000)
    }
  }

  // 컴포넌트 언마운트 시 인터벌 정리
  onUnmounted(() => {
    if (intervalId) clearInterval(intervalId)
  })

  // --- 통계 (computed) ---
  const stats = computed(() => ({
    total: totalElements.value,
    pending: items.value.filter((m) => m.status === 'PENDING').length,
    sent: items.value.filter((m) => m.status === 'SENT').length,
    failed: items.value.filter((m) => m.status === 'FAILED').length,
  }))

  return {
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
  }
}
