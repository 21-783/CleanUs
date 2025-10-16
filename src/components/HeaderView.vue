<template>
  <header class="header-container">
    <div class="header-left">
      <img src="@/assets/logo.png" alt="로고" class="logo" />
    </div>

    <div class="header-center"> 
    </div>

    <div class="header-right">
      <button class="icon-button account-button">
        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="icon user-icon">
          <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"></path>
          <circle cx="12" cy="7" r="4"></circle>
        </svg>
      </button>
      <button class="login-button">
        로그인
      </button>
    </div>
  </header>
</template>

<script setup>
</script>

<style scoped>
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