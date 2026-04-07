package org.example.booklog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BookRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String mainImageUrl; // 목록용 썸네일 경로

    @ElementCollection
    private List<String> subImageUrls = new ArrayList<>(); // 본문 사진들

    private Long viewCount = 0L;

    @Column(updatable = false) // 수정해도 날짜 안 바뀌게!
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // BookRecord.java 파일 안의 기존 필드들 밑에 추가해라!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // DB 외래키 이름이지 이말이여
    private Member member;

// BookRecord.java 내부 필드에 추가해라!

    // 책 표지로 쓸 이미지는 mainImageUrl을 그대로 쓰면 되는 거군!!
    // 그리고 이 책이 어느 폴더(책장)에 소속되어 있는지 연결해라!!
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;
}