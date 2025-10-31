<template>
  <HeaderView />

  <!-- 인트로 섹션 -->
  <div class="intro-section">
    <h1>Clean : US(URI Service)</h1>
    <p>장부를 투명하게 조회할 수 있게 도와줍니다.</p>
    <button @click="scrollToContent" class="scroll-button">게시물 보러가기</button>
  </div>

  <!-- 게시물 영역 -->
  <div ref="mainContent" class="main-page-container">
    <div class="centered-header-section">
      <h2 class="page-title">모든 게시물</h2>

      <div v-if="errorMessage" class="api-error">
        {{ errorMessage }}
      </div>

      <div class="search-box">
        <input
          type="text"
          placeholder="모임명을 검색해주세요."
          class="search-input"
          v-model="searchQuery"
          @keyup.enter="onSearch"
        />
        <button class="search-button" @click="onSearch">검색</button>
      </div>
    </div>

    <div v-if="loading" class="loading-message">게시물 목록을 불러오는 중...</div>

    <div v-else class="post-grid">
      <div 
        v-for="post in postsData" 
        :key="post.id" 
        class="post-card" 
        @click="goToPostDetail(post.id, post.name)"
      >
        <div class="post-details">
          <h3 class="post-title">{{ post.name }}</h3>
          <p class="post-group">{{ post.groupName }}</p>
          <span class="post-date">{{ post.date }}</span>
        </div>

        <div class="favorite-button" @click.stop="toggleFavorite(post.id)">
          <span v-if="isFavorite(post.id)">★</span>
          <span v-else>☆</span>
        </div>
      </div>

      <div v-if="postsData.length === 0 && !loading" class="no-posts">
        <p>표시할 게시물이 없습니다.</p>
      </div>
    </div>

    <!-- 페이지네이션 -->
    <div v-if="totalPages > 1" class="pagination">
      <button 
        @click="changePage(currentPage - 1)" 
        :disabled="currentPage === 1"
        class="page-nav-button"
      >
        이전
      </button>

      <span class="divider">|</span>

      <div class="page-numbers">
        <button 
          v-for="page in paginatedPages" 
          :key="page" 
          @click="changePage(page)" 
          :class="['page-number-button', { active: page === currentPage }]">
          {{ page }}
        </button>
      </div>

      <span class="divider">|</span>

      <button 
        @click="changePage(currentPage + 1)" 
        :disabled="currentPage === totalPages"
        class="page-nav-button"
      >
        다음
      </button>
    </div>
  </div>

  <!-- 모달: 비밀번호 입력 -->
  <ModalView
    :isVisible="isModalOpen"
    :title="modalTitle"
    @close="isModalOpen = false"
  >
    <p class="instruction-text">장부 접근을 위해 비밀번호를 입력해주세요.</p>

    <div class="input-group">
      <input 
        type="password" 
        v-model="accessPassword" 
        @keyup.enter="handleAccessAttempt"
        placeholder="비밀번호 입력"
        required
        ref="passwordInput"
      >
    </div>
    <p v-if="accessError" class="error-msg">{{ accessError }}</p>

    <template #footer>
      <button @click="handleAccessAttempt" class="btn-confirm" :disabled="!accessPassword">
        접속
      </button>
      <button @click="isModalOpen = false" class="btn-cancel">
        취소
      </button>
    </template>
  </ModalView>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ref, computed, onMounted, nextTick, watch } from 'vue';
import axios from 'axios';
import HeaderView from '@/components/HeaderView.vue';
import ModalView from '@/components/ModalView.vue';

const router = useRouter();
const mainContent = ref(null);
const passwordInput = ref(null);

const favoriteIds = ref([]);
const errorMessage = ref('');
const loading = ref(false);

// 페이지네이션 및 상태
const postsData = ref([]);
const currentPage = ref(1);
const pageSize = 12;
const totalItems = ref(0);
const searchQuery = ref('');

