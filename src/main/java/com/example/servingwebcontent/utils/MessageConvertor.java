package com.example.servingwebcontent.utils;

import com.example.servingwebcontent.domain.MessageEntity;
import com.example.servingwebcontent.domain.User;
import com.example.servingwebcontent.dto.MessageDto;
import com.example.servingwebcontent.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class MessageConvertor {
    private static ModelMapper modelMapper = null;

    public MessageConvertor() {
        modelMapper = new ModelMapper();
    }
    public static MessageDto ConvertToDto(MessageEntity messageEntity){
        return modelMapper.map(messageEntity, MessageDto.class);
    }
    public static UserDto ConvertToDto(User user){
        return modelMapper.map(user, UserDto.class);
    }


}
