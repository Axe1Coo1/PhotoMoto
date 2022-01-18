package com.example.servingwebcontent.service;

import com.example.servingwebcontent.controller.ControllerUtils;
import com.example.servingwebcontent.domain.MessageEntity;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.MessageDto;
import com.example.servingwebcontent.dto.UserDto;
import com.example.servingwebcontent.repos.MessageRepo;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
    private ModelMapper modelMapper;

    @Autowired
    private MessageRepo messageRepo;


    private final String uploadPath = "/" + new File("src/main/resources/uploads")
            .getAbsolutePath()
            .replace("\\", "/");

    String messageFieldName = "message";
    String messagesFieldName = "messages";
    String filterFieldName = "filter";
    String mainReturnedFieldName = "main";

    private void saveFile(MessageEntity messageEntity, MultipartFile file) throws IOException {
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
    }

    @Transactional
    public String mainFindAll(@RequestParam(required = false, defaultValue = "") String filter, Model model) {

        List<MessageEntity> messageEntities;
        List<MessageDto> messagesDto;

        if (filter != null && !filter.isEmpty()) {
            messagesDto = messageRepo.findByTag(filter).stream()
                    .map(o -> modelMapper.map(o, MessageDto.class))
                    .collect(Collectors.toList());
        } else {
            messageEntities = (List<MessageEntity>) messageRepo.findAll();
            messagesDto = messageEntities.stream()
                    .map(o -> modelMapper.map(o, MessageDto.class))
                    .collect(Collectors.toList());
        }

        model.addAttribute(messagesFieldName, messagesDto);
        model.addAttribute(filterFieldName, filter);

        return mainReturnedFieldName;
    }

    @Transactional
    public String addMessages(UserDto userDto, MessageEntity messageEntity, BindingResult bindingResult,
                              Model model, MultipartFile file) throws IOException {
        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        messageEntity.setAuthor(userEntity);
        if (bindingResult.hasErrors()) {
            Map<String, String> errorsMap = ControllerUtils.getErrors(bindingResult);
            model.mergeAttributes(errorsMap);
            model.addAttribute(messageFieldName, messageEntity);
        } else {
            saveFile(messageEntity, file);

            model.addAttribute(messageFieldName, null);

            messageRepo.save(messageEntity);
        }
        List<MessageEntity> messageEntities;
        List<MessageDto> messagesDto;
        messageEntities = (List<MessageEntity>) messageRepo.findAll();
        messagesDto = messageEntities.stream()
                .map(o -> modelMapper.map(o, MessageDto.class))
                .collect(Collectors.toList());


        model.addAttribute(messagesFieldName, messagesDto);

        return mainReturnedFieldName;
    }

    @Transactional
    public String getUserMessages(UserDto currentUser, UserEntity user, MessageEntity message, Model model) {
        Set<MessageEntity> messages = user.getMessages();
        model.addAttribute("messages", messages);
        model.addAttribute("message", message);
        model.addAttribute("isCurrentUser", modelMapper.map(currentUser, UserEntity.class).equals(user));
        return "userMessages";
    }

    @SneakyThrows
    @Transactional
    public String updateMessage(UserDto currentUser, Long user, MessageEntity message, String text, String tag, MultipartFile file) {
        if (modelMapper.map(message.getAuthor(), UserDto.class).equals(currentUser)) {
            if (StringUtils.hasText(text)) {
                message.setText(text);
            }
            if (StringUtils.hasText(tag)) {
                message.setTag(tag);
            }
            saveFile(message, file);
            messageRepo.save(message);
        }
        return "redirect:/user-messages/" + user;
    }

    @Transactional
    public String deleteMessage(Long user, Long messageId) {
        messageRepo.deleteById(messageId);
        return "redirect:/user-messages/" + user;
    }

}
