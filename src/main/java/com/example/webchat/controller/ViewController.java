package com.example.webchat.controller;

import com.example.webchat.model.User;
import com.example.webchat.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@AllArgsConstructor
@Controller
public class ViewController {

    private final UserService userService;

    @GetMapping("/card")
    public ModelAndView card() {

        log.info("Card attempt for user: get card ");
        return new ModelAndView("card");
    }

    @GetMapping("/billing")
    public ModelAndView  billing() {

        log.info("Billing attempt for user: get billing ");
        return new ModelAndView("billing");
    }


    @GetMapping("/login")
    public ModelAndView login() {

        log.info("Login attempt for user: ");
        return new ModelAndView("login");
    }

    @GetMapping("/singup")
    public ModelAndView singup() {

        log.info("Singup attempt for user: ");
        return new ModelAndView("singup");
    }

    @GetMapping("/profile")
    public ModelAndView profile() {

        log.info("Profile attempt for user: ");
        return new ModelAndView("profile");
    }

    @GetMapping("/bio")
    public ModelAndView bio() {

        log.info("Bio attempt for user: ");
        return new ModelAndView("bio");
    }

    @GetMapping("/")
    public String home(Model model) {

        User user = userService.getAuthenticatedUser();
        if (user == null) {
            System.out.println("User is not authenticated");
            return "redirect:/login"; // Redirect to login page if user is not authenticated
        }

        return "index";
    }


    @GetMapping("/chat")
    public ModelAndView getChat() {

        log.info("Chat attempt for user: get chat ");
        return new ModelAndView("chat");
    }

    @GetMapping("/twofactor")
    public ModelAndView twofactor() {

        log.info("Two factor attempt for user: get two factor ");
        return new ModelAndView("twofasecurity");
    }


    @GetMapping("/search/chat")
    public ModelAndView searchChat() {

        log.info("Search chat attempt for user: get search chat ");
        return new ModelAndView("search/searchchat");
    }

    @GetMapping("/search/user")
    public ModelAndView searchUser() {

        log.info("Search chat attempt for user: get search chat ");
        return new ModelAndView("search/searchuser");
    }

    @GetMapping("/profile/message")
    public ModelAndView profileMessages() {

        log.info("Profile messages attempt for user: get profile messages ");
        return new ModelAndView("profile/message");
    }

}
