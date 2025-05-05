package com.woorix2.vocaforest.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
	private String email;
	private String password;
	private boolean rememberId;
}
