package com.woorix2.vocaforest.config;

import com.woorix2.vocaforest.user.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final CustomUserDetailsService customUserDetailsService;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(csrf -> csrf.disable()) // 필요시 활성화 가능
				.authorizeHttpRequests(authz -> authz
						.requestMatchers("/", "/main", "/signup", "/login", "/find-email", "/find-password", "/reset-password", "/todayword", "/random-word", "/synonyms", "/css/**", "/js/**", "/img/**").permitAll()
						.requestMatchers("/upload-todayword", "/today/register").hasRole("ADMIN") // 관리자만
						.anyRequest().authenticated() // 나머지는 로그인 필요
				)
				.formLogin(form -> form
						.loginPage("/login") // GET 요청
						.loginProcessingUrl("/login") // POST 요청 → 여기로 요청 보내야 시큐리티가 가로챔
						.usernameParameter("email") // 로그인 폼에서 이메일 파라미터 이름
						.passwordParameter("password") // 로그인 폼에서 비밀번호 파라미터 이름
						.defaultSuccessUrl("/main", true) // 로그인 성공시 이동
						.failureHandler((request, response, exception) -> {
							response.sendRedirect("/login?error=true");
						})
						.permitAll()
				)
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/main")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID", "saveId")
				)
				.userDetailsService(customUserDetailsService);

		return http.build();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
