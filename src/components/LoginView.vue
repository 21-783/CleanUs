<template>
  <HeaderView />
  <div class="login-container">
    <div class="login-form-wrapper">
      <h2 class="form-title">Login</h2>
      
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>

      <div class="input-group">
        <input 
          type="text" 
          placeholder="example@pukyong.ac.kr" 
          v-model="email" 
          class="login-input"
        >
      </div>
      
      <div class="input-group">
        <input 
          type="password" 
          placeholder="비밀번호" 
          v-model="password" 
          class="login-input"
        >
      </div>
      
      <button @click="submitLogin" class="login-button" :disabled="isLoading">
        {{ isLoading ? '로그인 중...' : '로그인' }}
      </button>
    </div>

    <div class="link-group">
      <router-link to="/SignupView" class="auth-link">회원가입</router-link>
      <span class="divider">|</span>
      <router-link to="/ForgetPW" class="auth-link">비밀번호 찾기</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router'; 
import HeaderView from '@/components/HeaderView.vue';

const router = useRouter();
const email = ref('');
const password = ref('');
const errorMessage = ref(''); //  오류 메시지 상태
const isLoading = ref(false); //  로딩 상태

// -------------------------------------------------------------------
// 1. 이메일 도메인 유효성 검사 (computed)
// -------------------------------------------------------------------
const isPukyongEmailValid = computed(() => {
    // 이메일 입력이 없으면 유효성 검사 통과
    if (!email.value) return true; 
    
    // @pukyong.ac.kr로 끝나는지 확인
    return email.value.endsWith('@pukyong.ac.kr');
});

// -------------------------------------------------------------------
// 2. 로그인 로직 함수
// -------------------------------------------------------------------
const submitLogin = async () => {
    errorMessage.value = ''; // 오류 메시지 초기화

    // 1. 유효성 검사 (부경대 이메일 도메인)
    if (!isPukyongEmailValid.value) {
        errorMessage.value = "부경대학교 이메일(pukyong.ac.kr)만 가능합니다.";
        return;
    }

    if (!email.value || !password.value) {
        errorMessage.value = '이메일과 비밀번호를 모두 입력해주세요.';
        return;
    }
    
    isLoading.value = true;
    
    try {
        // 2. 백엔드로 email, password 전송 (명세서의 POST /auth/login 사용)
        const response = await fetch('/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email.value,
                password: password.value,
            }),
        });

        const data = await response.json();
        
        // 3. 백엔드 응답 처리
        if (response.ok && data.message === 'Login successful') {
            
            // 4-1. 로그인 성공: 로컬스토리지 저장 및 페이지 이동
            localStorage.setItem('userEmail', email.value); // 이메일만 저장
            localStorage.setItem('authToken', data.token); // 토큰도 저장하는 것이 일반적
            
            // MainPage.vue로 이동 (라우터 이름이 'MainPage'라고 가정)
            router.push({ name: 'MainPage' }); 
            
        } else {
            // 4-2. 로그인 실패 메시지 출력
            errorMessage.value = '로그인 정보가 다릅니다. 다시 입력해주세요.';
            console.error('Login Failed:', data.message || '서버 응답 오류');
        }

    } catch (error) {
        console.error('API 호출 중 오류 발생:', error);
        errorMessage.value = '서버 연결에 실패했습니다. 잠시 후 다시 시도해주세요.';
    } finally {
        isLoading.value = false;
    }
};


</script>

<style scoped>

.error-message {
    color: #e74c3c;
    background-color: #fcebeb;
    padding: 10px;
    border-radius: 6px;
    margin-bottom: 15px;
    font-size: 0.9em;
    font-weight: 500;
    width: 100%;
    text-align: center;
}

/* 기존 스타일 코드 유지 */
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f4f7ff; /* 아주 연한 푸른색 */
  padding: 20px;
  box-sizing: border-box;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.login-form-wrapper {
  position: relative;
  /* flex-direction: column으로 변경하여 요소들을 세로로 정렬 */
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  max-width: 400px; /* 이미지에 맞춰 크기 조정 */
  padding: 40px; /* 내부 패딩 추가 */
  background-color: #ffffff; /* 하얀색 배경 */
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
  margin-bottom: 30px;
}

.login-overlay-content {
  position: static;
  width: 100%;
  max-width: none;
  padding: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  box-sizing: border-box;
  text-align: center;
}

.form-title {
  font-size: 2.2em;
  font-weight: 700;
  color: #343a40; /* 진한 회색 */
  margin-bottom: 25px;
}

.input-group {
  display: flex;
  width: 100%;
  margin-bottom: 18px;
  text-align: center;
  align-items: center;
}

.login-input {
  width: 100%;
  padding: 14px 18px;
  border: 1px solid #ced4da; /* 옅은 회색 테두리 */
  border-radius: 8px; /* 이미지에 맞춰 모서리를 덜 둥글게 변경 */
  font-size: 1em;
  background-color: #f8f9fa; /* 입력창 배경색 */
  color: #343a40;
  outline: none;
  transition: all 0.3s ease;
}

.login-input::placeholder {
  color: #a0a8b4;
}

.login-input:focus {
  border-color: #5a74d2; /* 푸른색 테두리 */
  box-shadow: 0 0 0 3px rgba(90, 116, 210, 0.2);
}

.options-row {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  width: 100%;
  margin-bottom: 20px;
  font-size: 0.85em;
  color: #6c757d; /* 옅은 회색 */
}


.login-button {
  width: 100%;
  padding: 16px;
  background-color: #5a74d2; /* 푸른색 */
  color: white;
  border: none;
  border-radius: 8px; /* 이미지에 맞춰 모서리를 덜 둥글게 변경 */
  font-size: 1.1em;
  font-weight: 600;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 10px rgba(90, 116, 210, 0.3);
}

.login-button:hover {
  background-color: #4a62bb; /* 호버 시 약간 더 진한 푸른색 */
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(90, 116, 210, 0.4);
}

.link-group {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 15px;
}

.auth-link {
  color: #6c757d;
  text-decoration: none;
  font-size: 0.95em;
  font-weight: 500;
  transition: color 0.3s;
}

.auth-link:hover {
  color: #343a40;
  text-decoration: underline;
}

.divider {
  color: #ced4da;
  font-size: 0.95em;
}

@media (max-width: 600px) {
  .login-form-wrapper {
    max-width: 90%;
  }
  .login-overlay-content {
    width: 90%;
  }
  .form-title {
    font-size: 1.8em;
  }
}
</style>