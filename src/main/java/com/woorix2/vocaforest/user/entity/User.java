package com.woorix2.vocaforest.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer userId;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private LocalDate birthDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private Role role = Role.ROLE_USER;

	public enum Role {
		ROLE_USER, ROLE_ADMIN;
	}
}


