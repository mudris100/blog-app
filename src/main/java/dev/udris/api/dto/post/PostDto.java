package dev.udris.api.dto.post;

import java.time.LocalDateTime;
import java.util.List;

import dev.udris.api.dto.CommentDto;
import dev.udris.entity.PostStatus;

public record PostDto(Long postId, String title, String content, LocalDateTime publishedOn, PostStatus status,
		String tags, List<CommentDto> comments) {

}
