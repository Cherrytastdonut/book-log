package org.example.booklog.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.UUID;

@Service
public class FileService {
    // 사진 저장할 물리적 경로 (프로젝트 내 static/uploads 폴더다 이말이야!)
    private final String uploadPath = System.getProperty("user.dir") + "/src/main/resources/static/uploads/";

    // 1. 파일 저장 로직 (이미 한 거지만 다시 체크해라 이말이야!)
    public String saveFile(MultipartFile file) throws Exception {
        if (file == null || file.isEmpty()) return null;

        File folder = new File(uploadPath);
        if (!folder.exists()) folder.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        file.transferTo(new File(uploadPath + fileName));

        return "/uploads/" + fileName;
    }

    // 2. 파일 삭제 로직 (이게 진짜 쫀득한 핵심이다 유남생?!?)
    public void deleteFile(String filePath) {
        if (filePath == null || filePath.isEmpty() || filePath.startsWith("http")) return;

        // DB에 저장된 "/uploads/파일명"을 실제 컴퓨터 경로로 바꾼다 이말이야!
        String fullPath = System.getProperty("user.dir") + "/src/main/resources/static" + filePath;
        File file = new File(fullPath);

        if (file.exists()) {
            if (file.delete()) {
                System.out.println("✅ 파일 삭제 성공했다 이말이야: " + filePath);
            } else {
                System.out.println("❌ 파일 삭제 실패했다 이말이야: " + filePath);
            }
        } else {
            System.out.println("⚠️ 삭제할 파일이 존재하지 않는다 이말이지: " + fullPath);
        }
    }
}