package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.domain.MessageEntity;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.service.MessageService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class MainController {

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
        return  messageService.mainFindAll(filter, model);
    }

    @PostMapping("/main")
    @ResponseStatus(HttpStatus.OK)
    @SneakyThrows
    public String addMessage(@AuthenticationPrincipal UserEntity userEntity,
                             @Valid MessageEntity messageEntity,
                             BindingResult bindingResult,
                             Model model,
                             @RequestParam("file") MultipartFile file){
        return messageService.addMessages(userEntity, messageEntity, bindingResult, model, file);
    }


}