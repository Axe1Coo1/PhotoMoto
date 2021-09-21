package com.example.servingwebcontent.service;

import com.example.servingwebcontent.controller.ControllerUtils;
import com.example.servingwebcontent.domain.Message;
import com.example.servingwebcontent.domain.User;
import com.example.servingwebcontent.repos.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class MessageService {

    @Autowired
    private MessageRepo messageRepo;

    @Value("${upload.path}")
    private String uploadPath;

    String messageFieldName = "message";
    String messagesFieldName = "messages";
    String filterFieldName = "filter";
    String mainReturnedFieldName = "main";


    public String mainFindAll(@RequestParam(required = false, defaultValue = "") String filter, Model model) {

        Iterable<Message> messages;

        if (filter != null && !filter.isEmpty()) {
            messages = messageRepo.findByTag(filter);
        } else {
            messages = messageRepo.findAll();
        }

        model.addAttribute(messagesFieldName, messages);
        model.addAttribute(filterFieldName, filter);


        return mainReturnedFieldName;
    }

    public String addMessages  (User user, Message message, BindingResult bindingResult, Model model, MultipartFile file) throws IOException {
        message.setAuthor(user);

        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute(messageFieldName, message);
        } else {
            if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String uuidFile = UUID.randomUUID().toString();
                String resultFilename = uuidFile + "." + file.getOriginalFilename();

                file.transferTo(new File(uploadPath + "/" + resultFilename));

                message.setFilename(resultFilename);
            }

            model.addAttribute(messageFieldName, null);

            messageRepo.save(message);
        }
        Iterable<Message> messages = messageRepo.findAll();

        model.addAttribute(messagesFieldName, messages);

        return mainReturnedFieldName;
    }
}
