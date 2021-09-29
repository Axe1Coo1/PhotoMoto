package com.example.servingwebcontent.dto;

import com.example.servingwebcontent.domain.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MessageDto {
    private Long id;
    private String text;
    private String tag;
    private User author;
    private String filename;

    public String getAuthorName() {
        return author != null ? author.getUsername() : "<none>";
    }

}
