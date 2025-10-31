<template>
  <div class="account-setting-container">
    <h2>계좌 설정</h2>

    <div class="account-display-card">
      <h3>은행 계좌 등록</h3>

      <!-- 🚨 로딩 중 -->
      <div v-if="isLoading">
        <p class="loading-text">계좌 정보를 불러오는 중...</p>
      </div>

      <!-- ✅ 등록된 계좌 있음 -->
      <div v-else-if="registeredAccount">
        <p class="status-success"><strong>✅ 등록 완료</strong></p>
        <p><strong>은행명:</strong> {{ registeredAccount.bankName }}</p>
        <p><strong>계좌번호:</strong> {{ maskedAccountNum }}</p>
        <p><strong>예금주:</strong> {{ registeredAccount.ownerName }}</p>
        <p class="registration-date">등록일: {{ registeredAccount.registeredAt }}</p>
        <button @click="goToRegistration" class="btn-update">계좌 정보 수정</button>
      </div>

      <!-- ❌ 등록된 계좌 없음 -->
      <div v-else>
        <p class="no-account">정산에 사용할 은행 계좌 정보가 없습니다.</p>
        <button @click="goToRegistration" class="btn-connect">계좌 정보 등록</button>
      </div>

      <!-- ⚠️ 오류 메시지 표시 -->
      <p v-if="errorMessage" class="error-text">{{ errorMessage }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();
const registeredAccount = ref(null);
const isLoading = ref(false);
const errorMessage = ref('');

// ✅ 1. API 호출 함수 (GET /user/account-check)
const fetchAccountInfo = async () => {
  isLoading.value = true;
  errorMessage.value = '';

  try {
    const response = await axios.get('/user/account-check');
    const data = response.data;

    // 응답 필드 확인 후 등록된 계좌인지 판별
    if (data.account_last4) {
      registeredAccount.value = {
        bankName: data.bank_name,
        accountLast4: data.account_last4,
        ownerName: data.holder_name,
        registeredAt: data.registered_at
          ? data.registered_at.slice(0, 10)
          : '정보 없음',
      };
    } else {
      registeredAccount.value = null;
    }
  } catch (error) {
    console.error('계좌 정보 로딩 중 오류 발생:', error);
    errorMessage.value = '서버에서 계좌 정보를 불러오는 중 오류가 발생했습니다.';
    registeredAccount.value = null;
  } finally {
    isLoading.value = false;
  }
};

// ✅ 2. 계좌번호 마스킹 처리 (예: *********1234)
const maskedAccountNum = computed(() => {
  const last4 = registeredAccount.value?.accountLast4;
  if (!last4 || last4.length !== 4) {
    return '계좌번호 정보 오류';
  }
  return '*********' + last4;
});

// ✅ 3. 등록 페이지로 이동
const goToRegistration = () => {
  router.push({ name: 'PopupView' });
};

// ✅ 4. 컴포넌트 마운트 시 데이터 불러오기
onMounted(() => {
  fetchAccountInfo();
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

.account-display-card {
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

.loading-text {
  color: #666;
  font-style: italic;
}

.error-text {
  color: #e74c3c;
  margin-top: 15px;
  font-size: 0.95rem;
}

.account-display-card p {
  font-size: 1rem;
  margin: 8px 0;
  color: #333;
}

.registration-date {
  font-size: 0.85rem;
  color: #888;
  margin-top: 10px;
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
