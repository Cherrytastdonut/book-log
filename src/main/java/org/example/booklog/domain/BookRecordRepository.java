package org.example.booklog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRecordRepository extends JpaRepository<BookRecord, Long> {
    // 최신순 정렬 (기본)
    List<BookRecord> findAllByOrderByCreatedAtDesc();
    // 오래된순 정렬
    List<BookRecord> findAllByOrderByCreatedAtAsc();
    // 조회수순 정렬
    List<BookRecord> findAllByOrderByViewCountDesc();
}