<template>
  <HeaderView />
  
  <div class="intro-section">
    <h1>우리 웹사이트에 오신 것을 환영합니다!</h1>
    <p>우리 조직의 장부를 투명하게 조회할 수 있게 도와줍니다. </p>
    <button @click="scrollToContent" class="scroll-button">게시물 보러가기</button>
  </div>
  
  <div ref="mainContent" class="main-page-container">
    <div class="centered-header-section">
      <h2 class="page-title">모든 게시물</h2>
      <div class="search-box">
        <input type="text" placeholder="모임명을 검색해주세요." class="search-input" />
        <button class="search-button">검색</button>
      </div>
    </div>
    
    <div class="post-grid">
      <div 
        v-for="post in displayedPosts" 
        :key="post.id" 
        class="post-card" 
        @click="goToPostDetail(post.id)"
      >
        <div class="post-details">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="post-affiliation">{{ post.affiliation }}</p>
          <p class="post-group">{{ post.group }}</p>
          <span class="post-date">{{ post.date }}</span>
        </div>

        <div class="favorite-button" @click.stop="toggleFavorite(post.id)">
          <span v-if="isFavorite(post.id)">⭐️</span>
          <span v-else>⚪</span>
        </div>

      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router';
import { ref, computed, onMounted } from 'vue';
import HeaderView from '@/components/HeaderView.vue';
// Vue Router의 인스턴스를 가져옵니다.
const router = useRouter();
const mainContent = ref(null);
const favoriteIds = ref([]); //즐겨찾기 ID를 저장할 ref

// 버튼 클릭 시 호출될 스크롤 함수
const scrollToContent = () => {
  if (mainContent.value) {
    mainContent.value.scrollIntoView({
      behavior: 'smooth', // 부드러운 스크롤 효과
      block: 'start'      // 요소의 시작 부분이 뷰포트 상단에 오도록 정렬
    });
  }
};

// 🚨 수정된 로직: 게시물 클릭 시 'TransactionTable.vue'가 연결된 라우트로 이동
const goToPostDetail = (postId) => {
  // 'TransactionDetail'은 index.js에서 /Post/:id 경로에 부여한 이름입니다.
  router.push({ 
    name: 'TransactionDetail', 
    params: { id: postId } 
  });
};


// 임시 게시물 데이터 (이 데이터는 PostDetailView.vue에서는 제거되었고, 
// TransactionTable.vue에서 사용됩니다. 일치시켜야 함.) 
const posts = ref([
  { id: 1, title: '제목명', affiliation: '학과명(단과대학)/동아리', group: '학생회명', date: '2023-10-26(작성날짜)' },
  { id: 2, title: '2025년 1학기 장부', affiliation: '정보융합대학', group: '컴퓨터공학학생회', date: '2023-10-25' },
  { id: 3, title: '2023년 2학기 장부', affiliation: '인문사회대학', group: '데이터분석팀', date: '2023-10-24' },
  { id: 4, title: '장부1', affiliation: '전기공학부(공과대학)', group: '학생회 A', date: '2023-10-23' },
  { id: 5, title: '장부2', affiliation: '수산과학대학', group: '학생회B', date: '2023-10-22' },
  { id: 6, title: '장부3', affiliation: '자연과학대학', group: '학생회C', date: '2023-10-21' },
  { id: 7, title: '장부4', affiliation: '자유전공학부', group: '학생회D', date: '2023-10-20' },
  { id: 8, title: '장부5', affiliation: '환경해양대학', group: '학생회E', date: '2023-10-19' },
]);

// 즐겨찾기 여부를 확인하는 함수
const isFavorite = (postId) => {
  return favoriteIds.value.includes(postId);
};

// 즐겨찾기 추가/제거 함수
const toggleFavorite = (postId) => {
  if (isFavorite(postId)) {
    // 이미 즐겨찾기에 있으면 제거
    favoriteIds.value = favoriteIds.value.filter(id => id !== postId);
  } else {
    // 없으면 추가
    favoriteIds.value.push(postId);
  }
  // 변경된 즐겨찾기 ID를 로컬 스토리지에 저장
  localStorage.setItem('favoriteIds', JSON.stringify(favoriteIds.value));
};

// 즐겨찾기를 적용한 게시물 목록 (computed 속성)
const displayedPosts = computed(() => {
  const favorites = posts.value.filter(post => favoriteIds.value.includes(post.id));
  const others = posts.value.filter(post => !favoriteIds.value.includes(post.id));
  return [...favorites, ...others];
});

// 컴포넌트가 마운트될 때 로컬 스토리지에서 즐겨찾기 ID를 불러옵니다.
onMounted(() => {
  const storedFavorites = localStorage.getItem('favoriteIds');
  if (storedFavorites) {
    favoriteIds.value = JSON.parse(storedFavorites);
  }
});

</script>

<style scoped>
.centered-header-section {
  display: flex; 
  flex-direction: column;
  align-items: center; 
  margin-bottom: 30px; 
}

.main-page-container {
  max-width: 1200px; 
  margin: 20px auto;
  padding: 20px;
  font-family: 'Segoe UI', Arial, sans-serif;
  justify-content: center;
}

.centered-header-section {
  display: flex; 
  flex-direction: column;
  align-items: center; 
  margin-bottom: 30px; 
}

.scroll-button {
  margin-top: 30px;
  padding: 12px 25px;
  font-size: 1rem;
  font-weight: bold;
  color: white;
  background-color: #007bff;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.scroll-button:hover {
  background-color: #0056b3;
}

.page-title {
  text-align: center;
  color: #333;
  margin-bottom: 20px;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr); 
  gap: 20px; 
}

.post-card {
  display: flex;
  flex-direction: column;
  padding: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background-color: #fcfcfc;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  position: relative; 
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
}

.post-details {
  flex-grow: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.post-title {
  font-size: 1.25rem;
  font-weight: bold;
  color: #333;
  margin: 0 0 10px 0;
}

.post-affiliation,
.post-group {
  font-size: 0.9rem;
  color: #555;
  margin-bottom: 5px;
}

.post-date {
  font-size: 0.8rem;
  color: #888;
  align-self: flex-end; 
  margin-top: auto; 
}

.search-box {
  display: flex;
  width: 100%;
  max-width: 500px;
  border: 1px solid #ddd;
  border-radius: 20px;
  overflow: hidden;
}

.search-input {
  flex-grow: 1;
  border: none;
  padding: 10px 15px;
  outline: none;
  font-size: 16px;
}

.search-button {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 10px 15px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.search-button:hover {
  background-color: #0056b3;
}

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

.favorite-button {
  position: absolute;
  top: 10px;
  right: 10px;
  font-size: 1.5rem;
  cursor: pointer;
  transition: transform 0.2s ease;
  z-index: 10;
}

.favorite-button:hover {
  transform: scale(1.2);
}
</style>