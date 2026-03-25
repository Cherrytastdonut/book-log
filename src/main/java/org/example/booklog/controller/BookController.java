package org.example.booklog.controller;

import lombok.RequiredArgsConstructor;
import org.example.booklog.domain.BookRecord;
import org.example.booklog.domain.BookRecordRepository;
import org.example.booklog.domain.Member;
import org.example.booklog.domain.MemberRepository;
import org.example.booklog.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import org.example.booklog.domain.BookRecord;
import org.example.booklog.domain.BookRecordRepository;
import org.example.booklog.domain.Member;
import org.example.booklog.domain.MemberRepository;
import org.example.booklog.service.FileService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList; // 빈 리스트용이지 이말이여
import java.util.List;
@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookRecordRepository repository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "latest") String sort, Model model, Principal principal) {
        List<BookRecord> books = new ArrayList<>(); // 기본은 빈 리스트!

        if (principal != null) {
            // 로그인 한 브로만 자기 책 가져오기!
            Member member = memberRepository.findByUsername(principal.getName()).orElseThrow();
            if (sort.equals("oldest")) books = repository.findAllByMemberOrderByCreatedAtAsc(member);
            else if (sort.equals("views")) books = repository.findAllByMemberOrderByViewCountDesc(member);
            else books = repository.findAllByMemberOrderByCreatedAtDesc(member);
        }
        // 로그인 안 했으면 books는 빈 상태로 나가는 거지 이말이야!

        model.addAttribute("books", books);
        return "home";
    }

    @GetMapping("/view/{id}")
    public String viewRecord(@PathVariable Long id, Model model, Principal principal) {
        BookRecord record = repository.findById(id).orElseThrow();

        // 🔒 남의 글@을 보려고 하면 뼈@ 때려야지?!?
        if (!record.getMember().getUsername().equals(principal.getName())) {
            throw new RuntimeException("네 글@이 아니다 이말이야 유남생?!?");
        }

        record.setViewCount(record.getViewCount() + 1);
        repository.save(record);
        model.addAttribute("book", record);
        return "view";
    }

    @GetMapping("/write")
    public String writeForm() { return "write"; }

    @PostMapping("/write")
    public String writeSave(String title, String content, MultipartFile mainImg,
                            List<MultipartFile> subImgs, Principal principal) throws Exception {

        Member member = memberRepository.findByUsername(principal.getName()).orElseThrow();

        BookRecord record = new BookRecord();
        record.setTitle(title);
        record.setContent(content);
        record.setMember(member); // 👈 여기서 사용자 정보를 쫀득@하게 박아주는 거지 ㅇㅇ

        record.setMainImageUrl(fileService.saveFile(mainImg));
        if (subImgs != null) {
            for (MultipartFile img : subImgs) {
                String path = fileService.saveFile(img);
                if (path != null) record.getSubImageUrls().add(path);
            }
        }
        repository.save(record);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        BookRecord record = repository.findById(id).orElseThrow();
        if (!record.getMember().getUsername().equals(principal.getName())) {
            return "redirect:/";
        }
        model.addAttribute("book", record);
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String editSave(@PathVariable Long id, String title, String content,
                           @RequestParam(value = "deleteImgs", required = false) List<String> deleteImgs,
                           MultipartFile mainImg, List<MultipartFile> subImgs, Principal principal) throws Exception {

        BookRecord record = repository.findById(id).orElseThrow();
        // 수정도 본인@만 가능하게 찰@지게 막아라!
        if (!record.getMember().getUsername().equals(principal.getName())) return "redirect:/";

        record.setTitle(title);
        record.setContent(content);

        if (mainImg != null && !mainImg.isEmpty()) {
            fileService.deleteFile(record.getMainImageUrl());
            record.setMainImageUrl(fileService.saveFile(mainImg));
        }

        if (deleteImgs != null) {
            for (String imgUrl : deleteImgs) {
                fileService.deleteFile(imgUrl);
                record.getSubImageUrls().remove(imgUrl);
            }
        }

        if (subImgs != null) {
            for (MultipartFile img : subImgs) {
                String path = fileService.saveFile(img);
                if (path != null) record.getSubImageUrls().add(path);
            }
        }
        repository.save(record);
        return "redirect:/view/" + id;
    }

    @PostMapping("/delete/{id}")
    public String deleteRecord(@PathVariable Long id, Principal principal) {
        BookRecord record = repository.findById(id).orElseThrow();
        if (!record.getMember().getUsername().equals(principal.getName())) return "redirect:/";

        fileService.deleteFile(record.getMainImageUrl());
        for (String url : record.getSubImageUrls()) fileService.deleteFile(url);
        repository.delete(record);
        return "redirect:/";
    }
}