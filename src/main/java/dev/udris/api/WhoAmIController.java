package dev.udris.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WhoAmIController {

    @GetMapping("/whoami")
    public String whoami() {
        return System.getenv().getOrDefault("HOSTNAME", "unknown");
    }
}
