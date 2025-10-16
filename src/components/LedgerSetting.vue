<template>
  <div>
    <h2>장부 설정</h2>
    <div class="action-box">
      <button @click="goToLedger">장부 조회하러 이동</button>
    </div>
    
    <div class="password-section">
      <h3>장부게시물방 비밀번호 설정</h3>
      <input 
        type="password" 
        v-model="password" 
        placeholder="새 비밀번호 입력"
      />
      <button @click="setPassword">
        {{ isPasswordSet ? '비밀번호 재설정' : '비밀번호 설정' }}
      </button>
      <p v-if="message">{{ message }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const password = ref('');
const message = ref('');
const isPasswordSet = ref(false); // 서버에서 비밀번호 설정 여부를 가져와야 함

const goToLedger = () => {
  // Vue Router를 사용하여 라우팅
  router.push('/ledger');
};

const setPassword = async () => {
  if (password.value.length < 6) {
    message.value = "비밀번호는 최소 6자 이상이어야 합니다.";
    return;
  }
  
  try {
    // TODO: 서버에 비밀번호 저장(해시) 로직
    // fetch('/api/set-ledger-password', {
    //   method: 'POST',
    //   headers: { 'Content-Type': 'application/json' },
    //   body: JSON.stringify({ password: password.value })
    // });

    isPasswordSet.value = true;
    message.value = "비밀번호가 성공적으로 설정/재설정되었습니다.";
    password.value = '';

  } catch (error) {
    console.error("비밀번호 설정 중 오류 발생:", error);
    message.value = "비밀번호 설정에 실패했습니다.";
  }
};
</script>

<style scoped>
.password-section {
  margin-top: 20px;
}
input[type="password"] {
  padding: 8px;
  margin-right: 10px;
  border: 1px solid #ccc;
  border-radius: 4px;
}
</style>