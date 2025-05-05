package com.woorix2.vocaforest.wordbook.repository;

import com.woorix2.vocaforest.user.entity.User;
import com.woorix2.vocaforest.word.entity.Word;
import com.woorix2.vocaforest.wordbook.entity.Wordbook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordbookRepository extends JpaRepository<Wordbook, Long> {

	boolean existsByUserAndWord(User user, Word word);

	Optional<Wordbook> findByUserAndWord(User user, Word word);


	List<Wordbook> findByUser(User user);

	@Query("SELECT w FROM Wordbook w WHERE w.user.userId = :userId")
	List<Wordbook> findByUserId(@Param("userId") Integer userId);

}
