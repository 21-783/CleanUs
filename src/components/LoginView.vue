<template>
  <div class="login-container">  
    <div class="login-form-wrapper">
      <h2 class="form-title">Login</h2>
      <div class="input-group">
        <input 
          type="text" 
          placeholder="이메일" 
          v-model="username" 
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
      <button @click="handleLogin" class="login-button">로그인</button>
    </div>

    <div class="link-group">
      <router-link to="/SignupView" class="auth-link">회원가입</router-link>
      <span class="divider">|</span>
      <router-link to="/reset-password" class="auth-link">비밀번호 찾기</router-link>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 폴더 이미지를 사용하지 않으므로 아래 코드는 필요 없습니다.
// import folderImage from '@/assets/Folderimage.png';

// 입력 필드와 체크박스 상태를 위한 반응형 변수
const username = ref('');
const password = ref('');
const rememberLogin = ref(false);
const ipSecurityOn = ref(true); 

// 로그인 버튼 클릭 시 실행될 함수
const handleLogin = () => {
  console.log('로그인 시도:', {
    username: username.value,
    password: password.value,
    rememberLogin: rememberLogin.value,
    ipSecurityOn: ipSecurityOn.value
  });
  // 실제 로그인 로직 (API 호출 등)을 여기에 구현
  alert('로그인 기능은 아직 구현되지 않았습니다.');
};
</script>

<style scoped>
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

/* 폴더 이미지를 사용하지 않으므로 아래 스타일은 제거 */
/* .folder-background-image {
  display: none;
} */

.login-overlay-content {
  /* 폴더 이미지를 사용하지 않으므로 absolute 위치를 제거 */
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

/* IP 보안 토글 스위치 스타일 - 이 컴포넌트에는 없음 */
/* .switch {} ... */

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