// 모달 관련 상태
const isModalOpen = ref(false);
const selectedLedgerId = ref(null);
const modalTitle = ref('');
const accessPassword = ref('');
const accessError = ref('');

// API 호출: 게시물 불러오기
const fetchPosts = async (page = currentPage.value) => {
  loading.value = true;
  errorMessage.value = '';
  const endpoint = searchQuery.value 
    ? `/api/ledgers/search?search=${searchQuery.value}&page=${page}&size=${pageSize}`
    : `/api/ledgers?page=${page}&size=${pageSize}`;

  try {
    const response = await axios.get(endpoint);
    const data = response.data;

    if (Array.isArray(data.groups) && data.groups.length > 0) {
      postsData.value = data.groups.map(p => ({
        id: p.id,
        name: p.name || '(제목 없음)',
        date: p.created_at ? p.created_at.slice(0, 10) : '날짜 없음',
        groupName: p.group_name || '(그룹명 없음)',
      }));
      totalItems.value = data.totalItems || data.groups.length;
      currentPage.value = page;
    } else {
      postsData.value = [];
      totalItems.value = 0;
      errorMessage.value = '표시할 게시물이 없습니다.';
    }
  } catch (e) {
    console.error('게시물 로드 실패:', e);
    postsData.value = [];
    totalItems.value = 0;
    errorMessage.value = e.response?.data?.message || '서버 연결 오류가 발생했습니다.';
  } finally {
    loading.value = false;
  }
};

// 검색
const onSearch = () => {
  currentPage.value = 1;
  fetchPosts(1);
};

// 페이지네이션
const totalPages = computed(() => Math.ceil(totalItems.value / pageSize));

const changePage = (page) => {
  if (page >= 1 && page <= totalPages.value) {
    fetchPosts(page);
  }
};

const paginatedPages = computed(() => {
  const pages = [];
  const maxPagesToShow = 5;
  let startPage = Math.max(1, currentPage.value - Math.floor(maxPagesToShow / 2));
  let endPage = Math.min(totalPages.value, startPage + maxPagesToShow - 1);

  if (endPage - startPage + 1 < maxPagesToShow) {
    startPage = Math.max(1, endPage - maxPagesToShow + 1);
  }

  for (let i = startPage; i <= endPage; i++) pages.push(i);
  if (!pages.includes(1)) pages.unshift(1);
  return pages;
});

// 게시물 클릭 → 모달
const goToPostDetail = (id, name) => {
  selectedLedgerId.value = id;
  modalTitle.value = name + ' - 접근';
  accessPassword.value = '';
  accessError.value = '';
  isModalOpen.value = true;
};

// 모달에서 비밀번호 인증
const handleAccessAttempt = async () => {
  accessError.value = '';
  if (!accessPassword.value) {
    accessError.value = '비밀번호를 입력해주세요.';
    return;
  }
  try {
    const response = await axios.post(`/api/group/${selectedLedgerId.value}/verify-password`, {
      password: accessPassword.value
    });

    if (response.status === 200) {
      isModalOpen.value = false;
      router.push({ name: 'TransactionDetail', params: { id: selectedLedgerId.value } });
    }
  } catch (e) {
    if (e.response?.status === 401) {
      accessError.value = '비밀번호가 올바르지 않습니다.';
    } else {
      console.error('접속 시도 오류:', e);
      accessError.value = '서버 연결에 실패했습니다.';
    }
  }
};

// 모달 열리면 포커스 자동 이동
watch(isModalOpen, async (val) => {
  if (val) {
    await nextTick();
    passwordInput.value?.focus();
  }
});

// 스크롤
const scrollToContent = () => {
  mainContent.value?.scrollIntoView({ behavior: 'smooth', block: 'start' });
};

// 즐겨찾기
const isFavorite = (postId) => favoriteIds.value.includes(postId);
const toggleFavorite = (postId) => {
  const currentPost = postsData.value.find(p => p.id === postId);
  if (!currentPost) return;

  if (isFavorite(postId)) {
    favoriteIds.value = favoriteIds.value.filter(id => id !== postId);
  } else {
    favoriteIds.value.push(postId);
  }
  localStorage.setItem('favoriteIds', JSON.stringify(favoriteIds.value));
};

