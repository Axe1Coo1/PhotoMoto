package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.domain.User;
import com.example.servingwebcontent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Map;

@Controller

//google about restExceptionHandler
public class RegistrationController {
    @Autowired
    private UserService userService;


    @GetMapping("/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(@Valid User user, BindingResult bindingResult, Model model) {
        String registrationName = "registration";
        if (user.getPassword() != null && !user.getPassword().equals(user.getPassword2())){
            String passwordErrorName = "passwordError";
            model.addAttribute(passwordErrorName, "Passwords are different!");
            return registrationName;
        }
        if (bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errors);

            return registrationName;
        }
        if (!userService.addUser(user)) {
            String usernameErrorName = "usernameError";
            model.addAttribute(usernameErrorName, "User exists!");
            return registrationName;
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);

        String messageName = "message";
        if(isActivated) {
            model.addAttribute(messageName, "User successfully activated!");
        }else {
            model.addAttribute(messageName, "Activation code is not found!");
        }


        return "login";
    }
}