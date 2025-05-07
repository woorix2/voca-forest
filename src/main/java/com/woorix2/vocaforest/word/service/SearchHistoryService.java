package com.woorix2.vocaforest.word.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchHistoryService {

	private final StringRedisTemplate redisTemplate;

	//검색어 저장
	public void saveRecentSearch(String email, String word) {
		String key = "recent:" + email;

		redisTemplate.opsForList().remove(key, 1, word); // 중복 제거
		redisTemplate.opsForList().leftPush(key, word); // 앞에 추가
		redisTemplate.opsForList().trim(key, 0, 4); // 5개만 유지
	}

	//최근 검색어 조회
	public List<String> getRecentSearches(String email) {
		String key = "recent:" + email;
		return redisTemplate.opsForList().range(key, 0, -1);
	}

	//최근 검색어 삭제
	public void removeSearch(String email, String word) {
		String key = "recent:" + email;
		redisTemplate.opsForList().remove(key, 1, word); // 최근검색어 리스트에서 단어 하나 삭제
	}
}