// 초기화
onMounted(() => {
  try {
    const storedFavorites = localStorage.getItem('favoriteIds');
    if (storedFavorites) favoriteIds.value = JSON.parse(storedFavorites);
  } catch {
    favoriteIds.value = [];
    localStorage.removeItem('favoriteIds');
  }
  fetchPosts(1);
});
</script>

<style scoped>
/* ----------------------------- */
/* 인트로 섹션 */
.intro-section {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  height: 85vh;
  width: 100%;
  background-color: #f0f4f8;
  color: #333;
  padding: 10px;
}
.intro-section h1 {
  font-size: 2.5rem;
  margin-bottom: 20px;
}
.intro-section p {
  font-size: 1.2rem;
  max-width: 500px;
  line-height: 1.6;
}
.scroll-button {
  margin-top: 30px;
  padding: 12px 25px;
  font-size: 1rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
}
.scroll-button:hover {
  background-color: #0056b3;
}

/* ----------------------------- */
/* 게시물 영역 */
.main-page-container {
  padding: 40px 20px;
  max-width: 1200px;
  margin: 0 auto;
}
.centered-header-section {
  text-align: center;
  margin-bottom: 30px;
}
.page-title {
  font-size: 2rem;
  margin-bottom: 15px;
}
.api-error {
  color: red;
  margin-bottom: 10px;
}
.search-box {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-bottom: 20px;
}
.search-input {
  padding: 8px 12px;
  width: 250px;
  border: 1px solid #ccc;
  border-radius: 5px;
}
.search-button {
  padding: 8px 15px;
  background-color: #28a745;
  border: none;
  border-radius: 5px;
  color: white;
  cursor: pointer;
}
.search-button:hover {
  background-color: #1e7e34;
}

/* 게시물 카드 */
.post-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}
.post-card {
  background-color: white;
  border-radius: 8px;
  padding: 15px;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  flex-direction: column;
  transition: transform 0.2s, box-shadow 0.2s;
}
.post-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}
.post-title {
  font-weight: 600;
  font-size: 1.2rem;
  margin-bottom: 8px;
}
.post-group {
  font-size: 0.95rem;
  color: #555;
  margin-bottom: 6px;
}
.post-date {
  font-size: 0.85rem;
  color: #888;
}
.favorite-button {
  font-size: 1.3rem;
  color: #ffcc00;
  cursor: pointer;
  margin-top: 10px;
}
.no-posts {
  text-align: center;
  font-size: 1.1rem;
  color: #666;
  margin-top: 50px;
}

/* 페이지네이션 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 40px;
  gap: 10px;
  flex-wrap: wrap;
}
.page-nav-button,
.page-number-button {
  padding: 6px 12px;
  border: 1px solid #ccc;
  background-color: white;
  border-radius: 4px;
  cursor: pointer;
}
.page-number-button.active {
  background-color: #007bff;
  color: white;
  border-color: #007bff;
}
.page-nav-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
.divider {
  font-weight: bold;
  color: #555;
}

/* 모달 */
.input-group input {
  width: 100%;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #ccc;
  margin-bottom: 10px;
}
.btn-confirm {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
  margin-right: 10px;
}
.btn-confirm:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
.btn-cancel {
  background-color: #6c757d;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}
.error-msg {
  color: red;
  font-size: 0.9rem;
}
.instruction-text {
  font-size: 1rem;
  margin-bottom: 10px;
}

/* ----------------------------- */
/* 모바일 반응형 */
@media (max-width: 768px) {
  .intro-section h1 {
    font-size: 2rem;
  }
  .intro-section p {
    font-size: 1rem;
  }
  .search-box {
    flex-direction: column;
    gap: 8px;
  }
  .search-input {
    width: 100%;
  }
  .post-grid {
    grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  }
}
</style>

