<template>
  <div class="ledger-table-container">
    
    <header class="table-info-header">
      <button @click="goBackToMain" class="btn-back">
        ← 뒤로가기
      </button>
      
      <h1 class="table-title">
        {{ postData.title || '거래내역 (로드 실패)' }}
      </h1>
      
      <div v-if="postData.id" class="post-meta-info">
        <p class="meta-item">
          <span class="meta-label">소속:</span>
          <span class="meta-value">{{ postData.affiliation }}</span>
        </p>
        <p class="meta-item">
          <span class="meta-label">모임:</span>
          <span class="meta-value">{{ postData.group }}</span>
        </p>
        <p class="meta-item">
          <span class="meta-label">작성일:</span>
          <span class="meta-value">{{ postData.date }}</span>
        </p>
      </div>
    </header>
    
    <div class="table-controls">
      <button class="btn-refresh" @click="fetchTransactions" :disabled="loading">
        {{ loading ? '불러오는 중...' : '새로고침' }}
      </button>
    </div>

    <div v-if="error" class="error">❌ {{ error }}</div>

    <table class="transaction-table">
      <thead>
        <tr>
          <th>거래일시</th>
          <th>메모</th>
          <th class="amount-cell">금액</th>
          <th>블록체인 상태</th>
          <th>블록 번호</th>
          <th>무결성</th>
          <th>액션</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(tx, index) in items" :key="index">
          <td>{{ formatDate(tx.time) }}</td>
          <td>{{ tx.memo }}</td>
          <td class="amount-cell">{{ formatAmount(tx.amount) }}</td>
          <td>
            <span v-if="tx.txHash" class="badge ok">기록됨 ✅</span>
            <span v-else class="badge pend">대기중 ⏳</span>
          </td>
          <td>{{ tx.blockNum ?? '—' }}</td>
          <td>
            <span v-if="tx.integrity===true" class="integrity-result ok">O</span>
            <span v-else-if="tx.integrity===false" class="integrity-result err">X</span>
            <span v-else class="integrity-result muted">—</span>
          </td>
          <td>
            <button class="btn-action" @click.stop="verifyMerkleProof(tx)">
              무결성 검증
            </button>
          </td>
        </tr>
      </tbody>
    </table>
    <p v-if="!items.length && !loading" class="empty-data">표시할 거래가 없습니다.</p>
  </div>
</template>

