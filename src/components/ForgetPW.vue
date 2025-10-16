<template>
  <div class="reset-password-container">
    <div class="form-wrapper">
      <!-- 로고 섹션: 왼쪽 정렬 -->
      <div class="logo-section">
        <!-- 실제 로고 이미지 경로로 변경하세요. -->
        <div class="logo-text">로고 사진</div>
      </div>

      <!-- 비밀번호 찾기 폼 -->
      <form @submit.prevent="resetPassword">
        <h1 class="form-title">비밀번호 찾기</h1>

        <!-- 이름 입력칸 -->
        <div class="input-group">
          <label for="name" class="input-label">이름</label>
          <input
            type="text"
            id="name"
            v-model="formData.name"
            placeholder="이름을 입력하세요"
            class="input-field"
            required
          />
        </div>

        <!-- 이메일 입력칸 | 이메일 인증 버튼 -->
        <div class="input-group">
          <label for="email" class="input-label">이메일</label>
          <div class="input-with-button">
            <input
              type="email"
              id="email"
              v-model="formData.email"
              placeholder="example@example.com"
              class="input-field"
              :disabled="isEmailVerified"
              required
            />
            <button
              type="button"
              @click="sendVerificationCode"
              :disabled="!formData.email || isEmailVerified"
              class="verify-button"
            >
              인증하기
            </button>
          </div>
        </div>

        <!-- 이메일 인증번호 입력칸 (인증번호 발송 후 표시) -->
        <div class="input-group" v-if="isCodeSent">
          <label for="verification-code" class="input-label">이메일 인증번호</label>
          <input
            type="text"
            id="verification-code"
            v-model="formData.verificationCode"
            placeholder="인증번호를 입력하세요"
            class="input-field"
            :disabled="isEmailVerified"
            required
          />
        </div>

        <!-- 비밀번호 재설정 버튼 -->
        <button
          type="submit"
          :disabled="!isFormValid"
          class="submit-button"
        >
          비밀번호 재설정
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

// 폼 데이터 상태 관리
const formData = ref({
  name: '',
  email: '',
  verificationCode: '',
});

// 인증 관련 상태
const isCodeSent = ref(false);
const isEmailVerified = ref(false);

// 폼 유효성 검사 (모든 필수 필드와 이메일 인증 상태 확인)
const isFormValid = computed(() => {
  return (
    formData.value.name &&
    formData.value.email &&
    isEmailVerified.value
  );
});

// 이메일 인증번호 발송 (실제 로직을 여기에 구현)
const sendVerificationCode = () => {
  console.log(`${formData.value.email}로 인증번호를 보냅니다.`);
  isCodeSent.value = true;
  // 실제 API 호출로 인증번호를 보내는 로직을 구현해야 합니다.
  // 이 예제에서는 시뮬레이션으로 3초 후 인증 완료 상태로 변경합니다.
  setTimeout(() => {
    isEmailVerified.value = true;
    console.log('이메일 인증이 완료되었습니다.');
  }, 3000);
};

// 비밀번호 재설정 버튼 클릭 핸들러
const resetPassword = () => {
  if (isFormValid.value) {
    console.log('비밀번호 재설정 페이지로 이동합니다.');
    // 이 부분에 비밀번호 재설정 페이지로 이동하는 라우팅 로직을 추가하세요.
    // 예: router.push('/reset-password-page');
    alert('비밀번호 재설정 페이지로 이동합니다.');
  } else {
    alert('모든 정보를 올바르게 입력하고 이메일 인증을 완료해주세요.');
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
}

/* 입력 필드 (input, select) */
.input-field {
  display: block;
  width: 100%;
  padding: 0.75rem;
  border-radius: 0.375rem;
  border: 2px solid #f2f2f2;
  font-size: 0.875rem;
  color: #1f2937;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

/* 포커스 시 스타일 */
.input-field:focus {
  outline: none;
  border-color: #4ca7cc;
  box-shadow: 0 0 0 3px rgba(76, 167, 204, 0.25);
}

/* 이메일 입력 그룹 */
.input-with-button {
  display: flex;
}

.input-with-button .input-field {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

/* 이메일 인증 버튼 */
.verify-button {
  padding: 0.75rem 1rem;
  border-radius: 0.375rem;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  background-color: #88d4ff; /* 하늘색 */
  color: #ffffff;
  font-size: 0.875rem;
  font-weight: 500;
  transition: background-color 0.15s ease-in-out;
  white-space: nowrap;
}

.verify-button:hover:not(:disabled) {
  background-color: #62cff6;
}

.verify-button:disabled {
  background-color: #d1d5db;
  cursor: not-allowed;
}

/* 비밀번호 재설정 버튼 */
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
