package com.woorix2.vocaforest.todayword.repository;

import com.woorix2.vocaforest.todayword.entity.TodayWord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TodayWordRepository extends JpaRepository<TodayWord, Integer> {

	//오늘의 단어 기존 단어 조회
	boolean existsByDate(LocalDate date);

	//오늘 날짜 단어 조회
	Optional<TodayWord> findByDate(LocalDate date);

	//과거 단어 조회
	List<TodayWord> findAllByDateBeforeOrderByDateDesc(LocalDate date);

	// 가장 최근 날짜 조회
	@Query("SELECT MAX(t.date) FROM TodayWord t")
	Optional<LocalDate> findLatestDate();
}
