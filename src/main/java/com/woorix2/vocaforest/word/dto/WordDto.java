package com.woorix2.vocaforest.word.dto;

import com.woorix2.vocaforest.word.entity.Word;
import lombok.Getter;

@Getter
public class WordDto {
	private String word;
	private String partSpeech;
	private String meaning;
	private String exampleSentence;

	public WordDto(Word word) {
		this.word = word.getWord();
		this.partSpeech = word.getPartSpeech();
		this.meaning = word.getMeaning();
		this.exampleSentence = word.getExampleSentence();
	}

	// ChatGPT 응답용 생성자
	public WordDto(String word, String partSpeech, String meaning, String exampleSentence) {
		this.word = word;
		this.partSpeech = partSpeech;
		this.meaning = meaning;
		this.exampleSentence = exampleSentence;
	}
}
