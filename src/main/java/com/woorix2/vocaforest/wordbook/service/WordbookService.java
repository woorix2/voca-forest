package com.woorix2.vocaforest.wordbook.service;

import com.woorix2.vocaforest.user.entity.User;
import com.woorix2.vocaforest.user.repository.UserRepository;
import com.woorix2.vocaforest.word.entity.Word;
import com.woorix2.vocaforest.word.repository.WordRepository;
import com.woorix2.vocaforest.wordbook.dto.WordbookDto;
import com.woorix2.vocaforest.wordbook.entity.Wordbook;
import com.woorix2.vocaforest.wordbook.repository.WordbookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WordbookService {

	private final WordbookRepository wordbookRepository;
	private final UserRepository userRepository;
	private final WordRepository wordRepository;


	// 단어장 저장
	public void save(int userId, String wordName) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

		Word word = wordRepository.findByWord(wordName)
				.orElseThrow(() -> new IllegalArgumentException("단어 없음"));

		// 중복 방지 (이미 등록되어 있다면 저장하지 않음)
		if (wordbookRepository.existsByUserAndWord(user, word)) {
			return;
		}

		Wordbook wordbook = Wordbook.builder()
				.user(user)
				.word(word)
				.build();

		wordbookRepository.save(wordbook);
	}

	// 단어장 삭제
	public void delete(int userId, String wordName) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

		Word word = wordRepository.findByWord(wordName)
				.orElseThrow(() -> new IllegalArgumentException("단어 없음"));

		wordbookRepository.findByUserAndWord(user, word).ifPresent(wordbookRepository::delete);
	}

	// 단어장 조회
	/*public List<Word> getWordbookList(int userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

		List<Wordbook> wordbookList = wordbookRepository.findByUser(user);

		// Wordbook 엔티티 안에 Word가 들어있으니까, Word만 뽑아서 리턴
		return wordbookList.stream()
				.map(Wordbook::getWord)
				.collect(Collectors.toList());
	}*/

	// 단어장 조회
	public List<Word> getWordbookList(int userId) {
		List<Wordbook> wordbookList = wordbookRepository.findByUserId(userId);

		return wordbookList.stream()
				.map(Wordbook::getWord)
				.collect(Collectors.toList());
	}

	// 단어장 조회 (DTO 변환)
	public List<WordbookDto> getWordbookListDto(int userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

		List<Wordbook> wordbookList = wordbookRepository.findByUser(user);

		return wordbookList.stream()
				.map(wb -> new WordbookDto(
						wb.getWord().getWord(),
						wb.getWord().getPartSpeech(),
						wb.getWord().getMeaning(),
						wb.getWord().getExampleSentence()
				))
				.collect(Collectors.toList());
	}
}
