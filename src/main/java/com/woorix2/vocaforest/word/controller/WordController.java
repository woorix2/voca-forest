package com.woorix2.vocaforest.word.controller;

import com.woorix2.vocaforest.todayword.dto.TodayWordForm;
import com.woorix2.vocaforest.todayword.entity.TodayWord;
import com.woorix2.vocaforest.todayword.service.TodayWordService;
import com.woorix2.vocaforest.user.entity.User;
import com.woorix2.vocaforest.user.security.CustomUserDetails;
import com.woorix2.vocaforest.word.dto.WordDto;
import com.woorix2.vocaforest.word.entity.Word;
import com.woorix2.vocaforest.word.service.ChatGPTService;
import com.woorix2.vocaforest.word.service.DictionaryService;
import com.woorix2.vocaforest.word.service.SearchHistoryService;
import com.woorix2.vocaforest.word.service.WordService;
import com.woorix2.vocaforest.wordbook.service.WordbookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class WordController {
	private final WordService wordService;
	private final ChatGPTService chatGPTService;
	private final DictionaryService dictionaryService;
	private final SearchHistoryService searchHistoryService;
	private final WordbookService wordbookService;
	private final TodayWordService todayWordService;

	@GetMapping("/main")
	public String mainPage(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		boolean isLoggedIn = authentication != null && authentication.isAuthenticated()
				&& !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"));

		model.addAttribute("isLoggedIn", isLoggedIn);

		if (isLoggedIn) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

			model.addAttribute("userRole", userDetails.getRole());
			model.addAttribute("userName", userDetails.getName());
			model.addAttribute("userEmail", userDetails.getEmail());

			List<String> wordbookList = wordbookService.getWordbookList(userDetails.getUserId())
					.stream()
					.map(Word::getWord)
					.toList();
			model.addAttribute("wordbookList", wordbookList);
		} else {
			model.addAttribute("wordbookList", List.of());
		}

		// 랜덤 단어 20개
		List<WordDto> wordDto = todayWordService.getRandomTodayWords(20);
		model.addAttribute("wordList", wordDto);

		return "main";
	}

	// 유사어 검색
	@PostMapping("/synonyms")
	public ResponseEntity<Map<String, Object>> getSynonyms(@RequestBody Map<String, String> body) {
		String word = body.get("word");

		// 🔥 스프링 시큐리티로 현재 로그인 사용자 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		boolean isLoggedIn = authentication != null && authentication.isAuthenticated()
				&& !(authentication.getPrincipal() instanceof String && authentication.getPrincipal().equals("anonymousUser"));

		String userEmail = null;
		if (isLoggedIn) {
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			userEmail = userDetails.getUsername(); // 이메일 추출
		}

		WordDto inputWordDto = null;
		Optional<WordDto> optional = wordService.getWordInfo(word);

		if (optional.isPresent()) {
			inputWordDto = optional.get();
		} else {
			Optional<WordDto> dicInfo = dictionaryService.getWordInfoFromDictionary(word);

			if (dicInfo.isPresent()) {
				WordDto dto = dicInfo.get();
				wordService.save(dto);
				inputWordDto = dto;
			} else {
				return ResponseEntity.badRequest()
						.body(Map.of("error", "입력하신 단어는 표준국어대사전에 등록되어 있지 않습니다."));
			}
		}

		if (userEmail != null) {
			// 🔥 로그인한 사용자 이메일로 최근 검색어 저장
			searchHistoryService.saveRecentSearch(userEmail, word);
		}

		// GPT 결과 처리와 응답 구성은 그대로
		List<String> gptWords = chatGPTService.findSynonyms(word);
		List<WordDto> filteredSynonyms = new ArrayList<>();
		for (String gptWord : gptWords) {
			dictionaryService.getWordInfoFromDictionary(gptWord)
					.ifPresent(dto -> {
						filteredSynonyms.add(dto);
						wordService.save(dto);
					});
		}

		List<String> wordList = filteredSynonyms.stream()
				.map(WordDto::getWord)
				.toList();
		List<Word> existingWords = wordService.findByWordIn(wordList);

		Map<String, Word> dbMap = existingWords.stream()
				.collect(Collectors.toMap(Word::getWord, Function.identity()));

		List<WordDto> finalResult = new ArrayList<>();
		for (WordDto dto : filteredSynonyms) {
			if (dbMap.containsKey(dto.getWord())) {
				Word w = dbMap.get(dto.getWord());
				finalResult.add(new WordDto(w.getWord(), w.getPartSpeech(), w.getMeaning(), w.getExampleSentence()));
			} else {
				finalResult.add(dto);
			}
		}

		Map<String, Object> response = new HashMap<>();
		response.put("wordInfo", inputWordDto);
		response.put("synonyms", finalResult);

		return ResponseEntity.ok(response);
	}

	//유사어 검색
	@PostMapping("/synonyms/by-word")
	public ResponseEntity<List<WordDto>> getSynonymsByWord(@RequestBody Map<String, String> body) {
		String word = body.get("word");

		// GPT 유사어 단어명 리스트
		List<String> gptWords = chatGPTService.findSynonyms(word);

		// 표준국어대사전 API에서 각 단어 정보 가져오기
		List<WordDto> filteredSynonyms = new ArrayList<>();
		for (String gptWord : gptWords) {
			dictionaryService.getWordInfoFromDictionary(gptWord).ifPresent(dto -> {
				filteredSynonyms.add(dto);
				try {
					wordService.save(dto); // 중복은 무시됨
				} catch (Exception e) { /* 무시 */ }
			});
		}

		// 3. words 테이블에서 일괄 조회
		List<String> wordList = filteredSynonyms.stream()
				.map(WordDto::getWord)
				.toList();
		//words테이블에 단어 여부 조회
		List<Word> existingWords = wordService.findByWordIn(wordList);

		Map<String, Word> dbMap = existingWords.stream()
				.collect(Collectors.toMap(Word::getWord, Function.identity()));

		// 4. DB에 있는 단어는 덮어쓰기
		List<WordDto> finalResult = new ArrayList<>();

		for (WordDto dto : filteredSynonyms) {
			if (dbMap.containsKey(dto.getWord())) {
				Word w = dbMap.get(dto.getWord());
				finalResult.add(new WordDto(w.getWord(), w.getPartSpeech(), w.getMeaning(), w.getExampleSentence()));
			} else {
				finalResult.add(dto); // 국어사전 API 정보 그대로 사용
			}
		}
		System.out.println("finalResult : " + finalResult);

		return ResponseEntity.ok(finalResult);
	}

	// 최근 검색어 불러오기
	@GetMapping("/recent-search")
	@ResponseBody
	public List<String> getRecentSearches() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
				|| authentication.getPrincipal().equals("anonymousUser")) {
			throw new IllegalStateException("로그인한 사용자만 조회 가능합니다.");
		}

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		return searchHistoryService.getRecentSearches(userDetails.getEmail());
	}

	//최근 검색어에 저장
	@PostMapping("/save-search")
	@ResponseBody
	public void saveSearch(@RequestBody Map<String, String> body) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() ||	(authentication.getPrincipal() instanceof String)) {
			// 로그인 안한 사용자는 저장하지 않음
			return;
		}

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String email = userDetails.getUsername();
		String word = body.get("word");

		if (word != null && !word.trim().isEmpty()) {
			searchHistoryService.saveRecentSearch(email, word);
		}
	}

	//최근 검색어 삭제
	@PostMapping("/remove-search")
	@ResponseBody
	public ResponseEntity<String> removeRecentSearch(@RequestBody Map<String, String> body) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || (authentication.getPrincipal() instanceof String)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}

		// Principal 꺼내서 CustomUserDetails로 캐스팅
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String userEmail = userDetails.getUserEmail();  // ✅ userId 꺼내기

		String word = body.get("word");
		searchHistoryService.removeSearch(userEmail, word);
		return ResponseEntity.ok("OK");
	}

	//나만의 단어장 목록 조회
	@GetMapping("/list")
	@ResponseBody
	public List<String> getWordbookList() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || (authentication.getPrincipal() instanceof String)) {
			throw new IllegalStateException("로그인한 사용자만 조회 가능합니다.");
		}

		// Principal 꺼내서 CustomUserDetails로 캐스팅
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Integer userId = userDetails.getUserId();  // ✅ userId 꺼내기

		return wordbookService.getWordbookList(userId)
				.stream()
				.map(Word::getWord) // Word 객체 → 단어명(String)으로 변환
				.toList();
	}

	//오늘의 단어 등록 페이지
	@GetMapping("/upload-todayword")
	public String uploadWordPage(Model model, RedirectAttributes redirectAttributes) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || (authentication.getPrincipal() instanceof String)) {
			redirectAttributes.addFlashAttribute("errorMessage", "로그인이 필요합니다.");
			return "redirect:/login";
		}

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String userRole = userDetails.getUserRole();

		if (!"ROLE_ADMIN".equals(userRole)) { // 문자열 비교는 이렇게
			redirectAttributes.addFlashAttribute("errorMessage", "관리자만 접근 가능한 페이지입니다.");
			return "redirect:/main";
		}

		// 오늘의 단어 가장 최근 날짜 조회
		LocalDate latestDate = todayWordService.getLatestDate();
		model.addAttribute("latestDate", latestDate);

		return "upload-todayword";
	}

	//오늘의 단어 등록
	@PostMapping("/today/register")
	public String registerTodayWord(@ModelAttribute TodayWordForm form, RedirectAttributes redirectAttributes) {
		try {
			WordDto wordDto = new WordDto(
					form.getWord(),
					form.getPartSpeech(),
					form.getMeaning(),
					form.getExampleSentence()
			);
			LocalDate date = LocalDate.of(form.getYear(), form.getMonth(), form.getDay());
			todayWordService.registerTodayWord(wordDto, date, form.getComment());

			return "redirect:/main";

		} catch (IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return "redirect:/upload-todayword";
		}
	}

	//오늘의 단어 조회
	@GetMapping("/todayword")
	@ResponseBody
	public ResponseEntity<?> getTodayWord() {
		LocalDate today = LocalDate.now();
		Optional<TodayWord> todayWordOpt = todayWordService.getTodayWord(today);

		if (todayWordOpt.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		TodayWord todayWord = todayWordOpt.get();
		Word word = todayWord.getWord();

		Map<String, Object> response = new HashMap<>();
		response.put("word", word.getWord());
		response.put("partSpeech", word.getPartSpeech());
		response.put("meaning", word.getMeaning());
		response.put("exampleSentence", word.getExampleSentence());
		response.put("comment", todayWord.getComment());

		return ResponseEntity.ok(response);
	}

	// 과거의 오늘의 단어 조회
	@GetMapping("/todayword/history")
	public String viewPastTodayWords(@RequestParam(name = "page", defaultValue = "0") int page, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");

		List<String> userWordbook = List.of();

		if (user != null) {
			userWordbook = wordbookService.getWordbookList(user.getUserId())
					.stream()
					.map(Word::getWord)
					.toList();
		}

		List<TodayWord> pastWords = todayWordService.getPastTodayWords();

		int pageSize = 9;
		int start = page * pageSize;
		int end = Math.min(start + pageSize, pastWords.size());

		List<TodayWord> currentPageList = pastWords.subList(start, end);

		model.addAttribute("todaywordList", currentPageList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", (pastWords.size() + pageSize - 1) / pageSize);

		model.addAttribute("userWordbook", userWordbook); // ✅ 추가: 단어장 정보도 같이 넘기기
		System.out.println("userWordbook :" + userWordbook);
		model.addAttribute("isLoggedIn", user != null);
		return "todayword";
	}

	// 최근 검색어 불러오기
	@GetMapping("/random-word")
	@ResponseBody
	public WordDto getRandomWord() {
		return wordService.findRandomWord();
	}
}
