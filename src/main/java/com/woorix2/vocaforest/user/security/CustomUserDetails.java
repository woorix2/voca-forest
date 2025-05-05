package com.woorix2.vocaforest.user.security;

import com.woorix2.vocaforest.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {
	private final Integer userId;
	private final String email;
	private final String password;
	private final String name;
	private final User.Role role;

	public CustomUserDetails(User user) {
		this.userId = user.getUserId();
		this.email = user.getEmail();
		this.password = user.getPassword();
		this.name = user.getName();
		this.role = user.getRole();
	}

	public String getUserEmail() {
		return email;
	}

	public String getUserRole() {
		return role.name();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getPassword() {
		return password; // 암호화된 비밀번호
	}

	@Override
	public String getUsername() {
		return email; // 로그인 아이디 = 이메일
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
