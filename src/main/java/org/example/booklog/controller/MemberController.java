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
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm() {
        // 💥 아무것도 묻지도 따지지도 말고 로그인 페이지나 보여줘라 유남생?!?
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         HttpSession session) {

        if (memberRepository.existsByUsername(username)) return "redirect:/signup?error=id";
        if (memberRepository.existsByEmail(email)) return "redirect:/signup?error=email";

        String vCode = mailService.sendVerificationEmail(email);

        Member tempMember = new Member();
        tempMember.setUsername(username);
        tempMember.setEmail(email);
        tempMember.setPassword(passwordEncoder.encode(password));

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
            memberRepository.save(tempMember);
            session.removeAttribute("vCode");
            session.removeAttribute("tempMember");
            return "redirect:/login?success";
        }

        return "redirect:/verify?error";
    }
}