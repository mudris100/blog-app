package dev.mudris.controller;

import dev.mudris.dto.CommentDto;
import dev.mudris.dto.post.CreatePostDto;
import dev.mudris.dto.post.PostDto;
import dev.mudris.dto.post.PostSummaryDto;
import dev.mudris.entity.Comment;
import dev.mudris.entity.Post;
import dev.mudris.entity.User;
import dev.mudris.oauth2.CustomUserPrincipal;
import dev.mudris.service.CommentService;
import dev.mudris.service.PostService;
import dev.mudris.service.TagService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final TagService tagService;

    public PostController(PostService postService, CommentService commentService,
                          TagService tagService) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
    }

    @GetMapping()
    public String allPublishedPostsByAuthor(@AuthenticationPrincipal CustomUserPrincipal principal, Model model) {
        User author = principal.getUser();
        List<PostSummaryDto> postDto = postService.getPublishedPostsByAuthor(author);
        model.addAttribute("posts", postDto);
        return "posts/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("post", new CreatePostDto());
        model.addAttribute("availableTags", tagService.findAll());
        return "posts/form";
    }

    @PostMapping
    public String createPost(@ModelAttribute CreatePostDto postDto, @AuthenticationPrincipal CustomUserPrincipal principal) {
        User author = principal.getUser();
        postService.createPostWithTags(postDto, author);
        return "redirect:/users/profile";

    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        postService.deletePostById(id);
        return "redirect:/users/profile";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        PostDto postDto = postService.findPostDtoById(id);
        model.addAttribute("post", postDto);
        model.addAttribute("availableTags", tagService.findAll());
        return "posts/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, @ModelAttribute PostDto postDto, @AuthenticationPrincipal CustomUserPrincipal principal) {
        postService.updateWithTags(id, postDto, postDto.tags(), principal.getName());
        return "redirect:/users/profile";
    }

    @PostMapping("/{id}/publish")
    public String publishPost(@PathVariable Long id, @AuthenticationPrincipal CustomUserPrincipal principal) {
        postService.publishPost(id, principal.getName());
        return "redirect:/users/profile";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserPrincipal principal) {
        Post post = postService.findPostByIdOrThrow(id);

        if (principal == null || !post.getAuthor().getUsername().equals(principal.getName())) {
            if (post.getStatus() != dev.mudris.entity.PostStatus.PUBLISHED) {
                throw new AccessDeniedException("This post is not published.");
            }
        }

        List<CommentDto> comments = commentService.findByPost(post);
        model.addAttribute("post", post);
        model.addAttribute("comments", comments);
        model.addAttribute("comment", new CommentDto(null, null, null, null, null, null));
        if (principal != null) {
            model.addAttribute("currentUsername", principal.getName());
        }
        return "posts/view";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id, @ModelAttribute("comment") CommentDto commentDto,
                             @AuthenticationPrincipal CustomUserPrincipal principal) {
        Post post = postService.findPostByIdOrThrow(id);
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(commentDto.content());
        if (principal != null) {
            User user = principal.getUser();
            comment.setAuthor(user);
            comment.setName(user.getUsername());
        }
        commentService.createComment(comment);
        return "redirect:/posts/" + id;
    }
}
