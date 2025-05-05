package com.woorix2.vocaforest.wordbook.entity;

import com.woorix2.vocaforest.user.entity.User;
import com.woorix2.vocaforest.word.entity.Word;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "wordbook")
public class Wordbook {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "wordbook_id")
	private Integer wordbookId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "word_id", nullable = false)
	private Word word;


}
