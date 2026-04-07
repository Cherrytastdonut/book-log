package org.example.booklog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class BookRepo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 레포지토리 이름 (예: 2026년 판타지 모음)

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member owner; // 이 작업소의 주인 브@로!!

    // 최상위에 존재하는 폴더들
    @OneToMany(mappedBy = "bookRepo", cascade = CascadeType.ALL)
    private List<Folder> folders = new ArrayList<>();
}