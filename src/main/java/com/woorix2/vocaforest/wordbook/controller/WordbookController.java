package com.woorix2.vocaforest.wordbook.controller;

import com.woorix2.vocaforest.user.security.CustomUserDetails;
import com.woorix2.vocaforest.word.service.WordService;
import com.woorix2.vocaforest.wordbook.dto.WordbookDto;
import com.woorix2.vocaforest.wordbook.service.WordbookService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wordbook")
public class WordbookController {

	private final WordbookService wordbookService;
	private final WordService wordService;

	// 단어장 저장
	@PostMapping
	public ResponseEntity<?> saveWord(@RequestBody Map<String, String> body) {
		String wordName = body.get("word");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || (authentication.getPrincipal() instanceof String)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Integer userId = userDetails.getUserId();

		wordService.getWordInfo(wordName).ifPresent(wordDto -> {
			wordbookService.save(userId, wordDto.getWord());
		});

		return ResponseEntity.ok().build();
	}

	// 단어장 삭제
	@DeleteMapping("/{word}")
	public ResponseEntity<?> removeWord(@PathVariable("word") String word) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || (authentication.getPrincipal() instanceof String)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		}

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Integer userId = userDetails.getUserId();

		wordbookService.delete(userId, word);
		return ResponseEntity.ok().build();
	}

	// 단어장 목록 조회
	@GetMapping
	public String wordbookPage(@RequestParam(name = "page", defaultValue = "0") int page, Model model, HttpSession session) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated() || (authentication.getPrincipal() instanceof String)) {
			return "redirect:/login";
		}

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		Integer userId = userDetails.getUserId();

		List<WordbookDto> wordbookList = wordbookService.getWordbookListDto(userId);
		int pageSize = 9;
		int start = page * pageSize;
		int end = Math.min(start + pageSize, wordbookList.size());

		List<WordbookDto> currentPageList = wordbookList.subList(start, end);

		model.addAttribute("wordbookList", currentPageList);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", (wordbookList.size() + pageSize - 1) / pageSize);

		return "wordbook";
	}
}
