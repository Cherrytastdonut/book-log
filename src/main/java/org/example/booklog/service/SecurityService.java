package org.example.booklog.service;

import lombok.RequiredArgsConstructor;
import org.example.booklog.domain.Member;
import org.example.booklog.domain.MemberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SecurityService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 🔍 DB에서 사용자를 쫀득하게 찾아라!
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("유저가 없다 이말이야 유남생?!?"));

        // 시큐리티 전용 User 객체로 변환해서 던져줘라!
        return new User(member.getUsername(), member.getPassword(), new ArrayList<>());
    }
}