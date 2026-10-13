package dev.udris.controller;

import dev.udris.api.dto.UserRegistrationDto;
import dev.udris.api.errors.UsernameAlreadyTakenException;
import dev.udris.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private final UserService service;

    public LoginController(UserService service) {
        this.service = service;
    }

    @Value("${app.oauth.enabled:false}")
    private boolean oauthEnabled;

    @GetMapping("/login")
    public String newUser(@ModelAttribute("dto") UserRegistrationDto dto,
                          @RequestParam(value = "oauth_disabled", required = false) boolean oauthDisabled,
                          Model model) {
        model.addAttribute("oauthEnabled", oauthEnabled);

        if (oauthDisabled) {
            model.addAttribute("oauthDisabledMessage",
                    "OAuth2 Login is Disabled in this environment.");
        }

        return "auth/login";
    }

    @PostMapping("/register")
    public String create(@ModelAttribute("dto") @Valid UserRegistrationDto dto, BindingResult bindingResult,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/login";
        }
        try {
            service.createUser(dto);
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        } catch (UsernameAlreadyTakenException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/login";
    }
}
