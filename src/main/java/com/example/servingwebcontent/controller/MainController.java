package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.domain.MessageEntity;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.UserDto;
import com.example.servingwebcontent.service.MessageService;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;
import java.util.Set;

@Controller
public class MainController {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageService messageService;

    @GetMapping("/")
    @ResponseStatus(HttpStatus.OK)
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    @ResponseStatus(HttpStatus.OK)
    public String findMessages(@RequestParam(required = false, defaultValue = "") String filter, Model model) {
        return messageService.mainFindAll(filter, model);
    }

    @PostMapping("/main")
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public String addMessage(@AuthenticationPrincipal UserDto userDto,
                             @Valid MessageEntity messageEntity,
                             BindingResult bindingResult,
                             Model model,
                             @RequestParam("file") MultipartFile file) {
        return messageService.addMessages(userDto, messageEntity, bindingResult, model, file);
    }

    @GetMapping("/user-messages/{user}")
    public String userMessages(
            @AuthenticationPrincipal UserDto currentUser,
            @PathVariable UserEntity user,
            @RequestParam(required = false) MessageEntity message,
            Model model

    ) {
        return messageService.getUserMessages(currentUser, user, message, model);
    }

    @PostMapping("/user-messages/{user}")
    public String updateMessages(
            @AuthenticationPrincipal UserDto currentUser,
            @PathVariable Long user,
            @RequestParam("id") MessageEntity message,
            @RequestParam("text") String text,
            @RequestParam("tag") String tag,
            @RequestParam("file") MultipartFile file
    ) {
        return messageService.updateMessage(currentUser, user, message, text, tag, file);
    }




}