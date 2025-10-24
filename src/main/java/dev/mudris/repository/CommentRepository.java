package dev.mudris.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.mudris.entity.Comment;
import java.util.List;
import dev.mudris.entity.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByPost(Post post);
}
