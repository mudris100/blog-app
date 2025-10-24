package dev.mudris.controller;

import dev.mudris.dto.post.PostDto;
import dev.mudris.entity.User;
import dev.mudris.oauth2.CustomUserPrincipal;
import dev.mudris.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UserController {

    private final PostService postService;

    public UserController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/profile")
    public String userProfile(Model model, @AuthenticationPrincipal CustomUserPrincipal principal) {
        User user = principal.getUser();
        List<PostDto> posts = postService.findByAuthor(user);
        model.addAttribute("user", user);
        model.addAttribute("posts", posts);
        return "user/profile";
    }
}
