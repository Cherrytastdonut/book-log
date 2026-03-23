package org.example.booklog.controller;

import org.example.booklog.domain.BookRecord;
import org.example.booklog.domain.BookRecordRepository;
import org.example.booklog.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookRecordRepository repository;
    private final FileService fileService; // 위치 옮겼다 이말이야!

    @GetMapping("/")
    public String home(@RequestParam(defaultValue = "latest") String sort, Model model) {
        List<BookRecord> books;
        if (sort.equals("oldest")) books = repository.findAllByOrderByCreatedAtAsc();
        else if (sort.equals("views")) books = repository.findAllByOrderByViewCountDesc();
        else books = repository.findAllByOrderByCreatedAtDesc();

        model.addAttribute("books", books);
        return "home";
    }

    @GetMapping("/view/{id}")
    public String viewRecord(@PathVariable Long id, Model model) {
        BookRecord record = repository.findById(id).orElseThrow();
        record.setViewCount(record.getViewCount() + 1);
        repository.save(record);
        model.addAttribute("book", record);
        return "view";
    }

    @GetMapping("/write")
    public String writeForm() { return "write"; }

    @PostMapping("/write")
    public String writeSave(String title, String content, MultipartFile mainImg, List<MultipartFile> subImgs) throws Exception {
        BookRecord record = new BookRecord();
        record.setTitle(title);
        record.setContent(content);
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
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("book", repository.findById(id).orElseThrow());
        return "edit";
    }

    @PostMapping("/edit/{id}")
    public String editSave(@PathVariable Long id, String title, String content,
                           @RequestParam(value = "deleteImgs", required = false) List<String> deleteImgs,
                           MultipartFile mainImg, List<MultipartFile> subImgs) throws Exception {
        BookRecord record = repository.findById(id).orElseThrow();
        record.setTitle(title);
        record.setContent(content);

        if (mainImg != null && !mainImg.isEmpty()) {
            fileService.deleteFile(record.getMainImageUrl()); // 기존파일 삭제!
            record.setMainImageUrl(fileService.saveFile(mainImg));
        }

        if (deleteImgs != null) {
            for (String imgUrl : deleteImgs) {
                fileService.deleteFile(imgUrl); // 진짜 삭제!
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

    // 💥 이게 없어서 화이트 페이지 뜬 거다 이말이야! 💥
    @PostMapping("/delete/{id}")
    public String deleteRecord(@PathVariable Long id) {
        BookRecord record = repository.findById(id).orElseThrow();
        fileService.deleteFile(record.getMainImageUrl());
        for (String url : record.getSubImageUrls()) fileService.deleteFile(url);
        repository.delete(record);
        return "redirect:/"; // 목록으로 쌈뽕하게 이동!
    }
}