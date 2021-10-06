package com.example.servingwebcontent.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "message")
//rename to message entity, create dto
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Please fill the message")
    @Length(max = 2048, message = "Message too long (more then 2kB)")
    private String text;
    @Length(max = 255, message = "Message too long (more then 255 symbols)")
    private String tag;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity author;
    private String filename;

    public String getAuthorName() {
        return author != null ? author.getUsername() : "<none>";
    }
    public String getTag() {
        return tag.length() > 0 ? '#' + tag : tag;
    }
}
