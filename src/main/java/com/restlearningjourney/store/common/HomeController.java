package com.restlearningjourney.store.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    //It represents the root of our website
    @RequestMapping("/")
    public String index(Model model ){
        model.addAttribute("name", "Luis");
        return "index";
    }

}
