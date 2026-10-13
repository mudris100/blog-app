package dev.udris.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.udris.entity.Post;
import dev.udris.entity.PostStatus;
import dev.udris.entity.User;
import dev.udris.repository.UserRepository;
import dev.udris.service.PostService;
import dev.udris.service.UserService;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private final UserService userService;
    private final PostService postService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.seed-data:true}")
    private boolean seedData;

    public DataSeeder(UserService userService, PostService postService, UserRepository userRepository) {
        this.userService = userService;
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        if (!seedData) {
            return;
        }

        if (!userRepository.existsByIdNotNull()) {
            seedUsers();
            seedPosts();
        }

    }

    private void seedUsers() throws IOException {
        List<User> users = readJson("users.json", new TypeReference<>() {
        });
        userService.createAllUsers(users);
        log.info("Users seeded successfully ({} users)", users.size());
    }

    private void seedPosts() throws IOException {
        List<PostDTO> postDtos = readJson("posts.json", new TypeReference<List<PostDTO>>() {
        });

        // Preload authors once instead of one query per post
        Set<String> usernames = postDtos.stream()
                .map(PostDTO::authorUsername)
                .collect(Collectors.toSet());
        Map<String, User> usersByUsername = userService.findAllByUsernames(usernames).stream()
                .collect(Collectors.toMap(User::getUsername, Function.identity()));

        List<Post> posts = postDtos.stream()
                .map(dto -> {
                    User author = usersByUsername.get(dto.authorUsername());
                    if (author == null) {
                        throw new IllegalStateException(
                                "No user found for username: " + dto.authorUsername());
                    }
                    return new Post(dto.title(), dto.content(), PostStatus.PUBLISHED, author);
                })
                .toList();

        postService.createAllPosts(posts);
        log.info("Posts seeded successfully ({} posts)", posts.size());
    }

    private <T> T readJson(String classpathFile, TypeReference<T> typeRef) throws IOException {
        try (InputStream inputStream = new ClassPathResource(classpathFile).getInputStream()) {
            return objectMapper.readValue(inputStream, typeRef);
        }
    }

    private record PostDTO(String title, String content, String authorUsername) {
    }
}