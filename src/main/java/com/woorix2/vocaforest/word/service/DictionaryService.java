package com.woorix2.vocaforest.word.service;

import com.woorix2.vocaforest.word.dto.WordDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DictionaryService {
	@Value("${dict.api.key}")
	private String apiKey;

	//단어 조회
	public Optional<WordDto> getWordInfoFromDictionary(String word) {
		try {
			// 기본조회
			String searchUrl = "https://stdict.korean.go.kr/api/search.do?key=" + apiKey
					+ "&target_type=search&part=word&q=" + URLEncoder.encode(word, StandardCharsets.UTF_8);

			RestTemplate restTemplate = new RestTemplate();
			String searchXml = restTemplate.getForObject(searchUrl, String.class);

			// <target_code> 추출
			Matcher wordNoMatcher = Pattern.compile("<target_code>(\\d+)</target_code>").matcher(searchXml);
			if (!wordNoMatcher.find()) return Optional.empty();

			String wordNo = wordNoMatcher.group(1);

			// 상세조회
			String viewUrl = "https://stdict.korean.go.kr/api/view.do?key=" + apiKey
					+ "&target_type=view&method=target_code&q=" + wordNo;

			String viewXml = restTemplate.getForObject(viewUrl, String.class);

			String pos = extract(viewXml, "<pos>(.*?)</pos>");

			// 여러 개 추출
			List<String> definitions = extractMultiple(viewXml, "<definition><!\\[CDATA\\[(.*?)]]></definition>");
			// 최대 2개까지만 사용
			String definitionText = IntStream.range(0, Math.min(definitions.size(), 2))
					.mapToObj(i -> (i + 1) + ") " + definitions.get(i))
					.collect(Collectors.joining("  "));

			String example = extract(viewXml, "<example><!\\[CDATA\\[(.*?)]]></example>");

			return Optional.of(new WordDto(word, pos, definitionText, example));
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

	//태그 추출 함수
	private String extract(String xml, String regex) {
		Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(xml);
		if (matcher.find()) return matcher.group(1).replaceAll("<.*?>", "").trim();
		return "-";
	}

	//태그 추출 함수
	private List<String> extractMultiple(String xml, String regex) {
		Matcher matcher = Pattern.compile(regex, Pattern.DOTALL).matcher(xml);
		List<String> results = new ArrayList<>();
		while (matcher.find()) {
			String cleaned = matcher.group(1).replaceAll("<.*?>", "").trim();
			if (!cleaned.isEmpty()) results.add(cleaned);
		}
		return results;
	}
}
