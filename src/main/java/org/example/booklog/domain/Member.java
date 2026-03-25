package org.example.booklog.domain; // 본인 패키지 경로 확인해라!

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false) // 중복 ID 방지지 이말이야!
    private String username;

    @Column(nullable = false)
    private String password; // 암호화해서 넣을 거다 유남생?!?

    @Column(unique = true, nullable = false) // 중복 이메일 방지!
    private String email;
    // Member.java 에 추가해라 이말이야!
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookRecord> bookRecords = new ArrayList<>();

    private boolean emailVerified = false; // 이메일 인증 여부지 이말이여 ㅇㅇ
}