package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.UserDto;
import com.example.servingwebcontent.service.UserService;
import com.example.servingwebcontent.utils.EntityConvertor;
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
import java.util.Map;

@Controller

//google about restExceptionHandler
public class RegistrationController {
    @Autowired
    private UserService userService;


    @GetMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    public String registration() {
        return "registration";
    }

    @PostMapping("/registration")
    @ResponseStatus(HttpStatus.OK)
    public String addUser(@Valid UserEntity userEntity, BindingResult bindingResult, Model model) {
        UserDto userDto = EntityConvertor.ConvertToDto(userEntity);
        String registrationName = "registration";
        if (userDto.getPassword() != null && !userDto.getPassword().equals(userDto.getPassword2())){
            String passwordErrorName = "passwordError";
            String passwordAreDifferentName = "Passwords are different!";
            model.addAttribute(passwordErrorName, passwordAreDifferentName);
            return registrationName;
        }
        if (bindingResult.hasErrors()){
            Map<String, String> errors = ControllerUtils.getErrors(bindingResult);

            model.mergeAttributes(errors);

            return registrationName;
        }
        if (!userService.addUser(userDto)) {
            String usernameErrorMessage = "usernameError";
            model.addAttribute(usernameErrorMessage, "User exists!");
            return registrationName;
        }

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    @ResponseStatus(HttpStatus.OK)
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);

        String messageName = "message";
        if(isActivated) {
            String userActivatedMessage = "User successfully activated!";
            model.addAttribute(messageName, userActivatedMessage);
        }else {
            String activationCodeIsNotFoundMessage = "Activation code is not found!";
            model.addAttribute(messageName, activationCodeIsNotFoundMessage);
        }


        return "login";
    }
}