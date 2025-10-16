<template>
  <div class="reset-password-container">
    <div class="form-wrapper">
      <!-- 로고 섹션: 왼쪽 정렬 -->
      <div class="logo-section">
        <!-- 실제 로고 이미지 경로로 변경하세요. -->
        <div class="logo-text">로고 사진</div>
      </div>

      <!-- 비밀번호 재설정 폼 -->
      <form @submit.prevent="completeReset">
        <h1 class="form-title">비밀번호 재설정</h1>

        <!-- 새 비밀번호 입력칸 -->
        <div class="input-group">
          <label for="new-password" class="input-label" :class="{ 'error-text': isPasswordMismatch || !isPasswordValid }">
            새 비밀번호
          </label>
          <input
            type="password"
            id="new-password"
            v-model="formData.newPassword"
            placeholder="새 비밀번호를 입력하세요"
            class="input-field"
            :class="{ 'error-border': isPasswordMismatch || !isPasswordValid }"
            @input="validatePassword"
            required
          />
        </div>

        <!-- 새 비밀번호 재입력칸 -->
        <div class="input-group">
          <label for="confirm-password" class="input-label" :class="{ 'error-text': isPasswordMismatch }">
            새 비밀번호 재입력
          </label>
          <input
            type="password"
            id="confirm-password"
            v-model="formData.confirmPassword"
            placeholder="새 비밀번호를 다시 입력하세요"
            class="input-field"
            :class="{ 'error-border': isPasswordMismatch }"
            @input="checkPasswordMatch"
            required
          />
        </div>

        <!-- 비밀번호 규칙 및 경고 메시지 -->
        <div class="password-rules-section">
          <p class="rule-text" :class="{ 'error-text': !isPasswordValid }">
            1. 8~16자의 영문 대소문자, 숫자, 특수문자만 가능합니다.<br />
            (사용 가능한 특수문자: )
          </p>
          <p class="warning-text" v-if="isPasswordMismatch">
            입력된 비밀번호가 일치하지 않습니다. 다시 입력해주세요.
          </p>
        </div>

        <!-- 완료 버튼 -->
        <button
          type="submit"
          :disabled="!isFormValid"
          class="submit-button"
        >
          완료
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

// 폼 데이터 상태 관리
const formData = ref({
  newPassword: '',
  confirmPassword: '',
});

// 유효성 검사 상태
const isPasswordMismatch = ref(false);
const isPasswordValid = ref(true);

// 비밀번호 유효성 검사 함수
const validatePassword = () => {
  const password = formData.value.newPassword;
  const passwordRegex = /^(?=.*[a-zA-Z])(?=.*\d).{8,16}$/;
  
  isPasswordValid.value = passwordRegex.test(password);
  checkPasswordMatch();
};

// 비밀번호 일치 확인 함수
const checkPasswordMatch = () => {
  if (formData.value.newPassword && formData.value.confirmPassword) {
    isPasswordMismatch.value = formData.value.newPassword !== formData.value.confirmPassword;
  } else {
    isPasswordMismatch.value = false;
  }
};

// 폼 유효성 최종 확인
const isFormValid = computed(() => {
  return (
    formData.value.newPassword &&
    formData.value.confirmPassword &&
    !isPasswordMismatch.value &&
    isPasswordValid.value
  );
});

// 완료 버튼 클릭 핸들러
const completeReset = () => {
  if (isFormValid.value) {
    console.log('비밀번호 재설정이 완료되었습니다. 메인 페이지로 이동합니다.');
    // 실제 라우팅 로직을 여기에 추가하세요.
    // 예: router.push('/main-page');
    alert('비밀번호 재설정이 완료되었습니다. 메인 페이지로 이동합니다.');
  } else {
    alert('비밀번호를 다시 확인해주세요.');
  }
};
</script>

<style scoped>
/* 전체 컨테이너 */
.reset-password-container {
  min-height: 100vh;
  background-color: #ffffff; /* 하얀 배경색 */
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  font-family: Arial, sans-serif;
}

/* 폼 래퍼 */
.form-wrapper {
  width: 100%;
  max-width: 42rem;
  background-color: #ffffff;
  border: 2px solid #f2f2f2; /* 연회색 테두리 */
  border-radius: 0.75rem;
  padding: 2rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

/* 로고 섹션 */
.logo-section {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 1.5rem;
}

.logo-text {
  font-size: 1.5rem;
  font-weight: bold;
  color: #1f2937;
}

/* 폼 제목 */
.form-title {
  font-size: 1.5rem;
  font-weight: bold;
  text-align: center;
  color: #1f2937;
  margin-bottom: 2rem;
}

/* 입력 그룹 (라벨과 입력 필드) */
.input-group {
  margin-bottom: 1rem;
}

.input-label {
  display: block;
  font-size: 0.875rem;
  font-weight: 500;
  color: #374151;
  margin-bottom: 0.25rem;
  transition: color 0.2s ease-in-out;
}

/* 입력 필드 (input) */
.input-field {
  display: block;
  width: 100%;
  padding: 0.75rem;
  border-radius: 0.375rem;
  border: 2px solid #f2f2f2;
  font-size: 0.875rem;
  color: #1f2937;
  transition: border-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

/* 포커스 시 스타일 */
.input-field:focus {
  outline: none;
  border-color: #4ca7cc;
  box-shadow: 0 0 0 3px rgba(76, 167, 204, 0.25);
}

/* 비밀번호 규칙 및 경고 메시지 섹션 */
.password-rules-section {
  margin-bottom: 1.5rem;
}

.rule-text {
  font-size: 0.75rem;
  color: #a0a0a0; /* 연회색 */
  line-height: 1.5;
  transition: color 0.2s ease-in-out;
}

.warning-text {
  font-size: 0.75rem;
  color: #ef4444; /* 빨간색 경고 메시지 */
  margin-top: 0.5rem;
  font-weight: 500;
}

/* 유효성 검사 실패 시 스타일 */
.error-text {
  color: #ef4444 !important;
}

.error-border {
  border-color: #ef4444 !important;
}

/* 완료 버튼 */
.submit-button {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 0.75rem 1rem;
  border-radius: 0.375rem;
  color: #ffffff;
  background-color: #4ca7cc; /* 진한 하늘색 */
  font-size: 0.875rem;
  font-weight: 500;
  transition: background-color 0.15s ease-in-out;
}

.submit-button:hover:not(:disabled) {
  background-color: #3d83a1;
}

.submit-button:disabled {
  background-color: #d1d5db; /* 비활성화 상태일 때 회색 */
  cursor: not-allowed;
}

/* 반응형 디자인 */
@media (max-width: 640px) {
  .form-wrapper {
    padding: 1rem;
  }
}
</style>
