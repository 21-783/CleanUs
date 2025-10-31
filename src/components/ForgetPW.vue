<template>
  <div class="reset-password-container">
    <div class="form-wrapper">
      <div class="logo-section">
        <img 
          src="@/assets/logo.png" 
          alt="로고" 
          class="logo" 
          @click="goToMain"
        />
      </div>

      <form @submit.prevent="resetPassword">
        <h1 class="form-title">비밀번호 찾기</h1>

        <!-- 이름 입력 -->
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

        <!-- 이메일 입력 및 인증 -->
        <div class="input-group">
          <label for="email" class="input-label">이메일</label>
          <div class="input-with-button">
            <input
              type="email"
              id="email"
              v-model="formData.email"
              placeholder="example@pukyong.ac.kr"
              class="input-field"
              :disabled="isEmailVerified || isCodeSent"
              required
            />
            <button
              type="button"
              @click="sendVerificationCode"
              :disabled="!isPukyongEmailValid || !formData.email || isEmailVerified || isSending"
              class="verify-button"
            >
              {{ isSending ? '전송 중...' : (isCodeSent ? '재전송' : '인증하기') }}
            </button>
          </div>
          <div v-if="formData.email && !isPukyongEmailValid" class="message error-message">
            이메일은 '@pukyong.ac.kr' 도메인만 사용할 수 있습니다.
          </div>
          <div v-if="isCodeSent && !isEmailVerified" class="message success-message">
            인증번호가 전송되었습니다.
          </div>
        </div>

        <!-- 인증번호 입력 -->
        <div class="input-group" v-if="isCodeSent && !isEmailVerified">
          <label for="verification-code" class="input-label">이메일 인증번호</label>
          <div class="input-with-button">
            <input
              type="text"
              id="verification-code"
              v-model="formData.verificationCode"
              placeholder="인증번호를 입력하세요"
              class="input-field"
              ref="codeInput"
              required
            />
            <button
              type="button"
              @click="verifyCode"
              :disabled="!formData.verificationCode || isVerifying"
              class="verify-button"
            >
              {{ isVerifying ? '확인 중...' : '확인' }}
            </button>
          </div>
          <div 
            v-if="verificationMessage" 
            :class="['message', verificationSuccess ? 'success-message' : 'error-message']"
          >
            {{ verificationMessage }}
          </div>
        </div>
        
        <!-- 인증 완료 안내 -->
        <div v-if="isEmailVerified" class="message success-message verified-message">
          인증이 완료되었습니다. 이제 비밀번호 재설정을 진행하세요.
        </div>

        <!-- 비밀번호 재설정 이동 버튼 -->
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
import { ref, computed, nextTick } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

// 폼 상태
const formData = ref({
  name: '',
  email: '',
  verificationCode: '',
});

// 인증 관련 상태
const isCodeSent = ref(false);
const isEmailVerified = ref(false);
const verificationMessage = ref('');
const verificationSuccess = ref(false);
const isSending = ref(false);
const isVerifying = ref(false);
const codeInput = ref(null);

// 메인으로 이동
const goToMain = () => router.push({ name: 'MainView' });

// 이메일 도메인 검사
const isPukyongEmailValid = computed(() => {
  if (!formData.value.email) return true;
  return /@pukyong\.ac\.kr$/.test(formData.value.email);
});

// 인증번호 전송
const sendVerificationCode = async () => {
  if (!isPukyongEmailValid.value) {
    alert("이메일은 '@pukyong.ac.kr' 도메인만 사용할 수 있습니다.");
    return;
  }

  try {
    isSending.value = true;
    const response = await axios.post('/api/email-verification/send', {
      email: formData.value.email
    });

    if (response.data.message === "Verification code sent successfully") {
      isCodeSent.value = true;
      verificationMessage.value = '';
      alert("인증번호가 전송되었습니다.");
      await nextTick();
      codeInput.value?.focus();
    } else {
      alert("인증번호 전송에 실패했습니다. 다시 시도해주세요.");
    }
  } catch (error) {
    console.error("이메일 전송 실패:", error);
    alert("서버 오류로 인증번호 전송에 실패했습니다.");
  } finally {
    isSending.value = false;
  }
};

// 인증번호 확인
const verifyCode = async () => {
  if (!formData.value.verificationCode) {
    alert("인증번호를 입력해주세요.");
    return;
  }

  try {
    isVerifying.value = true;
    const response = await axios.post('/api/email-verification/verify', {
      email: formData.value.email,
      code: formData.value.verificationCode
    });

    if (response.data.message === "Verification successful") {
      isEmailVerified.value = true;
      verificationSuccess.value = true;
      verificationMessage.value = "인증이 완료되었습니다.";
    } else {
      verificationSuccess.value = false;
      verificationMessage.value = "인증번호가 올바르지 않습니다.";
    }
  } catch (error) {
    console.error("인증 실패:", error);
    verificationSuccess.value = false;
    verificationMessage.value = "서버 오류로 인증에 실패했습니다.";
  } finally {
    isVerifying.value = false;
  }
};

// 폼 유효성 검사
const isFormValid = computed(() =>
  formData.value.name && formData.value.email && isEmailVerified.value
);

// 비밀번호 재설정 페이지로 이동
const resetPassword = () => {
  if (!isFormValid.value) {
    alert('모든 정보를 올바르게 입력하고 이메일 인증을 완료해주세요.');
    return;
  }

  // 실제 비밀번호 재설정 페이지 이동
  router.push({ name: 'GotoPW', query: { email: formData.value.email } });
};
</script>

<style scoped>
.message {
  font-size: 0.875rem;
  margin-top: 0.5rem;
  padding-left: 0.75rem;
}
.error-message {
  color: #ef4444;
}
.success-message {
  color: #10b981;
}
.verified-message {
  padding: 1rem;
  border: 1px solid #10b981;
  border-radius: 0.5rem;
  text-align: center;
  font-weight: bold;
  margin-top: 1rem;
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
  box-shadow: 0 4px 6px rgba(0,0,0,0.1);
}

.logo-section {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 1.5rem;
  height: 40px;
}

.logo {
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
}

.input-field {
  width: 100%;
  padding: 0.75rem;
  border-radius: 0.375rem;
  border: 2px solid #f2f2f2;
  font-size: 0.875rem;
  color: #1f2937;
  transition: border-color 0.15s ease-in-out;
}

.input-field:focus {
  outline: none;
  border-color: #4ca7cc;
  box-shadow: 0 0 0 3px rgba(76, 167, 204, 0.25);
}

.input-with-button {
  display: flex;
}

.input-with-button .input-field {
  border-top-right-radius: 0;
  border-bottom-right-radius: 0;
}

.verify-button {
  padding: 0.75rem 1rem;
  border-radius: 0.375rem;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
  background-color: #88d4ff;
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
