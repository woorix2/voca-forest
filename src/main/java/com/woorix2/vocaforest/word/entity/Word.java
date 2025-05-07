package com.woorix2.vocaforest.word.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "words")
public class Word {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer word_id;

	@Column(name = "word", unique = true, nullable = false)
	private String word;       // 단어 이름

	@Column(name = "part_speech")
	private String partSpeech; // 품사

	@Column(name = "meaning")
	private String meaning;    // 의미

	@Column(name = "example_sentence")
	private String exampleSentence;    // 예시문장

	public Word(String word, String partSpeech, String meaning, String exampleSentence) {
		this.word = word;
		this.partSpeech = partSpeech;
		this.meaning = meaning;
		this.exampleSentence = exampleSentence;
	}
}