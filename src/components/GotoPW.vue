<template>
  <div class="reset-password-container">
    <div class="form-wrapper">
      <!-- 로고 섹션 -->
      <div class="logo-section">
        <img 
          src="@/assets/logo.png" 
          alt="로고" 
          class="logo" 
          @click="goToMain"
          style="cursor: pointer;"
        />
      </div>

      <!-- 비밀번호 재설정 폼 -->
      <form @submit.prevent="completeReset">
        <h1 class="form-title">비밀번호 재설정</h1>

        <!-- 새 비밀번호 입력칸 -->
        <div class="input-group">
          <label for="new-password" class="input-label" :class="{ 'error-text': errors.password }">
            새 비밀번호
          </label>
          <input
            type="password"
            id="new-password"
            v-model="formData.password"
            placeholder="새 비밀번호를 입력하세요"
            class="input-field"
            :class="{ 'error-border': errors.password }"
            @input="validatePassword"
            required
          />
        </div>
        <span v-if="errors.password" class="error-msg">{{ errors.password }}</span>

        <!-- 새 비밀번호 재입력칸 -->
        <div class="input-group">
          <label for="confirm-password" class="input-label" :class="{ 'error-text': errors.passwordConfirm }">
            새 비밀번호 재입력
          </label>
          <input
            type="password"
            id="confirm-password"
            v-model="formData.passwordConfirm"
            placeholder="새 비밀번호를 다시 입력하세요"
            class="input-field"
            :class="{ 'error-border': errors.passwordConfirm }"
            @input="validatePasswordConfirm"
            required
          />
        </div>
        <span v-if="errors.passwordConfirm" class="error-msg">{{ errors.passwordConfirm }}</span>

        <!-- 비밀번호 규칙 -->
        <div class="password-rules-section">
          <p class="rule-text" :class="{ 'error-text': errors.password }">
            1. 8~16자의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.<br />
            2. 같은 문자가 3회 이상 연속될 수 없습니다.<br />
            3. 이메일(ID)과 동일할 수 없습니다.
          </p>
          <p class="warning-text" v-if="successMessage">{{ successMessage }}</p>
          <p class="warning-text" v-if="errorMessage">{{ errorMessage }}</p>
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
import { useRouter, useRoute } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const route = useRoute();

// 폼 상태
const formData = ref({
  password: '',
  passwordConfirm: '', 
});

// 에러 상태
const errors = ref({
  password: '',
  passwordConfirm: ''
});

const successMessage = ref('');
const errorMessage = ref('');

// ----------------------------------------------------
// 1. 로고 클릭 시 메인으로 이동
// ----------------------------------------------------
const goToMain = () => {
  router.push({ name: 'MainView' });
};

// ----------------------------------------------------
// 2. 비밀번호 유효성 검사
// ----------------------------------------------------
const validatePassword = () => {
  const pass = formData.value.password;
  const username = route.query.email ? route.query.email.split('@')[0] : ''; 

  const hasLetter = /[A-Za-z]/.test(pass);
  const hasDigit = /\d/.test(pass);
  const hasSpecial = /[^a-zA-Z0-9\s]/.test(pass);
  const isSequential = /(.)\1{2,}/.test(pass);

  errors.value.password = '';

  if (pass.length < 8 || pass.length > 16) {
    errors.value.password = '비밀번호는 8자리 이상 16자리 이하로 입력하세요.';
  } else if (!hasLetter) {
    errors.value.password = '비밀번호에는 영문 대소문자가 최소 1자 이상 포함되어야 합니다.';
  } else if (!hasDigit) {
    errors.value.password = '비밀번호에는 숫자가 최소 1자 이상 포함되어야 합니다.';
  } else if (!hasSpecial) {
    errors.value.password = '비밀번호에는 특수문자가 최소 1자 이상 포함되어야 합니다.';
  } else if (isSequential) {
    errors.value.password = '같은 문자가 3회 이상 연속될 수 없습니다.';
  } else if (username && pass.includes(username)) {
    errors.value.password = '비밀번호는 이메일과 동일할 수 없습니다.';
  }

  // passwordConfirm이 입력된 경우에만 검사
  if (formData.value.passwordConfirm) {
    validatePasswordConfirm();
  }
};

