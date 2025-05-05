package com.woorix2.vocaforest.wordbook.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WordbookDto {
	private String word;
	private String partSpeech;
	private String meaning;
	private String exampleSentence;
}
