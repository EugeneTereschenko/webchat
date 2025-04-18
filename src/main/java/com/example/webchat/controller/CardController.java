package com.example.webchat.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@AllArgsConstructor
@Controller
public class CardController {

    @GetMapping("/card")
    public ModelAndView singup() {
        // Perform login logic here
        // For example, you can save the username in the session or perform authentication

        log.info("Login attempt for user: get card ");
        return new ModelAndView("card");
    }

}
