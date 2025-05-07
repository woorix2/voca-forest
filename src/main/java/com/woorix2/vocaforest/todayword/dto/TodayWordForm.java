package com.woorix2.vocaforest.todayword.dto;

import lombok.Data;

@Data
public class TodayWordForm {
	private int year;
	private int month;
	private int day;

	private String word;
	private String partSpeech;
	private String meaning;
	private String exampleSentence;
	private String comment;
}
