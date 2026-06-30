package com.campus.placement_portal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "<h1>🎓 Welcome to CampusHire Placement Portal</h1>" +
               "<p>Your Spring Boot backend is working perfectly!</p>";
    }
}