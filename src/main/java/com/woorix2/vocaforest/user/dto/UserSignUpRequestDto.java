package com.woorix2.vocaforest.user.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
public class UserSignUpRequestDto {
	private String email;
	private String password;
	private String name;
	private String birthYear;
	private String birthMonth;
	private String birthDay;

	public String getBirthDate() {
		return String.format("%s-%02d-%02d",
				birthYear,
				Integer.parseInt(birthMonth),
				Integer.parseInt(birthDay));
	}
}
