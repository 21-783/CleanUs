<template>
  <div>
    <h2>장부 설정</h2>
    
    <div class="password-section">
      <h3>장부게시물방 비밀번호 설정</h3>

      <!-- 비밀번호 입력창 -->
      <input 
        type="password" 
        v-model="password" 
        placeholder="새 비밀번호 입력"
        :disabled="isLoading"
      />

      <!-- 설정/재설정 버튼 -->
      <button 
        @click="setPassword" 
        :disabled="isLoading || !groupId"
      >
        {{ isPasswordSet ? '비밀번호 재설정' : '비밀번호 설정' }}
      </button>

      <!-- 메시지 표시 -->
      <p 
        v-if="message" 
        :class="message.includes('성공') ? 'success-msg' : 'error-msg'"
      >
        {{ message }}
      </p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';

// 상태 변수들
const password = ref('');
const message = ref('');
const isPasswordSet = ref(false);
const isLoading = ref(false);
const groupId = ref(null); // ✅ 동적으로 불러올 그룹 ID

/* 
-------------------------------------------------
1️⃣ 사용자 그룹 ID 불러오기
GET /api/user/info → group_id 사용
-------------------------------------------------
*/
const fetchGroupId = async () => {
  try {
    const res = await axios.get('/api/user/info');
    if (res.data?.group_id) {
      groupId.value = res.data.group_id;
    } else {
      console.warn('group_id가 응답에 없습니다.');
    }
  } catch (error) {
    console.error('그룹 ID 불러오기 실패:', error);
    message.value = '그룹 정보를 불러올 수 없습니다.';
  }
};

/* 
-------------------------------------------------
2️⃣ 그룹 비밀번호 설정/재설정
POST /api/group/{groupId}/password
-------------------------------------------------
*/
const setPassword = async () => {
  if (password.value.length < 6) {
    message.value = "비밀번호는 최소 6자 이상이어야 합니다.";
    return;
  }

  if (!groupId.value) {
    message.value = "그룹 정보가 없습니다. 다시 시도해주세요.";
    return;
  }

  message.value = '';
  isLoading.value = true;

  try {
    const payload = { password: password.value };
    const response = await axios.post(`/api/group/${groupId.value}/password`, payload);

    // 대소문자 구분 없이 처리
    const msg = response.data.message?.toLowerCase() || '';
    if (msg.includes('ledger password set successfully')) {
      isPasswordSet.value = true;
      message.value = "비밀번호가 성공적으로 설정/재설정되었습니다.";
      password.value = ''; // 입력 초기화
    } else {
      message.value = "비밀번호 설정에 성공했지만, 서버 응답 형식이 다릅니다.";
    }

  } catch (error) {
    console.error("비밀번호 설정 중 오류 발생:", error);
    message.value = "비밀번호 설정에 실패했습니다. 서버 연결을 확인해주세요.";
  } finally {
    isLoading.value = false;
  }
};

/* 
-------------------------------------------------
3️⃣ 비밀번호 설정 여부 조회
GET /api/group/{groupId}/password/status
-------------------------------------------------
*/
const fetchPasswordStatus = async () => {
  if (!groupId.value) return;
  try {
    const response = await axios.get(`/api/group/${groupId.value}/password/status`);
    isPasswordSet.value = response.data.isSet || false; 
  } catch (e) {
    console.warn("그룹 비밀번호 상태 로드 실패:", e);
    isPasswordSet.value = false;
  }
};

/* 
-------------------------------------------------
4️⃣ onMounted 시 초기 데이터 로드
-------------------------------------------------
*/
onMounted(async () => {
  await fetchGroupId();
  if (groupId.value) {
    await fetchPasswordStatus();
  }
});
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

button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.error-msg {
  color: #e74c3c;
  margin-top: 10px;
}

.success-msg {
  color: #28a745;
  margin-top: 10px;
}
</style>
