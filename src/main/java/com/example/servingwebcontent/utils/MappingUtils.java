package com.example.servingwebcontent.utils;

import com.example.servingwebcontent.domain.MessageEntity;
import com.example.servingwebcontent.dto.MessageDto;
import org.springframework.stereotype.Service;

@Service
public class MappingUtils {
    public static MessageDto mapToMessageDto(MessageEntity entity){
        MessageDto dto = new MessageDto();
        dto.setAuthor(entity.getAuthor());
        dto.setFilename(entity.getFilename());
        dto.setId(entity.getId());
        dto.setTag(entity.getTag());
        dto.setText(entity.getText());
        return dto;
    }

    public MessageEntity mapToMessageEntity(MessageDto dto){
        MessageEntity entity = new MessageEntity();
        entity.setAuthor(dto.getAuthor());
        entity.setFilename(dto.getFilename());
        entity.setId(dto.getId());
        entity.setTag(dto.getTag());
        entity.setText(dto.getText());
        return entity;
    }
}
