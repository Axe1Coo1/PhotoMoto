package com.example.servingwebcontent.utils;

import com.example.servingwebcontent.domain.Message;
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
    public static MessageDto ConvertToDto(Message message){
        return modelMapper.map(message, MessageDto.class);
    }
    public static UserDto ConvertToDto(User user){
        return modelMapper.map(user, UserDto.class);
    }


}
