package com.woorix2.vocaforest.user.controller;


import com.woorix2.vocaforest.user.dto.FindEmailRequestDto;
import com.woorix2.vocaforest.user.dto.FindPasswordRequestDto;
import com.woorix2.vocaforest.user.dto.ResetPasswordRequestDto;
import com.woorix2.vocaforest.user.dto.UserSignUpRequestDto;
import com.woorix2.vocaforest.user.entity.User;
import com.woorix2.vocaforest.user.service.EmailService;
import com.woorix2.vocaforest.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final EmailService emailService;

	private final RedisTemplate<String, String> redisTemplate;

	//회원가입 페이지
	@GetMapping("/signup")
	public String signupForm() {
		return "signup"; // signup.html로 이동
	}

	//회원가입
	@PostMapping("/signup")
	public String signUp(@ModelAttribute UserSignUpRequestDto dto, Model model, RedirectAttributes redirectAttributes) {
		try {
			userService.signUp(dto);
			redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
			return "redirect:/login"; // 로그인 페이지로 리다이렉트
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("error", e.getMessage());
			return "redirect:/signup"; // 에러 시 다시 폼으로
		}
	}

	//로그인 페이지
	@GetMapping("/login")
	public String loginPage(HttpServletRequest request, Model model) {
		// request에서 message 꺼내서 model에 넣어준다
		Object message = request.getAttribute("message");
		if (message != null) {
			model.addAttribute("message", message);
		}
		return "login";
	}

	// 이메일(아이디 찾기) 페이지
	@GetMapping("find-email")
	public String findEmailForm() {
		return "find-email";
	}

	// 이메일(아이디 찾기)
	@PostMapping("find-email")
	public ResponseEntity<?> findEmail(@RequestBody FindEmailRequestDto dto) {
		Optional<User> user = userService.findEmail(dto);

		if (user.isPresent()) {
			return ResponseEntity.ok(user.get().getEmail());
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 사용자가 없습니다.");
		}
	}

	// 비밀번호 찾기 페이지
	@GetMapping("find-password")
	public String findPasswordForm() {
		return "find-password";
	}

	//비밀번호 찾기
	@PostMapping("/find-password")
	public ResponseEntity<?> findPassword(@RequestBody FindPasswordRequestDto dto) {
		Optional<User> user = userService.findUserForPasswordReset(dto);

		if (user.isEmpty()) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 사용자가 없습니다");
		}

		String token = UUID.randomUUID().toString();
		String email = user.get().getEmail();

		redisTemplate.opsForValue().set("reset:" + token, email, Duration.ofMinutes(15));

		String resetLink = "https://vofa-forest.site/reset-password?token=" + token; //운영
		//String resetLink = "http://localhost:8080/reset-password?token=" + token; //개발
		System.out.println("email " + email);
		emailService.sendResetLink(email, resetLink);
		return ResponseEntity.ok("비밀번호 재설정 링크를 이메일로 전송했습니다.");
	}

	// 비밀번호 재설정 페이지
	@GetMapping("/reset-password")
	public String resetPasswordPage(@RequestParam("token") String token, Model model) {
		String email = redisTemplate.opsForValue().get("reset:" + token);

		if (email == null) {
			model.addAttribute("error", "유효하지 않은 링크이거나 만료되었습니다.");
		}

		model.addAttribute("token", token);
		return "reset-password";
	}

	// 비밀번호 재설정
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto dto) {
		String email = redisTemplate.opsForValue().get("reset:" + dto.getToken());

		if (email == null) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않응 요청입니다.");
		}

		boolean success = userService.resetPassword(email, dto.getPassword());
		if (success) {
			redisTemplate.delete("reset:" + dto.getToken()); // 한 번 사용한 토큰 제거
			return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경 실패");
		}
	}
}
