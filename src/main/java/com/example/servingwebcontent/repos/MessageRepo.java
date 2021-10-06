package com.example.servingwebcontent.repos;

import com.example.servingwebcontent.domain.MessageEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends CrudRepository<MessageEntity, Long> {

    List<MessageEntity> findByTag(String tag);

}
