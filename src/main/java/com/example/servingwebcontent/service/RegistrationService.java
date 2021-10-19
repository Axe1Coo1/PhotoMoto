package com.example.servingwebcontent.service;

import com.example.servingwebcontent.controller.ControllerUtils;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Transactional
    public String addUser(UserEntity userEntity, BindingResult bindingResult, Model model) {
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        String registrationName = "registration";
        if (userDto.getPassword() != null && !userDto.getPassword().equals(userDto.getPassword2())) {
            String passwordErrorName = "passwordError";
            String passwordAreDifferentName = "Passwords are different!";
            model.addAttribute(passwordErrorName, passwordAreDifferentName);
            return registrationName;
        }
        if (bindingResult.hasErrors()) {
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

    @Transactional
    public String activateUser(Model model, String code) {
        boolean isActivated = userService.activateUser(code);

        String messageName = "message";
        if (isActivated) {
            String userActivatedMessage = "User successfully activated!";
            model.addAttribute(messageName, userActivatedMessage);
        } else {
            String activationCodeIsNotFoundMessage = "Activation code is not found!";
            model.addAttribute(messageName, activationCodeIsNotFoundMessage);
        }


        return "login";
    }
}
