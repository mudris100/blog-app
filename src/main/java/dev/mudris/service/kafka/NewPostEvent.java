package dev.mudris.service.kafka;

public record NewPostEvent(Long postId, String title, String authorName) {
}
