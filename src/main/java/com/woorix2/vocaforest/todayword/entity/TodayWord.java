package com.woorix2.vocaforest.todayword.entity;

import com.woorix2.vocaforest.word.entity.Word;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "today_words")
public class       TodayWord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer todayWordId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "word_id", nullable = false)
	private Word word;

	private LocalDate date;

	private String comment; // 감상평 (nullable)
}
