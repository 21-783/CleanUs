<template>
  <div class="popup-view-container">
    <h2>계좌 정보 등록</h2>
    
    <div class="form-card">
      <div class="input-group">
        <label for="bankName">은행명</label>
        <select id="bankName" v-model="accountInfo.bankName" class="select-field" required>
          <option value="" disabled>은행을 선택하세요</option>
          <option v-for="bank in bankList" :key="bank" :value="bank">
            {{ bank }}
          </option>
        </select>
      </div>
      
      <div class="input-group">
        <label for="accountNum">계좌번호</label>
        <input 
          type="text" 
          id="accountNum" 
          v-model="accountInfo.fullAccountNumber" 
          placeholder="계좌 번호 전체를 입력하세요 ('-' 제외)" 
          @input="accountInfo.fullAccountNumber = accountInfo.fullAccountNumber.replace(/[^0-9]/g, '')"
          required
        >
      </div>
      
      <div class="input-group">
        <label for="ownerName">예금주</label>
        <input type="text" id="ownerName" v-model="accountInfo.holderName" required>
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
import axios from 'axios';

const router = useRouter();

// 은행 목록
const bankList = [
  'KDB산업은행', 'IBK기업은행', 'KB국민은행', 'KEB하나은행', '수협은행', 
  'NH농협은행', '우리은행', 'SC은행', '씨티은행', '대구은행', 
  '부산은행', '광주은행', '제주은행', '전북은행', '경남은행', 
  'MG새마을금고', '신협', '저축은행', '산림조합', '우체국', 
  '하나은행', '신한은행', '케이뱅크', '카카오뱅크', '토스뱅크', 
  'SBI저축은행', 'KB증권', '미래에셋증권', '삼성증권', '한국투자증권', 
  'NH투자증권', '교보증권', '하이투자증권', '현대차투자증권', '키움증권', 
  '이베스트증권', 'SK증권', '대신증권', '한화투자증권', '하나증권', 
  '토스증권', '신한투자증권', 'DB금융투자', '유진투자', '메리츠증권', 
  '토스머니', '토스포인트'
];

const accountInfo = ref({
  bankName: '',
  fullAccountNumber: '', 
  holderName: '',
});

// ✅ 등록 완료
const completeRegistration = async () => {
  const info = accountInfo.value;

  if (!info.bankName || !info.fullAccountNumber || !info.holderName) {
    alert('모든 정보를 입력해주세요.');
    return;
  }

  // 숫자만 추출
  const cleanedAccountNum = info.fullAccountNumber.replace(/[^0-9]/g, '');

  if (cleanedAccountNum.length < 4) {
    alert('계좌번호는 최소 4자리 이상 입력해야 합니다.');
    return;
  }

  // 마지막 4자리만 추출
  const lastFourDigits = cleanedAccountNum.slice(-4);


  const payload = {
    bank_name: info.bankName,
    holder_name: info.holderName.trim(),
    account_last4: lastFourDigits,
  };

  try {
    const response = await axios.post('/user/account-register', payload);

    if (response.data.message?.includes('Register account information')) {
      alert('등록되었습니다.');
    } else {
      alert('등록 완료 (확인 필요)');
    }
    router.push({ name: 'AdminPage' });
    
  } catch (error) {
    console.error('계좌 등록 API 오류:', error);
    if (error.response) {
      alert(`오류: ${error.response.data.message || '등록에 실패했습니다.'}`);
    } else {
      alert('서버 응답이 없습니다. 네트워크를 확인해주세요.');
    }
  }
};

const cancelRegistration = () => {
  router.push({ name: 'AdminPage' }); 
};
</script>

<style scoped>
.select-field {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 16px;
  appearance: none;
  background: white url("data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 20 20'%3E%3Cpath fill='%236B7280' d='M9.293 12.95l.707.707L15 9.707l-1.414-1.414L10 11.536 6.464 8.293 5.757 9z'/%3E%3C/svg%3E") no-repeat right 0.75rem center;
  background-size: 1rem;
}

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

.input-group input,
.select-field { 
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