// 비밀번호 확인 검사
const validatePasswordConfirm = () => {
  if (formData.value.password !== formData.value.passwordConfirm) {
    errors.value.passwordConfirm = '비밀번호가 일치하지 않습니다.';
  } else {
    errors.value.passwordConfirm = '';
  }
};

// 유효성 확인
const isPasswordValid = computed(() => errors.value.password === '' && formData.value.password.length > 0);
const isFormValid = computed(() => (
  formData.value.password &&
  formData.value.passwordConfirm &&
  isPasswordValid.value &&
  errors.value.passwordConfirm === ''
));

// ----------------------------------------------------
// 3. 완료 버튼 클릭 시 비밀번호 재설정 요청
// ----------------------------------------------------
const completeReset = async () => {
  if (!isFormValid.value) {
    errorMessage.value = '입력된 비밀번호와 규칙을 다시 확인해주세요.';
    return;
  }

  successMessage.value = '';
  errorMessage.value = '';

  try {
    const payload = {
      email: route.query.email,
      token: route.query.token,
      password: formData.value.password,
    };

    const response = await axios.post('/api/password/reset', payload); 

    // 응답 상태 검증 강화
    if (response.status === 200 && (response.data.success || response.data.message?.includes('success'))) {
      successMessage.value = '비밀번호 재설정을 완료했습니다.';
      // alert 제거 → UI 내 메시지로 표시
      setTimeout(() => router.push({ name: 'LoginView' }), 1000);
    } else {
      errorMessage.value = response.data.message || "서버 오류가 발생했습니다.";
      console.error('Password Reset Failed:', response.data);
    }
  } catch (error) {
    console.error("API 호출 중 오류 발생:", error);
    errorMessage.value = "서버가 불안정합니다. 다시 시도해주십시오.";
  }
};
</script>

<style scoped>
.error-msg {
  display: block;
  font-size: 0.75rem;
  color: #ef4444;
  margin-top: 0.25rem;
  margin-bottom: 0.5rem;
  padding-left: 0.75rem;
}

.reset-password-container {
  min-height: 100vh;
  background-color: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
  font-family: Arial, sans-serif;
}

.form-wrapper {
  width: 100%;
  max-width: 42rem;
  background-color: #ffffff;
  border: 2px solid #f2f2f2;
  border-radius: 0.75rem;
  padding: 2rem;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
}

.logo-section {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 1.5rem;
  height: 40px;
}

.logo-section .logo {
  cursor: pointer;
  height: 100%;
  width: auto;
}

.form-title {
  font-size: 1.5rem;
  font-weight: bold;
  text-align: center;
  color: #1f2937;
  margin-bottom: 2rem;
}

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

.input-field {
  display: block;
  width: 95%;
  padding: 0.75rem;
  border-radius: 0.375rem;
  border: 2px solid #f2f2f2;
  font-size: 0.875rem;
  color: #1f2937;
  transition: border-color 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
}

.input-field:focus {
  outline: none;
  border-color: #4ca7cc;
  box-shadow: 0 0 0 3px rgba(76, 167, 204, 0.25);
}

.password-rules-section {
  margin-bottom: 1.5rem;
}

.rule-text {
  font-size: 0.75rem;
  color: #a0a0a0;
  line-height: 1.5;
  transition: color 0.2s ease-in-out;
}

.warning-text {
  font-size: 0.75rem;
  color: #ef4444;
  margin-top: 0.5rem;
  font-weight: 500;
}

.error-text {
  color: #ef4444 !important;
}

.error-border {
  border-color: #ef4444 !important;
}

.submit-button {
  width: 100%;
  display: flex;
  justify-content: center;
  padding: 0.75rem 1rem;
  border-radius: 0.375rem;
  color: #ffffff;
  background-color: #4ca7cc;
  font-size: 0.875rem;
  font-weight: 500;
  transition: background-color 0.15s ease-in-out;
}

.submit-button:hover:not(:disabled) {
  background-color: #3d83a1;
}

.submit-button:disabled {
  background-color: #d1d5db;
  cursor: not-allowed;
}

@media (max-width: 640px) {
  .form-wrapper {
    padding: 1rem;
  }
}
</style>
