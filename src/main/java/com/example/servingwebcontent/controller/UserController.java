package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.domain.Role;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.UserDto;
import com.example.servingwebcontent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String userList(Model model) {
        String usersName = "users";
        model.addAttribute(usersName, userService.findAll());

        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{userEntity}")
    @ResponseStatus(HttpStatus.OK)
    public String userEditForm(@PathVariable UserEntity userEntity, Model model) {
        model.addAttribute("user", userEntity);
        String rolesName = "roles";
        model.addAttribute(rolesName, Role.values());

        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userEntity") UserEntity userEntity
    ) {
        userService.saveUser(userEntity, username, form);

        return "redirect:/user";
    }

    @GetMapping("profile")
    @ResponseStatus(HttpStatus.OK)
    public String getProfile(Model model, @AuthenticationPrincipal UserDto userDto) {
        String usernameName = "username";
        model.addAttribute(usernameName, userDto.getUsername());
        String emailName = "email";
        model.addAttribute(emailName, userDto.getEmail());

        return "profile";
    }

    @PostMapping("profile")
    @ResponseStatus(HttpStatus.OK)
    public String updateProfile(
            @AuthenticationPrincipal UserDto userDto,
            @RequestParam String password,
            @RequestParam String email
    ) {
        userService.updateProfile(userDto, password, email);

        return "redirect:/user/profile";
    }
}