package com.restlearningjourney.store.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("${application.version:0.0.1}")
    private String version;

    @GetMapping("/")
    public String index(Model model) {
        System.out.println("HomeController.index");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean authenticated =
                auth != null &&
                        auth.isAuthenticated() &&
                        !(auth instanceof AnonymousAuthenticationToken);

        model.addAttribute("name", "Luis");
        model.addAttribute("userAuthenticated", authenticated);
        model.addAttribute("version", version);

        return "index";
    }
}