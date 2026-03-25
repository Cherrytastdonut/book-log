package org.example.booklog.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.booklog.domain.Member;
import org.example.booklog.domain.MemberRepository;
import org.example.booklog.service.MailService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    // 🔍 아이디 중복 체크 API (JS에서 호출하는 용도!)
    @GetMapping("/signup/check-id")
    @ResponseBody
    public boolean checkId(@RequestParam String username) {
        return memberRepository.existsByUsername(username);
    }

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    // 💥 핵심: 하나로 합친 회원가입 처리 로직이다 이말이야!
    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         HttpSession session,
                         Model model) {

        // 1. 이메일 중복 체크 (기존 계정 안내 팝업용)
        if (memberRepository.existsByEmail(email)) {
            Member existingMember = memberRepository.findByEmail(email).orElseThrow();
            model.addAttribute("existingId", existingMember.getUsername());
            model.addAttribute("error", "email_exists");
            return "signup"; // 팝업 띄우려고 가입 페이지로 다시 보낸다 유남생?!?
        }

        // 2. 아이디 중복 체크 (최종 방어선)
        if (memberRepository.existsByUsername(username)) {
            return "redirect:/signup?error=id";
        }

        // 3. 쫀득하게 인증 메일 발송
        String vCode = mailService.sendVerificationEmail(email);

        // 4. 임시 회원 정보 생성 및 비밀번호 암호화
        Member tempMember = new Member();
        tempMember.setUsername(username);
        tempMember.setEmail(email);
        tempMember.setPassword(passwordEncoder.encode(password));

        // 5. 세션에 임시 저장 (인증 전까지 DB 안 넣어!)
        session.setAttribute("tempMember", tempMember);
        session.setAttribute("vCode", vCode);

        return "redirect:/verify";
    }

    @GetMapping("/verify")
    public String verifyForm() {
        return "verify";
    }

    @PostMapping("/verify")
    public String verifyCode(@RequestParam String inputCode, HttpSession session) {
        String vCode = (String) session.getAttribute("vCode");
        Member tempMember = (Member) session.getAttribute("tempMember");

        if (vCode != null && vCode.equals(inputCode)) {
            memberRepository.save(tempMember); // 드디어 DB 저장!
            session.removeAttribute("vCode");
            session.removeAttribute("tempMember");
            return "redirect:/login?success";
        }

        return "redirect:/verify?error";
    }
}