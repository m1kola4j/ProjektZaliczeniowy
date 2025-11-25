package org.example.gym_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Strona główna pod "/"
    @GetMapping("/")
    public String home() {
        return "index";  // szuka templates/index.html
    }

    // (opcjonalnie) /index też pokazuje stronę główną
    @GetMapping("/index")
    public String index() {
        return "index";
    }
}
