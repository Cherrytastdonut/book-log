package org.example.booklog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Folder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 독서 폴더, 일반 폴더 등

    private boolean isBookshelf; // true면 3D 책장이 나타나는 독서 폴더인 거다!!

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookrepo_id")
    private BookRepo bookRepo;

    // 💥 핵심: 부모 폴더 (최상위 폴더면 null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Folder parentFolder;

    // 💥 핵심: 자식 폴더들 (무한 중첩 가능!)
    @OneToMany(mappedBy = "parentFolder", cascade = CascadeType.ALL)
    private List<Folder> subFolders = new ArrayList<>();

    // 이 폴더에 담긴 책 기록들
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL)
    private List<BookRecord> books = new ArrayList<>();
}