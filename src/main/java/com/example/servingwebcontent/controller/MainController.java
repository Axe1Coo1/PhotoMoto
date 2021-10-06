package com.example.servingwebcontent.controller;

import com.example.servingwebcontent.domain.MessageEntity;
import com.example.servingwebcontent.domain.User;
import com.example.servingwebcontent.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/")
    public String greeting(Map<String, Object> model) {
        return "greeting";
    }

    @GetMapping("/main")
    public String findMessages(@RequestParam(required = false, defaultValue = "") String filter, Model model) {
        return messageService.mainFindAll(filter, model);
    }

    //add swagger
    @PostMapping("/main")
    public String addMessage(@AuthenticationPrincipal User user,
                             @Valid MessageEntity messageEntity,
                             BindingResult bindingResult,
                             Model model,
                             @RequestParam("file") MultipartFile file) throws IOException {
        return messageService.addMessages(user, messageEntity, bindingResult, model, file);
    }


}