<script setup>
import { ref, defineProps, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router'; // 🚨 라우터 기능을 위해 추가
import { ethers } from "ethers";
import SHA256 from "crypto-js/sha256";

const router = useRouter(); 
const route = useRoute(); // ⬅️ 라우트 인스턴스 초기화

/* eslint-disable no-unused-vars */
const props = defineProps({
    // MainView에서 id만 전달되므로, postData는 이제 이 컴포넌트 내부에서 찾습니다.
    id: [Number, String],
});
/* eslint-enable no-unused-vars */

// ----------------------------------------------------
// 1. 상태 변수 및 데이터
// ----------------------------------------------------
const items = ref([]); 
const loading = ref(false);
const error = ref(null);
const batchId = ref(123); 

let provider = null;
let contract = null;

// 🚨 MainView.vue와 동일한 임시 데이터 목록
const tempPosts = [
  { id: 1, title: '제목명', affiliation: '학과명(단과대학)/동아리', group: '학생회명', date: '2023-10-26(작성날짜)' },
  { id: 2, title: '2025년 1학기 장부', affiliation: '정보융합대학', group: '컴퓨터공학학생회', date: '2023-10-25' },
  { id: 3, title: '2023년 2학기 장부', affiliation: '인문사회대학', group: '데이터분석팀', date: '2023-10-24' },
  { id: 4, title: '장부1', affiliation: '전기공학부(공과대학)', group: '학생회 A', date: '2023-10-23' },
  { id: 5, title: '장부2', affiliation: '수산과학대학', group: '학생회B', date: '2023-10-22' },
  { id: 6, title: '장부3', affiliation: '자연과학대학', group: '학생회C', date: '2023-10-21' },
  { id: 7, title: '장부4', affiliation: '자유전공학부', group: '학생회D', date: '2023-10-20' },
  { id: 8, title: '장부5', affiliation: '환경해양대학', group: '학생회E', date: '2023-10-19' },
];

// 🚨 현재 게시물 정보 객체를 계산하는 computed 속성 (ID: N/A 오류 해결)
const postData = computed(() => {
    // props.id 또는 route.params.id 중 유효한 ID를 찾습니다.
    const rawId = props.id || route.params.id; 
    const postId = parseInt(rawId); 
    
    // ID를 사용하여 tempPosts에서 게시물 객체를 찾습니다.
    const foundPost = tempPosts.find(p => p.id === postId);
    
    // 찾지 못했을 경우 템플릿의 오류 메시지 출력을 위한 기본 객체를 반환합니다.
    return foundPost || { id: null, title: '거래내역 (로드 실패)', affiliation: '—', group: '—', date: '—' };
});

// ----------------------------------------------------
// 2. 초기화 및 헬퍼 함수
// ----------------------------------------------------

const initContract = () => {
    const rpcUrl = "https://rpc-mumbai.maticvigil.com/"; 
    try {
        provider = new ethers.JsonRpcProvider(rpcUrl); 
        const abi = ["function merkleRoots(uint256 batchId) external view returns (bytes32)"];
        const contractAddress = "0xYourActualContractAddressHere"; // 🚨 실제 컨트랙트 주소로 변경 필수
        contract = new ethers.Contract(contractAddress, abi, provider);
    } catch (e) {
        console.error("Contract 초기화 실패:", e);
        error.value = "블록체인 연결 초기화 실패. RPC URL을 확인하세요.";
    }
};

const formatAmount = (amount) => {
  return amount != null ? Number(amount).toLocaleString('ko-KR') : '—';
};

const formatDate = (d) => {
    return d || '—';
};

// 🚨 뒤로가기 함수 추가
const goBackToMain = () => {
    router.push({ name: 'MainView' }); // index.js에 정의된 MainView 라우트로 이동
};

// ----------------------------------------------------
// 3. API 호출 및 검증 로직
// ----------------------------------------------------

const fetchTransactions = async () => {
  loading.value = true;
  error.value = null;
  
  // 🚨 임시 데이터 로드
  const apiData = [
    { id: 1, time: '2023-10-19', memo: '식품', amount: 8300, txHash: '0xabc...', blockNum: 10831, integrity: undefined },
    { id: 2, time: '2023-10-19', memo: '스마트폰', amount: 68000, txHash: '0xdef...', blockNum: 7981, integrity: undefined }, 
    { id: 3, time: '2023-10-18', memo: '사진', amount: 68000, txHash: null, blockNum: 22491, integrity: undefined },
    { id: 4, time: '2023-10-18', memo: '커피', amount: 35000, txHash: '0x123...', blockNum: 2020, integrity: undefined },
    { id: 5, time: '2023-10-18', memo: '휴대폰', amount: 350, txHash: '0x456...', blockNum: 1332, integrity: undefined },
    { id: 6, time: '2023-10-18', memo: '무결심', amount: 68000, txHash: null, blockNum: 3839, integrity: undefined },
  ];
  
  items.value = apiData;
  loading.value = false;
};

const computeMerkleRoot = (leaf, proof) => {
    let hash = leaf;
    for (const { node, position } of proof) {
        if (position === "left") { hash = SHA256(node + hash).toString(); } 
        else if (position === "right") { hash = SHA256(hash + node).toString(); } 
        else { throw new Error("Invalid proof position"); }
    }
    return hash;
};

const verifyMerkleProof = async tx => {
    if (!contract) initContract();
    error.value = null;

    let isMatch = tx.id % 2 !== 0; // ⚠️ 임시 검증 로직

    const updated = { ...tx, integrity: isMatch };
    items.value = items.value.map(it => it.id===tx.id ? updated : it);
};

// ----------------------------------------------------
// 4. 컴포넌트 라이프사이클
// ----------------------------------------------------

onMounted(() => {
    // 🚨 postData를 계산한 후, ID가 유효할 때만 데이터 로드를 시도합니다.
    if (postData.value.id) {
        fetchTransactions();
        initContract();
    } else {
        error.value = "❌ 게시물 정보(ID) 로드에 실패했습니다.";
    }
});
</script>

<style scoped>
/* 거래내역 표 스타일 */
.ledger-table-container {
  max-width: 900px;
  margin: 40px auto;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  font-family: Arial, sans-serif;
}

/* 🚨 뒤로가기 버튼 스타일 */
.btn-back {
    background: #f0f0f0;
    color: #333;
    border: none;
    padding: 8px 15px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 0.9rem;
    margin-bottom: 20px;
    transition: background-color 0.2s;
    float: left; /* 왼쪽 정렬 */
}

.btn-back:hover {
    background-color: #e0e0e0;
}

/* 제목 헤더 컨테이너 */
.table-info-header {
    display: flex;
    flex-direction: column;
    align-items: flex-end; 
    margin-bottom: 20px;
    width: 100%;
}

.table-title { 
    clear: both; /* 뒤로가기 버튼 float 해제 */
    width: 100%;
    text-align: center; 
    font-size: 18px; 
    font-weight: 600; 
    color: #333; 
    margin-top: 5px; 
    margin-bottom: 15px; 
}

/* 작은 글씨 상세 정보 */
.post-meta-info {
    width: 100%; 
    text-align: right; 
    font-size: 0.85rem; 
    color: #666;
    margin-bottom: 5px;
}

.meta-item {
    display: inline-block;
    margin-left: 15px;
    font-weight: 400;
}

.meta-label {
    font-weight: 600; 
    margin-right: 3px;
}

/* 테이블 기본 및 컨트롤 스타일 */
.table-controls { display: flex; justify-content: flex-end; gap: 10px; margin-bottom: 20px; }
.btn-refresh { padding: 8px 15px; background-color: #007bff; color: white; border: none; border-radius: 4px; cursor: pointer; font-weight: 500; }
.transaction-table { width: 100%; border-collapse: collapse; font-size: 14px; text-align: left; }
.transaction-table th, .transaction-table td { padding: 12px 10px; border-bottom: 1px solid #e0e0e0; }
.transaction-table thead th { background-color: #f7f7f7; font-weight: 600; color: #555; border-top: 1px solid #e0e0e0; }

/* 열별 정렬 */
.amount-cell { text-align: right; font-weight: 700; }
.transaction-table th:nth-child(4), .transaction-table td:nth-child(4) { text-align: center; } 
.transaction-table th:nth-child(5), .transaction-table td:nth-child(5) { text-align: center; } 
.transaction-table th:nth-child(6), .transaction-table td:nth-child(6) { text-align: center; } 
.transaction-table th:nth-child(7), .transaction-table td:nth-child(7) { text-align: center; } 

/* 상태 배지 스타일 */
.badge {display:inline-block;padding:3px 8px;border-radius:999px;font-size:12px;font-weight:600;}
.badge.ok, .status-check {background:#dcfce7;color:#166534;}
.badge.err, .status-fail {background:#fee2e2;color:#991b1b;}
.badge.pend {background:#fff7ed;color:#9a3412;}

/* 무결성 결과 'O', 'X' 스타일 */
.integrity-result {
    display: inline-block;
    font-weight: 700;
    font-size: 1.1em;
    padding: 2px 8px;
    border-radius: 4px;
}

.integrity-result.ok {
    color: #166534; 
    background: #dcfce7;
}

.integrity-result.err {
    color: #991b1b; 
    background: #fee2e2;
}

.integrity-result.muted {
    color: #475569; 
    background: #f1f5f9;
}

.btn-action { background-color: #007bff; color: white; border: none; padding: 6px 10px; border-radius: 4px; cursor: pointer; font-size: 12px; font-weight: 500; }
.btn-action:hover { background-color: #0056b3; }
.error {margin-top:10px;padding:10px;background:#fee2e2;color:#991b1b;border-radius:8px;}
.empty-data {color:#64748b;padding:12px 4px; text-align: center;}
</style>