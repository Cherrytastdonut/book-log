package org.example.booklog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByUsername(String username); // 아이디 중복 확인!
    boolean existsByEmail(String email);       // 이메일 중복 확인!
    Optional<Member> findByUsername(String username); // 로그인할 때 쓸 거다 유남생?!?

    Optional<Member> findByEmail(String email);
}