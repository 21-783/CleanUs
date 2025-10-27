package com.example.Cleanus.batch;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface BatchRepository extends JpaRepository<Batch,Long> {
    // Pending 상태의 Batch를 상대로 봉인 조건을 확인할 수 있음 or Sealed 상태의 Batch를 상대로 앵커링 요청을 보내 줄 수 있음.
    List<Batch> findByStatus(BatchStatus status);

    // 마지막 봉인 시점을 알아내서, 새로 봉인하려는 Batch의 기준점을 설정 가능.
    Optional<Batch> findTopByLedgerIdOrderByAtDesc(Long ledgerId);

    // 특정 시점 이후 생성된 배치들을 조회하여, '최근 앵커링 내역 보기' 등 주로 Frontend UI 기능으로 사용.
    List<Batch> findByLedgerIdAndToAtAfter(Long ledgerId, Instant after);
}
