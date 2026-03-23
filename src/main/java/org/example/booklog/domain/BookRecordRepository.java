package org.example.booklog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookRecordRepository extends JpaRepository<BookRecord, Long> {
    // 특정 사용자의 글만 정렬해서 가져오는 쌈뽕한 메서드들이지 이말이여!
    List<BookRecord> findAllByMemberOrderByCreatedAtDesc(Member member);
    List<BookRecord> findAllByMemberOrderByCreatedAtAsc(Member member);
    List<BookRecord> findAllByMemberOrderByViewCountDesc(Member member);
}