package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.Valid;

@Controller


public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;


    @GetMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    public String addUser(@Valid UserEntity userEntity, BindingResult bindingResult, Model model) {
        return registrationService.addUser(userEntity, bindingResult, model);
    }

    @GetMapping("/activate/{code}")
    @ResponseStatus(HttpStatus.OK)
    public String activate(Model model, @PathVariable String code) {
        return registrationService.activateUser(model, code);
    }
}