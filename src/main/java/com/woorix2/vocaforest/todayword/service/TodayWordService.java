package com.woorix2.vocaforest.todayword.service;

import com.woorix2.vocaforest.todayword.entity.TodayWord;
import com.woorix2.vocaforest.todayword.repository.TodayWordRepository;
import com.woorix2.vocaforest.word.dto.WordDto;
import com.woorix2.vocaforest.word.entity.Word;
import com.woorix2.vocaforest.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodayWordService {
	private final TodayWordRepository todayWordRepository;
	private final WordRepository wordRepository;

	// 랜덤 20개 가져오기
	public List<WordDto> getRandomTodayWords(int count) {
		List<TodayWord> allTodayWords = todayWordRepository.findAll();

		Collections.shuffle(allTodayWords); // 리스트를 랜덤 섞기

		return allTodayWords.stream()
				.limit(count)
				.map(tw -> {
					var w = tw.getWord();
					return new WordDto(
							w.getWord(),
							w.getPartSpeech(),
							w.getMeaning(),
							w.getExampleSentence()
					);
				})
				.collect(Collectors.toList());
	}

	//오늘의 단어 등록
	@Transactional
	public void registerTodayWord(WordDto wordDto, LocalDate date, String comment) {
		// (1) 날짜 중복 검사
		if (todayWordRepository.existsByDate(date)) {
			throw new IllegalStateException("이미 해당 날짜에 등록된 단어가 있습니다.");
		}

		// (2) 단어명으로 words 테이블 조회
		Optional<Word> optionalWord = wordRepository.findByWord(wordDto.getWord());

		Word word;
		if (optionalWord.isPresent()) {
			// 이미 존재하는 단어
			word = optionalWord.get();
		} else {
			// 존재하지 않으면 새로 저장
			word = Word.builder()
					.word(wordDto.getWord())
					.partSpeech(wordDto.getPartSpeech())
					.meaning(wordDto.getMeaning())
					.exampleSentence(wordDto.getExampleSentence())
					.build();
			wordRepository.save(word);
		}

		// (3) today_words에 저장
		TodayWord todayWord = TodayWord.builder()
				.word(word)
				.date(date)
				.comment(comment)
				.build();
		todayWordRepository.save(todayWord);
	}

	// 오늘 날짜에 해당하는 오늘의 단어 조회
	public Optional<TodayWord> getTodayWord(LocalDate today) {
		return todayWordRepository.findByDate(today);
	}

	// 과거의 오늘의 단어 조회
	public List<TodayWord> getPastTodayWords() {
		LocalDate today = LocalDate.now();
		return todayWordRepository.findAllByDateBeforeOrderByDateDesc(today);
	}

	// 가장 최근 날짜 조회
	public LocalDate getLatestDate() {
		return todayWordRepository.findLatestDate()
				.orElse(null); // 등록된게 없으면 null
	}
}
