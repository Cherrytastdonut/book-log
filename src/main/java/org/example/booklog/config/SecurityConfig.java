package org.example.booklog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 🔒 이게 있어야 'No beans of PasswordEncoder' 에러가 사라진다 이말이야!
        // BCrypt 방식이 가장 쫀득하고 안전한 암호화 방식이지 유남생?!?
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 테스트 편의를 위해 일단 끈다 이말이야!
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/signup", "/verify", "/login", "/css/**", "/js/**", "/uploads/**").permitAll() // 누구나 접근 가능!
                        .anyRequest().authenticated() // 나머지는 로그인해야 드@갈 수 있다 유남생?!?
                )
                .formLogin(login -> login
                        .loginPage("/login") // 로그인 페이지 경로지 이말이여
                        .defaultSuccessUrl("/") // 성공하면 홈으로 궈궈!
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}