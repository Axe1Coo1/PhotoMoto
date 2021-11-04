package com.example.servingwebcontent.service;

import com.example.servingwebcontent.controller.ControllerUtils;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.CaptchaResponseDto;
import com.example.servingwebcontent.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final static String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserService userService;

    @Value("${recaptcha.secret}")
    private String secret;

    @Transactional
    public String addUser(
            String captchaResponse,
            UserEntity userEntity,
            BindingResult bindingResult,
            Model model
    ) {
        String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        CaptchaResponseDto response = restTemplate.postForObject(
                url,
                Collections.emptyList(),
                CaptchaResponseDto.class
        );

        if (!response.isSuccess()) {
            model.addAttribute("captchaError", "Fill the captcha");
        }

        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        String registrationName = "registration";
        if (userDto.getPassword() != null && !userDto.getPassword().equals(userDto.getPassword2())) {
            String passwordErrorName = "passwordError";
            String passwordAreDifferentName = "Passwords are different!";
            model.addAttribute(passwordErrorName, passwordAreDifferentName);
            return registrationName;
        }
        if (bindingResult.hasErrors() || !response.isSuccess()) {
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
