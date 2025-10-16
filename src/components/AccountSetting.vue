<template>
  <div class="account-setting-container">
    <h2>계좌 설정</h2>

    <div class="account-display-card">
      <h3>은행 계좌 등록</h3>
      <div v-if="registeredAccount">
        <p class="status-success"><strong>✅ 등록 완료</strong></p>
        <p><strong>은행명:</strong> {{ registeredAccount.bankName }}</p>
        <p><strong>계좌번호:</strong> {{ registeredAccount.accountNum }}</p>
        <p><strong>예금주:</strong> {{ registeredAccount.ownerName }}</p>
        <button @click="goToRegistration" class="btn-update">계좌 정보 수정</button>
      </div>
      <div v-else>
        <p class="no-account">정산에 사용할 은행 계좌 정보가 없습니다.</p>
        <button @click="goToRegistration" class="btn-connect">계좌 정보 등록</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute(); // 쿼리 파라미터를 읽기 위해 필요

const registeredAccount = ref(null); // 등록된 은행 계좌 정보

// 팝업 뷰 (계좌 등록 페이지)로 이동하는 함수
const goToRegistration = () => {
    // 'PopupView'는 router/index.js에 정의된 계좌 등록 페이지의 name이어야 합니다.
    router.push({ name: 'PopupView' }); 
};

// 쿼리 파라미터에서 계좌 정보를 읽어와 상태에 저장
const loadAccountFromQuery = () => {
    const { bank, account, owner } = route.query;
    
    // 쿼리 파라미터가 모두 존재할 경우 등록된 것으로 간주
    if (bank && account && owner) {
        registeredAccount.value = {
            bankName: bank,
            accountNum: account,
            ownerName: owner
        };
        // 🚨 중요: URL에 계좌 정보가 노출되지 않도록 쿼리 파라미터를 제거합니다.
        // router.replace({ query: {} }); // 현재는 주석 처리 (테스트 용이성을 위해)
    }
};

// 지갑 관련 기존 로직 (기능 구현은 생략)
const checkExistingConnection = async () => {
  // 예시 데이터:
  // walletAddress.value = '0x123...abc';
  // balance.value = '1.234';
};

onMounted(() => {
  checkExistingConnection();
  // 페이지 로드 시 URL 쿼리 파라미터에 등록 정보가 있는지 확인
  loadAccountFromQuery(); 
});
</script>

<style scoped>
.account-setting-container {
  max-width: 800px;
  margin: 50px auto;
  padding: 30px;
}

h2 {
  font-size: 1.8rem;
  color: #333;
  border-bottom: 2px solid #ddd;
  padding-bottom: 10px;
  margin-bottom: 25px;
}

.wallet-box, .account-display-card {
  background-color: #fff;
  border: 1px solid #ddd;
  padding: 20px;
  border-radius: 6px;
  margin-top: 15px;
  text-align: center;
}

.account-display-card h3 {
  margin-top: 0;
  color: #555;
  font-size: 1.3rem;
}

.no-account {
  color: #e67e22;
  font-style: italic;
  margin-bottom: 15px;
}

.status-success {
    color: #28a745;
    font-size: 1.1rem;
    margin-bottom: 15px;
}

.account-display-card p {
    font-size: 1rem;
    margin: 8px 0;
    color: #333;
}

.divider {
  border: 0;
  height: 1px;
  background-color: #eee;
  margin: 30px 0;
}

/* 버튼 스타일 */
button {
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s;
  margin-top: 10px;
}

.btn-wallet {
  background-color: #42b983;
  color: white;
}
.btn-wallet:hover {
  background-color: #369b71;
}

.btn-connect {
  background-color: #007bff;
  color: white;
}
.btn-connect:hover {
  background-color: #0056b3;
}

.btn-update {
  background-color: #28a745;
  color: white;
}
.btn-update:hover {
  background-color: #1e7e34;
}
</style>