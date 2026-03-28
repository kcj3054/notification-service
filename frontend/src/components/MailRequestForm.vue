<template>
  <!-- ============================================================
    MailRequestForm 컴포넌트
    역할: 메일 발송 요청 폼을 렌더링하고, 제출 시 API를 호출합니다.
    성공/실패 메시지를 부모에게 emit하여 목록 갱신을 요청합니다.
  ============================================================ -->
  <div class="form-card">
    <h2 class="form-title">메일 발송 요청</h2>

    <form @submit.prevent="onSubmit" novalidate>
      <div class="form-row">
        <div class="field">
          <label for="recipientEmail">수신 이메일 <span class="required">*</span></label>
          <input
            id="recipientEmail"
            v-model="form.recipientEmail"
            type="email"
            placeholder="recipient@example.com"
            :class="{ error: errors.recipientEmail }"
          />
          <p v-if="errors.recipientEmail" class="error-msg">{{ errors.recipientEmail }}</p>
        </div>

        <div class="field">
          <label for="templateType">템플릿 타입</label>
          <select id="templateType" v-model="form.templateType">
            <option value="">선택 안함</option>
            <option value="GENERAL">GENERAL (일반)</option>
            <option value="WELCOME">WELCOME (환영)</option>
            <option value="PASSWORD_RESET">PASSWORD_RESET (비밀번호 재설정)</option>
            <option value="VERIFICATION">VERIFICATION (이메일 인증)</option>
          </select>
        </div>
      </div>

      <div class="field">
        <label for="subject">제목 <span class="required">*</span></label>
        <input
          id="subject"
          v-model="form.subject"
          type="text"
          placeholder="메일 제목을 입력하세요"
          :class="{ error: errors.subject }"
        />
        <p v-if="errors.subject" class="error-msg">{{ errors.subject }}</p>
      </div>

      <div class="field">
        <label for="body">내용 <span class="required">*</span></label>
        <textarea
          id="body"
          v-model="form.body"
          rows="5"
          placeholder="메일 본문을 입력하세요"
          :class="{ error: errors.body }"
        ></textarea>
        <p v-if="errors.body" class="error-msg">{{ errors.body }}</p>
      </div>

      <!-- 알림 메시지 -->
      <div v-if="alertMessage" :class="['alert', alertType]">
        {{ alertMessage }}
      </div>

      <div class="form-actions">
        <button type="button" class="btn-secondary" @click="onReset">초기화</button>
        <button type="submit" class="btn-primary" :disabled="isSubmitting">
          {{ isSubmitting ? '발송 중...' : '발송 요청' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { sendMail } from '../api/mail'
import type { TemplateType } from '../types/mail'

const emit = defineEmits<{
  sent: []  // 발송 요청 성공 시 목록 새로고침을 위해 부모에게 알림
}>()

// 폼 데이터
const form = reactive({
  recipientEmail: '',
  subject: '',
  body: '',
  templateType: '' as TemplateType | '',
})

// 유효성 에러
const errors = reactive({
  recipientEmail: '',
  subject: '',
  body: '',
})

const isSubmitting = ref(false)
const alertMessage = ref('')
const alertType = ref<'success' | 'error'>('success')

// 이메일 형식 체크
function isValidEmail(email: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)
}

// 유효성 검사
function validate(): boolean {
  let valid = true
  errors.recipientEmail = ''
  errors.subject = ''
  errors.body = ''

  if (!form.recipientEmail) {
    errors.recipientEmail = '수신 이메일을 입력하세요.'
    valid = false
  } else if (!isValidEmail(form.recipientEmail)) {
    errors.recipientEmail = '올바른 이메일 형식이 아닙니다.'
    valid = false
  }
  if (!form.subject.trim()) {
    errors.subject = '제목을 입력하세요.'
    valid = false
  }
  if (!form.body.trim()) {
    errors.body = '내용을 입력하세요.'
    valid = false
  }
  return valid
}

async function onSubmit() {
  if (!validate()) return
  isSubmitting.value = true
  alertMessage.value = ''

  try {
    const result = await sendMail({
      recipientEmail: form.recipientEmail,
      subject: form.subject,
      body: form.body,
      templateType: form.templateType || undefined,
    })
    alertType.value = 'success'
    alertMessage.value = `발송 요청 완료! (요청 ID: ${result.requestId})`
    emit('sent')
    onReset()
  } catch (e) {
    alertType.value = 'error'
    alertMessage.value = e instanceof Error ? e.message : '발송 요청 중 오류가 발생했습니다.'
  } finally {
    isSubmitting.value = false
  }
}

function onReset() {
  form.recipientEmail = ''
  form.subject = ''
  form.body = ''
  form.templateType = ''
  errors.recipientEmail = ''
  errors.subject = ''
  errors.body = ''
}
</script>

<style scoped>
.form-card {
  background: #fff;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  padding: 24px;
}

.form-title {
  font-size: 16px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid #f1f5f9;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 5px;
  margin-bottom: 14px;
}

.field label {
  font-size: 13px;
  font-weight: 500;
  color: #374151;
}

.required { color: #ef4444; }

.field input,
.field select,
.field textarea {
  padding: 8px 12px;
  border: 1px solid #d1d5db;
  border-radius: 6px;
  font-size: 13px;
  color: #1e293b;
  outline: none;
  transition: border-color 0.15s;
  font-family: inherit;
}
.field input:focus,
.field select:focus,
.field textarea:focus { border-color: #3b82f6; }
.field input.error,
.field textarea.error { border-color: #ef4444; }
.field textarea { resize: vertical; }

.error-msg {
  font-size: 11px;
  color: #ef4444;
  margin: 0;
}

.alert {
  padding: 10px 14px;
  border-radius: 6px;
  font-size: 13px;
  margin-bottom: 14px;
}
.alert.success { background: #f0fdf4; color: #166534; border: 1px solid #bbf7d0; }
.alert.error { background: #fef2f2; color: #991b1b; border: 1px solid #fecaca; }

.form-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.btn-primary, .btn-secondary {
  padding: 8px 20px;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background 0.15s;
}
.btn-primary { background: #1e40af; color: #fff; }
.btn-primary:hover:not(:disabled) { background: #1d4ed8; }
.btn-primary:disabled { background: #93c5fd; cursor: not-allowed; }
.btn-secondary { background: #f1f5f9; color: #475569; border: 1px solid #cbd5e1; }
.btn-secondary:hover { background: #e2e8f0; }
</style>
