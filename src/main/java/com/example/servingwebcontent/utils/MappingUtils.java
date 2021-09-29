package com.example.servingwebcontent.utils;

import com.example.servingwebcontent.domain.Message;
import com.example.servingwebcontent.dto.MessageDto;
import org.springframework.stereotype.Service;

@Service
public class MappingUtils {
    public static MessageDto mapToMessageDto(Message entity){
        MessageDto dto = new MessageDto();
        dto.setAuthor(entity.getAuthor());
        dto.setFilename(entity.getFilename());
        dto.setId(entity.getId());
        dto.setTag(entity.getTag());
        dto.setText(entity.getText());
        return dto;
    }

    public Message mapToMessageEntity(MessageDto dto){
        Message entity = new Message();
        entity.setAuthor(dto.getAuthor());
        entity.setFilename(dto.getFilename());
        entity.setId(dto.getId());
        entity.setTag(dto.getTag());
        entity.setText(dto.getText());
        return entity;
    }
}
