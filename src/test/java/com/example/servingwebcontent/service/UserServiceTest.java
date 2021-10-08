package com.example.servingwebcontent.service;


import com.example.servingwebcontent.domain.Role;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.repos.UserRepo;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepo userRepo;
    @MockBean
    private MailSender mailSender;
    @MockBean
    private PasswordEncoder passwordEncoder;


    @Test
    void addUser() {
        UserEntity user = new UserEntity();
        user.setEmail("some@some.some");

        boolean isUserCreated = userService.addUser(user);

        Assertions.assertTrue(isUserCreated);
        Assertions.assertNotNull(user.getActivationCode());
        Assertions.assertTrue(CoreMatchers.is(user.getRoles()).matches(Collections.singleton(Role.USER)));

        verify(userRepo, Mockito.times(1)).save(user);
        Mockito.verify(mailSender, Mockito.times(1)).send(ArgumentMatchers.eq(user.getEmail()),
                ArgumentMatchers.eq("Activation code"),
                ArgumentMatchers.contains("Welcome to PhotoMoto.")
        );
    }

    @Test
    public void addUserFailTest() {
        UserEntity user = new UserEntity();
        user.setUsername("Nate");
        doReturn(new UserEntity())
                .when(userRepo)
                .findByUsername("Nate");

        boolean isUserCreated = userService.addUser(user);
        Assertions.assertFalse(isUserCreated);

        verify(userRepo, Mockito.times(0)).save(ArgumentMatchers.any(UserEntity.class));
        Mockito.verify(mailSender, Mockito.times(0))
                .send(
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString(),
                        ArgumentMatchers.anyString()
                );
    }

    @Test
    public void activateUserTest(){
        UserEntity user = new UserEntity();
        user.setActivationCode("bingo!");

        doReturn(user)
                .when(userRepo)
                .findByActivationCode("activate");
        boolean isUserActivated = userService.activateUser("activate");
        Assertions.assertTrue(isUserActivated);
        Assertions.assertNull(user.getActivationCode());
        verify(userRepo, Mockito.times(1)).save(user);
    }

    @Test
    public void activateUserFailTest(){
        boolean isUserActivated = userService.activateUser("activate");
        Assertions.assertFalse(isUserActivated);
        verify(userRepo, Mockito.times(0)).save(any(UserEntity.class));
    }
}