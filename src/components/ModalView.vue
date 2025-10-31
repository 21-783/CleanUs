<template>
  <div v-if="props.isVisible" class="modal-overlay" @click.self="closeModal">
    <div class="modal-content">
      
      <header class="modal-header">
        <h3 class="modal-title">{{ props.title }}</h3>
        <button class="btn-close" @click="closeModal">×</button>
      </header>
      
      <div class="modal-body">
        <slot></slot>
      </div>
      
      <footer class="modal-footer">
        <slot name="footer">
          <button class="btn-cancel" @click="closeModal">확인</button>
        </slot>
      </footer>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';

const props = defineProps({
    isVisible: {
        type: Boolean,
        required: true,
        default: false
    },
    title: {
        type: String,
        default: '알림'
    }
});

const emit = defineEmits(['close']);

const closeModal = () => {
    emit('close');
};
</script>

<style scoped>
/* (스타일 코드는 이전과 동일하며, 비밀번호 관련 스타일은 제거) */
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
  z-index: 9999;
  font-family: Arial, sans-serif;
}

.modal-content {
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
  width: 90%;
  max-width: 450px;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.modal-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #333;
  margin: 0;
}

.btn-close {
  background: none;
  border: none;
  font-size: 1.8rem;
  line-height: 1;
  cursor: pointer;
  color: #aaa;
  transition: color 0.2s;
}

.btn-close:hover {
  color: #777;
}

.modal-footer {
  padding-top: 15px;
  margin-top: 15px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel {
  padding: 8px 15px;
  background-color: #f0f0f0;
  color: #333;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
}
</style>