package com.example.servingwebcontent.repos;

import com.example.servingwebcontent.domain.Message;
import org.springframework.data.repository.CrudRepository;

public interface MessageRepo extends CrudRepository<Message, Long> {
}
