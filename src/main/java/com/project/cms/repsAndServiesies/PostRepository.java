package com.project.cms.repsAndServiesies;

import com.project.cms.entities.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Posts, Integer> {
    @Query("SELECT p FROM Posts p WHERE p.author_login = :login")
    List<Posts> findPostsByLogin(@Param("login") String login);
}
