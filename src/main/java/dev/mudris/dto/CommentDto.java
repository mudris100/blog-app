package dev.mudris.dto;

import java.time.LocalDateTime;

public record CommentDto(Long id, String author, String content, LocalDateTime createdAt, LocalDateTime updatedAt, Long postId) {
	
}

