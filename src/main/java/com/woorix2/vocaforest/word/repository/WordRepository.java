package com.woorix2.vocaforest.word.repository;

import com.woorix2.vocaforest.word.dto.WordDto;
import com.woorix2.vocaforest.word.entity.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

	// 랜덤단어 조회
	@Query(value = "SELECT * FROM words ORDER BY RAND() LIMIT :limit", nativeQuery = true)
	List<Word> findRandomWords(@Param("limit") int limit);

	// 단어 상세
	Optional<Word> findByWord(String word);

	// 단어 상세
	List<Word> findByWordIn(List<String> words);
}
