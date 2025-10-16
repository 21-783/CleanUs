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

const user = ref(null);
const isModalOpen = ref(false);

// 사용자 정보 불러오기
const fetchUserInfo = async () => {
  try {
    // TODO: 실제 서버 API 호출
    // const response = await fetch('/api/user-info');
    // user.value = await response.json();
    
    // 임시 데이터
    user.value = {
      name: '홍길동',
      email: 'gildong@pukyong.ac.kr',
      affiliation: '학생회명(정보융합대학)',
      joinDate: '2025-01-01'
    };
  } catch (error) {
    console.error("사용자 정보 로딩 중 오류 발생:", error);
  }
};

const showWithdrawalModal = () => {
  isModalOpen.value = true;
};

const cancelWithdrawal = () => {
  isModalOpen.value = false;
};

const confirmWithdrawal = async () => {
  try {
    // TODO: 서버에 탈퇴 요청
    // const response = await fetch('/api/user-withdrawal', { method: 'POST' });
    // if (response.ok) {
    //   alert("성공적으로 탈퇴되었습니다.");
    //   // TODO: 로그아웃 또는 다른 페이지로 리다이렉트
    // } else {
    //   alert("탈퇴 실패. 다시 시도해 주세요.");
    // }
    alert("성공적으로 탈퇴되었습니다. (임시 메시지)");
    isModalOpen.value = false;

  } catch (error) {
    console.error("탈퇴 처리 중 오류 발생:", error);
    alert("탈퇴 처리 중 오류가 발생했습니다.");
  }
};

onMounted(() => {
  fetchUserInfo();
});
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