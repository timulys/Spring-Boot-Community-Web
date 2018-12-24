package me.timulys.SpringBootCommunityWeb.controller;

import me.timulys.SpringBootCommunityWeb.annotation.SocialUser;
import me.timulys.SpringBootCommunityWeb.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping(value="/{facebook|google|kakao}/complete") // 1
    public String loginComplete(@SocialUser User user) {
        return "redirect:/board/list";
    }
}
