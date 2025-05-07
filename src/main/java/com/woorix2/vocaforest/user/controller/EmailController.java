package com.woorix2.vocaforest.user.controller;

import com.woorix2.vocaforest.user.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

	private final EmailService emailService;

	//인증 코드 전송
	@PostMapping("/send-code")
	public ResponseEntity<String> sendCode(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		emailService.sendVerificationCode(email);
		return ResponseEntity.ok("인증코드가 전송되었습니다.");
	}

	//인증번호 확인
	@PostMapping("/verify-code")
	public ResponseEntity<Map<String, Object>> verifyCode(@RequestBody Map<String, String> body) {
		String email = body.get("email");
		String code = body.get("code");
		boolean result = emailService.verifyCode(email, code);
		return ResponseEntity.ok(Map.of("verified", result));
	}
}
