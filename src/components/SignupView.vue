<template>
  <div class="registration-container">
    <div class="form-wrapper">
      <div class="logo-section">
        <div class="logo-text">로고 사진</div>
      </div>

      <form @submit.prevent="submitForm">
        <h1 class="form-title">회원가입</h1>

        <div class="input-group">
          <label for="email" class="input-label">이메일</label>
          <div class="input-with-button">
            <input
              type="email"
              id="email"
              v-model="formData.email"
              placeholder="example@example.com"
              class="input-field"
              required
            />
            <button
              type="button"
              @click="sendVerificationEmail"
              class="verify-button"
              :disabled="isEmailSent || isEmailVerified"
            >
              {{ isEmailSent ? '재전송' : '인증하기' }}
            </button>
          </div>
          <div v-if="isEmailSent" class="message success">
            인증번호가 전송되었습니다.
          </div>
        </div>

        <div class="input-group" v-if="isEmailSent && !isEmailVerified">
          <label for="verification-code" class="input-label">인증번호</label>
          <div class="input-with-button">
            <input
              type="text"
              id="verification-code"
              v-model="verificationCode"
              placeholder="6자리 인증번호"
              class="input-field"
              required
            />
            <button
              type="button"
              @click="checkVerificationCode"
              class="verify-button"
            >
              확인
            </button>
          </div>
        </div>

        <div v-if="isEmailVerified" class="message success">
          본인인증이 성공했습니다.
        </div>

        <div class="input-group">
          <label for="password" class="input-label">비밀번호</label>
          <input
            type="password"
            id="password"
            v-model="formData.password"
            placeholder="비밀번호를 입력하세요"
            class="input-field"
            required
          />
        </div>

        <div class="input-group">
          <label for="confirm-password" class="input-label">비밀번호 재입력</label>
          <input
            type="password"
            id="confirm-password"
            v-model="formData.confirmPassword"
            placeholder="비밀번호를 다시 입력하세요"
            class="input-field"
            required
          />
        </div>

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

        <div class="input-group">
          <label for="dob" class="input-label">생년월일 (8자리)</label>
          <input
            type="text"
            id="dob"
            v-model="formData.dob"
            placeholder="예: 19990101"
            maxlength="8"
            class="input-field"
            required
          />
        </div>

        <div class="input-group">
          <label for="enrollment-year" class="input-label">입학년도 (학번)</label>
          <select
            id="enrollment-year"
            v-model="formData.enrollmentYear"
            class="select-field"
            required
          >
            <option value="" disabled selected>입학년도를 선택하세요</option>
            <option v-for="year in enrollmentYears" :key="year" :value="year">{{ year }}</option>
          </select>
        </div>

        <div class="input-group">
          <label for="department" class="input-label">학과</label>
          <select
            id="department"
            v-model="formData.department"
            class="select-field"
            required
          >
            <option value="" disabled selected>학과를 선택하세요</option>
            <option v-for="dept in departments" :key="dept" :value="dept">{{ dept }}</option>
          </select>
        </div>

        <div class="checkbox-group">
          <div class="checkbox-item">
            <input
              id="is-treasurer"
              type="checkbox"
              v-model="formData.isTreasurer"
              class="checkbox-input"
            />
            <label for="is-treasurer" class="checkbox-label">
              총무만 회원가입 가능합니다.
            </label>
          </div>
          <div class="checkbox-item">
            <input
              id="is-real-name"
              type="checkbox"
              v-model="formData.isRealName"
              class="checkbox-input"
              required
            />
            <label for="is-real-name" class="checkbox-label">
              본인 명의를 이용하여 가입을 진행하겠습니다.
            </label>
          </div>
          <div class="checkbox-item">
            <input
              id="is-over-14"
              type="checkbox"
              v-model="formData.isOver14"
              class="checkbox-input"
              required
            />
            <label for="is-over-14" class="checkbox-label">
              만 14세 이상입니다.
            </label>
          </div>
        </div>

        <button
          type="submit"
          :disabled="!isFormValid"
          class="submit-button"
        >
          회원가입 완료
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';

// 폼 데이터 상태 관리
const formData = ref({
  email: '',
  password: '',
  confirmPassword: '',
  name: '',
  dob: '',
  enrollmentYear: '',
  department: '',
  isTreasurer: false,
  isRealName: false,
  isOver14: false,
});

// 이메일 인증 관련 상태 관리
const isEmailSent = ref(false);
const verificationCode = ref('');
const isEmailVerified = ref(false);

