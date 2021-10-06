package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.domain.Role;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String userList(Model model) {
        String usersName = "users";
        model.addAttribute(usersName, userService.findAll());

        return "userList";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("{userEntity}")
    public String userEditForm(@PathVariable UserEntity userEntity, Model model) {
        model.addAttribute("user", userEntity);
        String rolesName = "roles";
        model.addAttribute(rolesName, Role.values());

        return "userEdit";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    public String userSave(
            @RequestParam String username,
            @RequestParam Map<String, String> form,
            @RequestParam("userEntity") UserEntity userEntity
    ) {
        userService.saveUser(userEntity, username, form);

        return "redirect:/user";
    }

    @GetMapping("profile")
    public String getProfile(Model model, @AuthenticationPrincipal UserEntity userEntity) {
        String usernameName = "username";
        model.addAttribute(usernameName, userEntity.getUsername());
        String emailName = "email";
        model.addAttribute(emailName, userEntity.getEmail());

        return "profile";
    }

    @PostMapping("profile")
    public String updateProfile(
            @AuthenticationPrincipal UserEntity userEntity,
            @RequestParam String password,
            @RequestParam String email
    ) {
        userService.updateProfile(userEntity, password, email);

        return "redirect:/user/profile";
    }
}