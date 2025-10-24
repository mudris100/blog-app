package dev.mudris.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import dev.mudris.dto.CommentDto;
import dev.mudris.entity.Comment;
import dev.mudris.entity.Post;
import dev.mudris.entity.User;
import dev.mudris.exception.NotFoundException;
import dev.mudris.repository.CommentRepository;

class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private Post post;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1);
        user.setUsername("testuser");
        post = new Post();
        post.setId(1L);
        comment = new Comment("Test content", post, user, user.getUsername());
        comment.setId(1L);
    }

    @Test
    void testCreateComment() {
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        Comment saved = commentService.createComment(comment);
        assertNotNull(saved);
        assertEquals(comment.getContent(), saved.getContent());
    }

    @Test
    void testFindAll() {
        when(commentRepository.findAll()).thenReturn(Arrays.asList(comment));
        List<CommentDto> comments = commentService.findAll();
        assertEquals(1, comments.size());
        assertEquals(comment.getContent(), comments.get(0).content());
    }

    @Test
    void testFindCommentDtoByIdOrThrow_Found() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        CommentDto dto = commentService.findCommentDtoByIdOrThrow(1L);
        assertEquals(comment.getContent(), dto.content());
    }

    @Test
    void testFindCommentDtoByIdOrThrow_NotFound() {
        when(commentRepository.findById(2L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> commentService.findCommentDtoByIdOrThrow(2L));
    }
} 