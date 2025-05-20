package com.woorix2.vocaforest.user.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender mailSender;
	private final Map<String, String> verificationMap = new ConcurrentHashMap<>();

	//메일 인증번호 발송
	public void sendVerificationCode(String email) {
		String code = String.valueOf((int) (Math.random() * 900000) + 100000); // 6자리 인증코드
		verificationMap.put(email, code);

		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

			helper.setFrom("woori8806@naver.com"); // 네이버 SMTP는 반드시 발신자 주소 명시
			helper.setTo(email);
			helper.setSubject("어휘의 숲 이메일 인증코드");
			helper.setText("다음 인증코드를 인증번호 입력란에 입력해주세요. : " + code);

			mailSender.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("메일 전송 실패: " + e.getMessage());
		}
	}

	//인증번호 확인
	public boolean verifyCode(String email, String code) {
		String savedCode = verificationMap.get(email);
		return savedCode != null && savedCode.equals(code);
	}

	public void sendResetLink(String to, String link) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

			helper.setFrom("woori8806@naver.com");
			helper.setTo(to);
			helper.setSubject("[어휘의 숲] 비밀번호 재설정 안내");
			helper.setText("안녕하세요. 비밀번호를 재설정하려면 아래 링크를 클릭해주세요:\n" + link);

			mailSender.send(message);
		} catch (Exception e) {
			throw new RuntimeException("이메일 전송 실패", e);
		}
	}
}
