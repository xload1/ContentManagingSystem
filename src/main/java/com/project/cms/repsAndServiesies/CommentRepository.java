package com.project.cms.repsAndServiesies;

import com.project.cms.entities.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comments, Integer> {
    @Query("SELECT p FROM Comments p WHERE p.post_id = :post_id")
    List<Comments> findCommentsByPost_id(@Param("post_id") int post_id);
}
