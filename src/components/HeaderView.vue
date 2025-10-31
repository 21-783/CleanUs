<template>
  <header class="header-container">
    <div class="header-left">
      <!-- 🚨 1. 로고 클릭 시 MainPage.vue로 이동 -->
      <img 
        src="@/assets/logo.png" 
        alt="로고" 
        class="logo" 
        @click="goToMain" 
        style="cursor: pointer;"
      />
    </div>

    <div class="header-center"> 
    </div>

    <div class="header-right">
      <button class="icon-button account-button">
        <!-- 🚨 2. SVG 클릭 시 로그인 상태 확인 후 AdminPage로 이동 -->
        <svg 
          @click="handleAdminClick" 
          xmlns="http://www.w3.org/2000/svg" 
          viewBox="0 0 24 24" 
          fill="none" 
          stroke="currentColor" 
          stroke-width="2" 
          stroke-linecap="round" 
          stroke-linejoin="round" 
          class="icon user-icon"
          style="cursor: pointer;"
        >
          <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
          <circle cx="12" cy="7" r="4"></circle>
        </svg>
      </button>
      
      <!-- 🚨 3 & 4. 로그인 상태에 따라 버튼 텍스트와 기능 변경 -->
      <button class="login-button" @click="handleAuthClick">
        {{ isLoggedIn ? '로그아웃' : '로그인' }}
      </button>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router'; 

const router = useRouter(); 

// ----------------------------------------------------
// 4. 로그인 상태 관리 (로컬 스토리지 확인)
// ----------------------------------------------------
const authToken = ref(localStorage.getItem('authToken')); 

const isLoggedIn = computed(() => {
    return !!authToken.value; // authToken의 존재 여부로 로그인 상태 판단
});

// ----------------------------------------------------
// 1, 2, 3, 4. 라우팅 및 인증 핸들러 함수
// ----------------------------------------------------

// 1. 로고 클릭 시 MainPage.vue로 이동
const goToMain = () => {
    router.push({ name: 'MainView' });
};

// 2. SVG 클릭 핸들러 (AdminPage 이동, 로그인 상태 검사)
const handleAdminClick = () => {
    if (isLoggedIn.value) {
        // 2-2. 로그인 상태: AdminPage.vue로 이동
        router.push({ name: 'AdminPage' });
    } else {
        // 2-1. 비로그인 상태: 메시지 출력 (alert 사용)
        alert("로그인시 사용가능합니다.");
    }
};

// 3 & 4. 로그인/로그아웃 버튼 클릭 핸들러
const handleAuthClick = () => {
    if (isLoggedIn.value) {
        // 4. 로그아웃 처리
        localStorage.removeItem('authToken');
        localStorage.removeItem('userEmail'); 
        authToken.value = null; // 반응형 상태 업데이트
        
        // 로그아웃 후 MainView로 이동
        router.push({ name: 'MainView' });
        
    } else {
        // 3. 로그인 페이지로 이동
        router.push({ name: 'LoginView' });
    }
};

// 🚨 컴포넌트 마운트 시 로컬 스토리지 상태를 다시 확인
onMounted(() => {
    authToken.value = localStorage.getItem('authToken');
});
</script>

<style scoped>
/* 🚨 cursor: pointer 추가 (UI 피드백) */
.header-left .logo,
.icon-button .icon {
    cursor: pointer; 
}

.header-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 101%;
  padding: 10px 20px;
  box-sizing: border-box;
  background-color: #ffffff; /* 헤더 배경색 */
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); /* 헤더 아래 그림자 */
}

/* 각 섹션의 스타일 */
.header-left,
.header-right {
  flex: 0 0 30%; /* 너비 30% 고정 */
  display: flex;
  align-items: center;
}

.header-center {
  flex-grow: 1; /* 남은 공간 모두 차지 */
  display: flex;
  justify-content: center;
}

/* 왼쪽 섹션 - 로고 */
.header-left .logo {
  height: 40px; /* 로고 높이 */
}


/* 오른쪽 섹션 - 버튼들 */
.header-right {
  justify-content: flex-end; /* 오른쪽으로 정렬 */
  gap: 15px; /* 버튼 사이 간격 */
}

.icon-button {
  background: none;
  border: none;
  cursor: pointer;
  padding: 5px;
}

.icon-button .icon {
  width: 24px;
  height: 24px;
  color: #555;
}

.login-button {
  /* 🚨 로그아웃 버튼 색상 변화를 위해 배경색 유지 */
  background-color: #28a745; 
  color: white;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.login-button:hover {
  background-color: #218838;
}
</style>