// 드롭다운 리스트 데이터 (예시)
const enrollmentYears = [2025, 2024, 2023, 2022, 2021, 2020];
const departments = ['컴퓨터공학과', '경영학과', '전자공학과', '건축학과', '디자인학과'];

// 폼 유효성 검사 (모든 필수 필드와 체크박스가 선택되었는지 확인)
const isFormValid = computed(() => {
  return (
    formData.value.email &&
    formData.value.password &&
    formData.value.confirmPassword &&
    formData.value.name &&
    formData.value.dob &&
    formData.value.enrollmentYear &&
    formData.value.department &&
    formData.value.isRealName &&
    formData.value.isOver14 &&
    formData.value.password === formData.value.confirmPassword && // 비밀번호 일치 확인
    isEmailVerified.value // 이메일 인증 성공 여부 확인
  );
});

// 이메일 인증 메일 전송
const sendVerificationEmail = async () => {
  if (!formData.value.email) {
    alert("이메일을 입력해주세요.");
    return;
  }
  
  try {
    const response = await axios.post('/api/send-verification-email', {
      email: formData.value.email
    });
    
    if (response.data === "Email sent successfully!") {
      alert("인증번호가 전송되었습니다.");
      isEmailSent.value = true;
    } else {
      alert("이메일 전송에 실패했습니다. 다시 시도해주세요.");
    }
  } catch (error) {
    console.error("이메일 전송 실패:", error);
    alert("이메일 전송에 실패했습니다. 서버를 확인해주세요.");
  }
};

// 인증번호 확인
const checkVerificationCode = async () => {
  if (!verificationCode.value) {
    alert("인증번호를 입력해주세요.");
    return;
  }

  // TODO: 백엔드에 인증번호 확인을 위한 API 엔드포인트를 추가해야 합니다.
  // 이 부분은 현재 프론트엔드에서만 임시로 처리합니다.
  try {
    // 실제 백엔드 API 호출 예시 (아직 구현되지 않음)
    // const response = await axios.post('/api/verify-code', {
    //   email: formData.value.email,
    //   code: verificationCode.value
    // });
    
    // 이메일 인증이 성공했을 경우
    // if (response.data === "Verification successful!") {
    isEmailVerified.value = true;
    alert("본인인증이 성공했습니다.");
    // } else {
    //   alert("인증번호가 올바르지 않습니다.");
    // }
  } catch (error) {
    console.error("인증번호 확인 실패:", error);
    alert("인증번호 확인 중 오류가 발생했습니다.");
  }
};

// 회원가입 완료 버튼 클릭 핸들러
const submitForm = () => {
  if (isFormValid.value) {
    console.log('회원가입이 완료되었습니다. 폼 데이터:', formData.value);
    // 여기에 실제 회원가입 로직을 구현합니다 (API 호출 등).
  } else {
    console.log('폼을 모두 채워주세요.');
    alert('모든 필수 항목을 입력하고 동의해주세요.');
  }
};
</script>

<style scoped>
/* 전체 컨테이너 */
.registration-container {
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
  border: 2px solid #f2f2f2; /* 하늘색 테두리 */
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
.input-field,
.select-field {
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
.input-field:focus,
.select-field:focus {
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
  background-color: #88d4ffff; /* 하늘색 */
  color: #ffffff;
  font-size: 0.875rem;
  font-weight: 500;
  transition: background-color 0.15s ease-in-out;
  white-space: nowrap; /* 글자가 줄바꿈되지 않도록 수정 */
}

.verify-button:hover {
  background-color: #4ca7cc;
}

/* 체크박스 그룹 */
.checkbox-group {
  margin-bottom: 1.5rem;
}

.checkbox-item {
  display: flex;
  align-items: center;
  margin-top: 1rem;
}

.checkbox-input {
  width: 1rem;
  height: 1rem;
  border-radius: 0.25rem;
  border: 1px solid #f2f2f2;
  cursor: pointer;
}

.checkbox-label {
  margin-left: 0.5rem;
  font-size: 0.875rem;
  color: #1f2937;
}

.checkbox-input:checked {
  background-color: #62cff6; /* 하늘색 */
  border-color: #62cff6;
}

/* 회원가입 완료 버튼 */
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

.message {
  font-size: 0.875rem;
  margin-top: 0.5rem;
  text-align: center;
}
.success {
  color: #4CAF50; /* 녹색 */
}
</style>
