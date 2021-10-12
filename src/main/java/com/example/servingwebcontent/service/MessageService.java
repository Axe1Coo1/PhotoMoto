package com.example.servingwebcontent.service;

import com.example.servingwebcontent.controller.ControllerUtils;
import com.example.servingwebcontent.domain.MessageEntity;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.MessageDto;
import com.example.servingwebcontent.repos.MessageRepo;
import com.example.servingwebcontent.utils.EntityConvertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

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

    @Transactional
    public String mainFindAll(@RequestParam(required = false, defaultValue = "") String filter, Model model) {

        List<MessageEntity> messageEntities;
        List<MessageDto> messagesDto;

        if (filter != null && !filter.isEmpty()) {
            messagesDto = messageRepo.findByTag(filter).stream()
                    .map(EntityConvertor::convertToDto)
                    .collect(Collectors.toList());
        } else {
            messageEntities = (List<MessageEntity>) messageRepo.findAll();
            messagesDto = messageEntities.stream()
                    .map(EntityConvertor::convertToDto)
                    .collect(Collectors.toList());
        }

        model.addAttribute(messagesFieldName, messagesDto);
        model.addAttribute(filterFieldName, filter);

        return mainReturnedFieldName;
    }

    @Transactional
    public String addMessages(UserEntity userEntity, MessageEntity messageEntity, BindingResult bindingResult, Model model, MultipartFile file) throws IOException {
        messageEntity.setAuthor(userEntity);
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute(messageFieldName, messageEntity);
        } else {
            if (file != null && !Objects.requireNonNull(file.getOriginalFilename()).isEmpty()) {
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                String uuidFile = UUID.randomUUID().toString();
                String resultFilename = uuidFile + "." + file.getOriginalFilename();

                file.transferTo(new File(uploadPath + "/" + resultFilename));

                messageEntity.setFilename(resultFilename);
            }

            model.addAttribute(messageFieldName, null);

            messageRepo.save(messageEntity);
        }
        List<MessageEntity> messageEntities;
        List<MessageDto> messagesDto;
        messageEntities = (List<MessageEntity>) messageRepo.findAll();
        messagesDto = messageEntities.stream().map(EntityConvertor::convertToDto).collect(Collectors.toList());


        model.addAttribute(messagesFieldName, messagesDto);

        return mainReturnedFieldName;
    }
}
