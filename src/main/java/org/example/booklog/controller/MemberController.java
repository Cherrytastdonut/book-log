package org.example.booklog.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.booklog.domain.Member;
import org.example.booklog.domain.MemberRepository;
import org.example.booklog.service.MailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder; // SecurityConfig 설정 후 사용 가능!

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         HttpSession session) {

        // 1. 아이디 & 이메일 중복 체크 (뼈 때리는 팩폭!)
        if (memberRepository.existsByUsername(username)) return "redirect:/signup?error=id";
        if (memberRepository.existsByEmail(email)) return "redirect:/signup?error=email";

        // 2. 이메일 인증 번호 발송 (MailService 아까 만든 거 쓰지 이말이야!)
        String vCode = mailService.sendVerificationEmail(email);

        // 3. 회원 정보 임시 저장 (인증 전까지 DB에 안 넣는다 유남생?!?)
        Member tempMember = new Member();
        tempMember.setUsername(username);
        tempMember.setEmail(email);
        // 비밀번호는 암호화해서 저장해야 쌈뽕하다! (Security 연동 필수)
        tempMember.setPassword(passwordEncoder.encode(password));

        session.setAttribute("tempMember", tempMember);
        session.setAttribute("vCode", vCode);

        return "redirect:/verify"; // 인증번호 입력 페이지로 궈궈!
    }
}