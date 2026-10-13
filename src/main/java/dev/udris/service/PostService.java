package dev.udris.service;

import dev.udris.api.errors.NotFoundException;
import dev.udris.api.dto.CommentDto;
import dev.udris.api.dto.post.CreatePostDto;
import dev.udris.api.dto.post.PostDto;
import dev.udris.api.dto.post.PostSummaryDto;
import dev.udris.api.dto.post.UpdatePostDto;
import dev.udris.entity.Post;
import dev.udris.entity.PostStatus;
import dev.udris.entity.Tag;
import dev.udris.entity.User;
import dev.udris.mapper.PostMapper;
import dev.udris.repository.PostRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository repository;
    private final CommentService commentService;
    private final TagService tagService;
    private final PostMapper mapper;

    public PostService(PostRepository repository, CommentService commentService, TagService tagService, PostMapper mapper) {
        this.repository = repository;
        this.commentService = commentService;
        this.tagService = tagService;
        this.mapper = mapper;
    }

    public long count() {
        return repository.count();
    }

    public List<PostDto> findByAuthor(User author) {
        return repository.findByAuthor(author).stream().map(p -> mapper.toPostDto(p, commentService, tagService)).collect(Collectors.toList());
    }

    public void deletePostById(Long id) {
        repository.deleteById(id);
    }

    public List<PostSummaryDto> getPublishedPostsByAuthor(User author) {
        List<Post> posts = repository.findByAuthorAndStatus(author, PostStatus.PUBLISHED);
        return posts.stream().map(mapper::toSummaryDto).collect(Collectors.toList());
    }

    public List<PostSummaryDto> findAllPublished() {
        List<Post> posts = repository.findByStatusOrderByPublishedOnDesc(PostStatus.PUBLISHED);
        return posts.stream().map(mapper::toSummaryDto).collect(Collectors.toList());
    }

    public PostDto findPostDtoById(Long id) {
        Post post = findPostByIdOrThrow(id);
        return mapper.toPostDto(post, commentService, tagService);
    }

    public PostDto findPublishedById(Long id) {
        Post post = findPostByIdOrThrow(id);
        if (post.getStatus() != PostStatus.PUBLISHED) {
            throw new NotFoundException("Post", "id", id);
        }
        return mapper.toPostDto(post, commentService, tagService);
    }

    public Post findPostByIdOrThrow(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Post", "id", id));
    }

    public Post createPost(Post post) {
        return repository.save(post);
    }

    public Post createPostWithTags(CreatePostDto postDto, User author) {
        Post post = mapper.toEntity(postDto);
        post.setAuthor(author);
        Set<Tag> tags = tagService.parseTagsFromString(postDto.getTags());
        post.setTags(tags);
        repository.save(post);
        return post;

    }

    public UpdatePostDto update(Long id, Post updatedPost) {
        Post existingPost = findPostByIdOrThrow(id);
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setStatus(updatedPost.getStatus());
        existingPost.setUpdatedOn(LocalDateTime.now());
        Post post = repository.save(existingPost);
        return mapper.toUpdatePostDto(post);
    }

    public Post updateWithTags(Long id, PostDto updatedPost, String tagsString, String username) {
        Post existingPost = findPostByIdOrThrow(id);
        if (!existingPost.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to update this post.");
        }
        existingPost.setTitle(updatedPost.title());
        existingPost.setContent(updatedPost.content());
        existingPost.setStatus(updatedPost.status());
        existingPost.setUpdatedOn(LocalDateTime.now());

        Set<Tag> tags = tagService.parseTagsFromString(tagsString);
        existingPost.setTags(tags);

        return repository.save(existingPost);
    }

    public void publishPost(Long id, String username) {
        Post existingPost = findPostByIdOrThrow(id);
        if (!existingPost.getAuthor().getUsername().equals(username)) {
            throw new AccessDeniedException("You are not allowed to publish this post.");
        }
        existingPost.setStatus(PostStatus.PUBLISHED);
        existingPost.setUpdatedOn(LocalDateTime.now());
        repository.save(existingPost);
    }


    public List<PostDto> getPublishedPostsByTag(PostStatus postStatus, Tag tag) {
        List<Post> posts = repository.findByStatusAndTagsContaining(postStatus, tag);
        return posts.stream().map(p -> mapper.toPostDto(p, commentService, tagService)).collect(Collectors.toList());
    }

    public List<CommentDto> findCommentsByPost(Post post) {
        return commentService.findByPost(post);
    }

    public void createAllPosts(List<Post> posts) {
        repository.saveAll(posts);
    }
}


