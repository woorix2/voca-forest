package com.woorix2.vocaforest.user.service;

import com.woorix2.vocaforest.user.dto.*;
import com.woorix2.vocaforest.user.entity.User;
import com.woorix2.vocaforest.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	//회원가입
	public void signUp(UserSignUpRequestDto dto) {
		// 이메일 중복 확인
		log.info(dto.getEmail());
		Optional<User> existingUser = userRepository.findByEmail(dto.getEmail());
		log.info("기존 유저 존재 여부: " + existingUser.isPresent());
		existingUser.ifPresent(user -> {
			throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		});

		// 비밀번호 암호화
		String encodedPassword = passwordEncoder.encode(dto.getPassword());
		log.info(String.valueOf(encodedPassword));
		// 생년월일 파싱
		LocalDate birthDate = LocalDate.parse(dto.getBirthDate());
		log.info(String.valueOf(birthDate));
		// 사용자 생성 및 저장
		User user = User.builder()
				.email(dto.getEmail())
				.password(encodedPassword)
				.name(dto.getName())
				.birthDate(birthDate)
				.build();

		userRepository.save(user);
	}

	//로그인
	public User login(LoginRequestDto dto) {
		User user = userRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
		}

		return user;
	}

	// 아이디(이메일) 찾기
	public Optional<User> findEmail(FindEmailRequestDto dto) {
		return userRepository.findByNameAndBirthDate(dto.getName(), dto.getBirthDate());
	}

	// 비밀번호 찾기
	public Optional<User> findUserForPasswordReset(FindPasswordRequestDto dto) {
		return userRepository.findByEmailAndNameAndBirthDate(
				dto.getEmail(),
				dto.getName(),
				dto.getBirthDate()
		);
	}

	// 비밀번호 재설정
	@Transactional
	public boolean resetPassword(String email, String newPassword) {
		Optional<User> userOpt = userRepository.findByEmail(email);

		if (userOpt.isPresent()) {
			User user = userOpt.get();
			user.setPassword(passwordEncoder.encode(newPassword)); // 암호화
			userRepository.save(user);
			return true;
		} else {
			return false;
		}
	}

	// 이메일로 사용자 조회
	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("해당 이메일로 가입된 사용자가 없습니다."));
	}

}
