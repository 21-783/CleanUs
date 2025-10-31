<template>
  <HeaderView/>
  <div class="admin-container">
    <header class="admin-header">
      <h1> 사용자 설정 </h1>
      
      <!-- 🚨 0. 서브 타이틀: 클릭 가능한 링크 기능 추가 -->
      <p class="admin-subtitle">
        <span @click="scrollToSection('account')" class="scroll-link">계좌 설정</span> 
        | 
        <span @click="scrollToSection('ledger')" class="scroll-link">장부 설정</span> 
        | 
        <span @click="scrollToSection('user')" class="scroll-link">사용자 정보</span>
      </p>
    </header>
    
    <main class="admin-main">
      
      <!-- 🚨 ref 추가: 스크롤 대상으로 지정 -->
      <section class="admin-section account-settings" ref="accountRef">
        <AccountSettings />
      </section>
      
      <!-- 🚨 ref 추가 -->
      <section class="admin-section ledger-settings" ref="ledgerRef">
        <LedgerSettings />
      </section>
      
      <!-- 🚨 ref 추가 -->
      <section class="admin-section user-info" ref="userRef">
        <UserInfo />
      </section>
      
    </main>
  </div>
</template>

<script setup>
import { ref } from 'vue'; // ref import
import AccountSettings from './AccountSetting';
import LedgerSettings from './LedgerSetting';
import UserInfo from './UserInfo';
import HeaderView from '@/components/HeaderView.vue';

// 🚨 1. 스크롤 대상 ref 정의
const accountRef = ref(null);
const ledgerRef = ref(null);
const userRef = ref(null);

// 🚨 2. 스크롤 함수 정의
const scrollToSection = (sectionName) => {
    let targetRef;
    
    if (sectionName === 'account') {
        targetRef = accountRef;
    } else if (sectionName === 'ledger') {
        targetRef = ledgerRef;
    } else if (sectionName === 'user') {
        targetRef = userRef;
    }

    if (targetRef.value) {
        targetRef.value.scrollIntoView({ 
            behavior: 'smooth', 
            block: 'start' // 섹션을 뷰포트 상단에 맞춥니다.
        });
    }
};

</script>

<style scoped>
/* ================================================= */
/* 1. 전체 컨테이너 및 헤더 스타일 */
/* ================================================= */

.admin-container {
  max-width: 800px; 
  margin: 50px auto; 
  padding: 30px;
  background-color: #f0f4f8; 
  border-radius: 12px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.05);
  font-family: 'Arial', sans-serif;
}

.admin-header {
  text-align: center;
  margin-bottom: 30px; 
}

.admin-header h1 {
  color: #2c3e50;
  font-size: 2.5rem;
  font-weight: 600;
  margin: 0;
}

/* 0. 서브 타이틀 스타일 */
.admin-subtitle {
  font-size: 0.9rem;
  color: #6c7a89;
  margin-top: 5px;
  font-weight: 400;
  /* 🚨 텍스트 내 링크 사이의 간격 제거 */
  user-select: none; 
}

/* 🚨 새로 추가: 스크롤 링크 스타일 */
.scroll-link {
    cursor: pointer;
    color: #007bff; /* 링크 색상 */
    transition: color 0.2s;
    padding: 0 5px; /* 클릭 영역 확보 */
    font-weight: 500;
}
.scroll-link:hover {
    color: #0056b3; /* 호버 시 색상 변경 */
    text-decoration: underline;
}


/* ================================================= */
/* 2. 메인 콘텐츠 (수직 Flex 레이아웃 적용) */
/* ================================================= */

.admin-main {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center; 
}

/* 각 컴포넌트(섹션) 영역 스타일 */
.admin-section {
  width: 80%; 
  max-width: 800px; /* 중앙에 적당한 너비로 제한 */
  
  background-color: #ffffff;
  padding: 25px; 
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.05);
  margin: 0;
}
</style>