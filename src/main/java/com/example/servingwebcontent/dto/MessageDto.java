package com.example.servingwebcontent.dto;

import com.example.servingwebcontent.domain.UserEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageDto {
    private Long id;
    private String text;
    private String tag;
    private UserEntity author;
    private String filename;

    public String getAuthorName() {
        return author != null ? author.getUsername() : "<none>";
    }

}
