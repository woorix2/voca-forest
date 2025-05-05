package com.woorix2.vocaforest.word.service;

import com.woorix2.vocaforest.todayword.entity.TodayWord;
import com.woorix2.vocaforest.todayword.repository.TodayWordRepository;
import com.woorix2.vocaforest.word.dto.WordDto;
import com.woorix2.vocaforest.word.entity.Word;
import com.woorix2.vocaforest.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordService {
	private final WordRepository wordRepository;
	private final TodayWordRepository todayWordRepository;

	//단어 조회
	public Optional<WordDto> getWordInfo(String word) {
		return wordRepository.findByWord(word).map(WordDto::new);
	}

	//단어 저장
	public void save(WordDto dto) {
		if (wordRepository.findByWord(dto.getWord()).isEmpty()) {
			wordRepository.save(new Word(dto.getWord(), dto.getPartSpeech(), dto.getMeaning(), dto.getExampleSentence()));
		}
	}

	public List<Word> findByWordIn(List<String> wordList) {
		List<Word> existingWords = wordRepository.findByWordIn(wordList);

		return existingWords;
	}

	// 랜덤 단어 1개
	public WordDto findRandomWord() {
		List<Word> words = wordRepository.findRandomWords(1);
		if (words.isEmpty()) throw new RuntimeException("랜덤 단어가 없습니다.");
		return new WordDto(words.get(0));
	}

}
