<template>
  <div>
    <h2>사용자 정보</h2>
    <div v-if="user" class="info-box">
      <p><strong>이름:</strong> {{ user.name }}</p> 
      <p><strong>이메일:</strong> {{ user.email }}</p>
      <p><strong>소속:</strong> {{ user.affiliation }}</p>
      <p><strong>가입 날짜:</strong> {{ user.joinDate }}</p>
      
      <div class="withdrawal-section">
        <button @click="showWithdrawalModal" class="danger-button">탈퇴</button>
      </div>
    </div>
    <div v-else class="info-box">
      <p>사용자 정보를 불러오는 중...</p>
    </div>

    <!-- ✅ 탈퇴 확인 모달 -->
    <div v-if="isModalOpen" class="modal-overlay">
      <div class="modal-content">
        <h3>정말 탈퇴하시겠습니까?</h3>
        <p>탈퇴 시 모든 정보가 영구적으로 삭제되며 복구할 수 없습니다.</p>
        <div class="modal-buttons">
          <button @click="confirmWithdrawal" class="danger-button">탈퇴하기</button>
          <button @click="cancelWithdrawal">취소</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';   // ✅ 추가
import axios from 'axios';

const router = useRouter();

const user = ref(null);
const isModalOpen = ref(false);

// ✅ 사용자 정보 불러오기
const fetchUserInfo = async () => {
  try {
    const response = await axios.get('/api/user/info');
    const data = response.data;

    user.value = {
      name: data.users_name || '이름 정보 없음', // ✅ 실제 필드명이 있다면 수정
      email: data.users_email || '이메일 정보 없음',
      affiliation: data.group_name || '소속 정보 없음',
      joinDate: data.users_created_at ? data.users_created_at.slice(0, 10) : '날짜 정보 없음'
    };
  } catch (error) {
    console.error("사용자 정보 로딩 중 오류 발생:", error);
    user.value = null;
  }
};

// ✅ 탈퇴 모달 제어
const showWithdrawalModal = () => {
  isModalOpen.value = true;
};

const cancelWithdrawal = () => {
  isModalOpen.value = false;
};

// ✅ 회원 탈퇴
const confirmWithdrawal = async () => {
  try {
    const response = await axios.post('/api/user/leave');

    if (response.data.message?.includes('Permanently delete your account')) {
      alert("탈퇴되었습니다.");
      isModalOpen.value = false;
      router.push({ name: 'MainView' }); // ✅ 메인 페이지로 이동
    } else {
      alert("다시 시도해주십시오.");
      isModalOpen.value = false;
    }
  } catch (error) {
    console.error("탈퇴 처리 중 오류 발생:", error);
    alert("다시 시도해주십시오.");
    isModalOpen.value = false;
  }
};

onMounted(fetchUserInfo);
</script>

<style scoped>
.danger-button {
  background-color: #e74c3c;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
}
.withdrawal-section {
  margin-top: 20px;
  border-top: 1px solid #eee;
  padding-top: 15px;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}
.modal-content {
  background-color: #fff;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
}
.modal-buttons button {
  margin: 0 10px;
}
</style>
