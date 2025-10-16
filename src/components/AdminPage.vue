<template>
  <HeaderView/>
  <div class="admin-container">
    <header class="admin-header">
      <h1> 사용자 설정 </h1>
    </header>
    
    <main class="admin-main">
      
      <section class="admin-section account-settings">
        <AccountSettings />
      </section>
      
      <section class="admin-section ledger-settings divider-boundary">
        <LedgerSettings />
      </section>
      
      <section class="admin-section user-info">
        <UserInfo />
      </section>
      
    </main>
  </div>
</template>

<script setup>
import AccountSettings from './AccountSetting';
import LedgerSettings from './LedgerSetting';
import UserInfo from './UserInfo';
import HeaderView from '@/components/HeaderView.vue';
// 🚨 AccountSetting, LedgerSetting, UserInfo 컴포넌트 내부의 CSS도 
//   `flex: 1`이 적용된 부모 컨테이너가 아닌 경우, 너비를 100%로 설정해야 
//   이 레이아웃이 제대로 작동합니다.
</script>

<style scoped>
/* ================================================= */
/* 1. 전체 컨테이너 및 헤더 스타일 */
/* ================================================= */

.admin-container {
  /* 이미지의 여백을 고려하여 max-width와 margin 조정 */
  max-width: 1200px; 
  margin: 50px auto; /* HeaderView 아래 여백 확보 */
  padding: 30px;
  background-color: #f0f4f8; /* 배경색 유지 */
  border-radius: 12px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.05); /* 그림자 약하게 */
  font-family: 'Arial', sans-serif;
}
.admin-header {
  text-align: center;
  margin-bottom: 40px;
}
.admin-header h1 {
  color: #2c3e50;
  font-size: 2.5rem;
  font-weight: 600;
  margin: 0;
}

/* ================================================= */
/* 2. 메인 콘텐츠 (Flex 레이아웃 및 영역 공간 일정하게 만들기) */
/* ================================================= */

.admin-main {
  display: flex; /* Flexbox 활성화 */
  justify-content: space-between; /* 요소들을 사이좋게 배치 */
  align-items: stretch; /* 🚨 모든 섹션의 높이를 동일하게 맞춤 (가장 중요) */
  gap: 0; /* 섹션 사이 간격은 구분선이 담당하므로 0으로 설정 */
  /* 각 섹션 컴포넌트를 담는 영역이므로 padding 제거 */
}

/* 각 컴포넌트(섹션) 영역 스타일 */
.admin-section {
  flex: 1; /* 🚨 각 섹션이 공간을 정확히 동일하게 차지하도록 설정 (너비 균등) */
  background-color: #ffffff;
  padding: 25px; 
  /* 🚨 이 컴포넌트 자체가 이미지에서 보이는 카드 박스입니다. */
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  border-radius: 10px;
  margin: 0 10px; /* 각 섹션 좌우에 여백을 주어 중앙에 정렬되도록 함 */
  min-width: 250px;
}

/* ================================================= */
/* 3. 구분선 추가 (Border 이용) */
/* ================================================= */

/* 장부 설정 섹션에 오른쪽 구분선 추가 */
.account-settings {
  border-right: 1px solid #e0e0e0;
}

/* 장부 설정 섹션에 오른쪽 구분선 추가 (이 섹션은 왼쪽 경계도 있으므로 클래스명을 명확히 함) */
.ledger-settings {
  border-right: 1px solid #e0e0e0;
}

/* 사용자 정보 섹션은 가장 오른쪽이므로 구분선 없음 */
.user-info {
  /* border-right: none; (기본값) */
}

/* 구분선이 전체 컨테이너의 흰색 배경 위에 깔끔하게 보이도록, 
   admin-main의 여백을 제거하고 섹션에 마진을 부여하여 간격을 만들었습니다. */
</style>