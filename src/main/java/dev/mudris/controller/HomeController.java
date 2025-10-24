package dev.mudris.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import dev.mudris.dto.post.PostSummaryDto;
import dev.mudris.service.PostService;

@Controller
public class HomeController {

    private final PostService postService;

    public HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/blog")
    public String home(Model model) {
        List<PostSummaryDto> posts = postService.findAllPublished();
        model.addAttribute("posts", posts);
        return "posts/list";
    }

    @GetMapping("/")
    public String portfolio() {
        return "portfolio";
    }
} 