<template>
  <div class="popup-view-container">
    <h2>계좌 정보 등록</h2>
    
    <div class="form-card">
      <div class="input-group">
        <label for="bankName">은행명</label>
        <input type="text" id="bankName" v-model="accountInfo.bankName" placeholder="예: 국민은행" required>
      </div>
      
      <div class="input-group">
        <label for="accountNum">계좌번호</label>
        <input type="text" id="accountNum" v-model="accountInfo.accountNum" placeholder="'-' 없이 숫자만 입력" required>
      </div>
      
      <div class="input-group">
        <label for="ownerName">예금주</label>
        <input type="text" id="ownerName" v-model="accountInfo.ownerName" required>
      </div>
      
      <div class="button-group">
        <button @click="cancelRegistration" class="btn-cancel">취소</button>
        <button @click="completeRegistration" class="btn-confirm">등록 완료</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const accountInfo = ref({
  bankName: '',
  accountNum: '',
  ownerName: '',
});

// 등록 완료 버튼 클릭 시
const completeRegistration = () => {
  const info = accountInfo.value;
  if (!info.bankName || !info.accountNum || !info.ownerName) {
    alert('모든 정보를 입력해주세요.');
    return;
  }
  
  router.push({ 
    name: 'AdminPage', // AccountSetting.vue의 라우트 이름
    query: {
      bank: info.bankName,
      account: info.accountNum,
      owner: info.ownerName
    }
  });
};

// 취소 버튼 클릭 시
const cancelRegistration = () => {
  router.push({ name: 'AccountSetting' }); // 설정 페이지로 돌아가기
};
</script>

<style scoped>
.popup-view-container {
  max-width: 500px;
  margin: 50px auto;
  padding: 30px;
  text-align: center;
}

.form-card {
  background-color: white;
  padding: 30px;
  border-radius: 8px;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  font-size: 1.8rem;
  margin-bottom: 25px;
  color: #333;
}

.input-group {
  text-align: left;
  margin-bottom: 20px;
}

.input-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 5px;
  color: #555;
}

.input-group input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 16px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.btn-confirm, .btn-cancel {
  padding: 10px 20px;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.3s;
}

.btn-confirm {
  background-color: #007bff;
  color: white;
}

.btn-cancel {
  background-color: #ccc;
  color: #333;
}
</style>