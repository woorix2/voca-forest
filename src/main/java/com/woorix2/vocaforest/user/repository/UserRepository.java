package com.woorix2.vocaforest.user.repository;

import com.woorix2.vocaforest.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User, Integer> {

	//기존 회원 조회
	Optional<User> findByEmail(String email);

	// 아이디(이메일) 찾기
	Optional<User> findByNameAndBirthDate(String name, LocalDate birthDate);

	//비밀번호 찾기
	Optional<User> findByEmailAndNameAndBirthDate(String email, String name, LocalDate birthDate);

}
