package dev.mudris.dto.post;

import java.time.LocalDateTime;

import dev.mudris.entity.PostStatus;

public record UpdatePostDto(Long id, String title, String content, LocalDateTime updatedOn, PostStatus status) {

}
