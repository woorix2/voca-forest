package com.woorix2.vocaforest.word.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatGPTService {
	@Value("${openai.api.key}")
	private String apiKey;

	//유사어 찾기
	public List<String> findSynonyms(String word) {
		String prompt = String.format("""
				입력된 단어와 의미가 유사한 순수 한국어 단어를 1~5개 추천해줘.
							
				❗조건:
				- 입력 단어와 똑같은 단어는 결과에서 제외하세요
				- 입력 단어와 같은 어근(접두사/접미사 포함)에서 파생된 단어는 절대 포함하지 마세요
				- 접두사·접미사에서 파생된 단어는 제외
				- 국립국어대사전에 등록된 단어만
				- 외래어, 신조어, 방언 제외
				- 의미가 너무 멀거나 반대인 단어 제외
							
				형식 (항목 없이, 줄마다 단어명만 응답해줘):
				단어1  
				단어2  
				단어3  
				...
							
				입력 단어: %s
				""", word);

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(apiKey);
		headers.setContentType(MediaType.APPLICATION_JSON);

		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("model", "gpt-3.5-turbo");
		requestBody.put("temperature", 0.3);
		requestBody.put("messages", List.of(
				Map.of("role", "user", "content", prompt)
		));

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

		ResponseEntity<Map> response = restTemplate.postForEntity(
				"https://api.openai.com/v1/chat/completions",
				entity,
				Map.class
		);

		List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
		Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

		String gptResponse = message.get("content").toString();
		// 줄바꿈 기준으로 분리
		return Arrays.stream(gptResponse.split("\\R"))
				.map(String::trim)
				.filter(line -> !line.isEmpty())
				.collect(Collectors.toList());
	}

}
