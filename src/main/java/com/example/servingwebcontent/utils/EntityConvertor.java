package com.example.servingwebcontent.utils;

import com.example.servingwebcontent.domain.MessageEntity;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.MessageDto;
import com.example.servingwebcontent.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class EntityConvertor {
    private static ModelMapper modelMapper = null;

    public EntityConvertor() {
        modelMapper = new ModelMapper();
    }
    public static MessageDto convertToDto(MessageEntity messageEntity){
        return modelMapper.map(messageEntity, MessageDto.class);
    }
    public static UserDto convertToDto(UserEntity userEntity){
        return modelMapper.map(userEntity, UserDto.class);
    }
    public static UserEntity convertToEntity(UserDto userDto){
        return modelMapper.map(userDto, UserEntity.class);
    }



}
