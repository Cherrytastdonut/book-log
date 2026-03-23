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
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // 💥 핵심: /** 를 붙여서 모든 하위 경로와 파라미터(?error 등)를 싹다 열어라!
                        .requestMatchers("/", "/login/**", "/signup/**", "/verify/**", "/css/**", "/js/**", "/uploads/**").permitAll()
                        // 🔒 특정 기능만 로그인 체크하겠다 이말이야!
                        .requestMatchers("/write/**", "/edit/**", "/delete/**", "/view/**").authenticated()
                        .anyRequest().permitAll() // 👈 나머지도 일단 다 열어줘서 튕기는 걸 원천 차단해라!
                )
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}