package org.example.booklog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    public String sendVerificationEmail(String toEmail) {
        // 쫀득한 6자리 인증 코드 생성!
        String vCode = UUID.randomUUID().toString().substring(0, 6);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("[나만의 독서기록소] 회원가입 인증 번호다 이말이야!");
        message.setText("브로! 인증 번호는 [" + vCode + "] 이말이야. 똑바로 입력해라 유남생?!?");

        mailSender.send(message);
        return vCode; // 이거 세션이나 DB에 임시 저장해야 검증하겠지?!?
    }
}