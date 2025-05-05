package com.woorix2.vocaforest.user.controller;


import com.woorix2.vocaforest.user.dto.*;
import com.woorix2.vocaforest.user.entity.User;
import com.woorix2.vocaforest.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

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
	public ResponseEntity<?> findPassword(@RequestBody FindPasswordRequestDto dto, HttpSession session) {
		Optional<User> userOpt = userService.findUserForPasswordReset(dto);

		if (userOpt.isPresent()) {
			session.setAttribute("resetEmail", userOpt.get().getEmail()); // 세션에 저장
			return ResponseEntity.ok().build(); // OK 응답
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("일치하는 사용자가 없습니다.");
		}
	}

	// 비밀번호 재설정 페이지
	@GetMapping("/reset-password")
	public String resetPasswordPage(HttpSession session, Model model) {
		String email = (String) session.getAttribute("resetEmail");

		if (email == null) {
			// 세션에 이메일 없으면 비정상 접근 -> 로그인 페이지로 리다이렉트
			return "redirect:/login";
		}

		model.addAttribute("email", email); // 필요하면 숨겨진 input에 넣을 수도 있음
		return "reset-password";
	}

	// 비밀번호 재설정
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequestDto dto, HttpSession session) {
		String email = (String) session.getAttribute("resetEmail");

		if (email == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("비정상적인 요청입니다.");
		}

		boolean success = userService.resetPassword(email, dto.getPassword());

		if (success) {
			session.removeAttribute("resetEmail"); // 비밀번호 변경 후 세션 정리
			return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경 실패");
		}
	}
}
