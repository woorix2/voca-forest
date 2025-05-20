package com.woorix2.vocaforest.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequestDto {
	private String email;
	private String token;
	private String password;
}
