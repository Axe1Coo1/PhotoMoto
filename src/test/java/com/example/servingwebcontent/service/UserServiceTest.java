package com.example.servingwebcontent.service;


import com.example.servingwebcontent.domain.Role;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.UserDto;
import com.example.servingwebcontent.repos.UserRepo;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
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
    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private UserRepo userRepo;
    @MockBean
    private MailSender mailSender;
    @MockBean
    private PasswordEncoder passwordEncoder;


    @Test
    void addUser() {
        UserEntity userEntity = new UserEntity();
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        userDto.setEmail("some@some.some");

        boolean isUserCreated = userService.addUser(userDto);

        Assertions.assertTrue(isUserCreated);
        Assertions.assertNotNull(userDto.getActivationCode());
        Assertions.assertTrue(CoreMatchers.is(userDto.getRoles()).matches(Collections.singleton(Role.USER)));

//        verify(userRepo, Mockito.times(1)).save(userDto);
        verify(mailSender, Mockito.times(1)).send(ArgumentMatchers.eq(userDto.getEmail()),
                ArgumentMatchers.eq("Activation code"),
                ArgumentMatchers.contains("Welcome to PhotoMoto.")
        );
    }

    @Test
    public void addUserFailTest() {
        UserEntity userEntity = new UserEntity();
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        userDto.setUsername("Nate");
        doReturn(new UserEntity())
                .when(userRepo)
                .findByUsername("Nate");

        boolean isUserCreated = userService.addUser(userDto);
        Assertions.assertFalse(isUserCreated);

        verify(userRepo, Mockito.times(0)).save(any(UserEntity.class));
        verify(mailSender, Mockito.times(0))
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

    @Test
    public void deleteUserTest(){
        UserEntity userEntity = new UserEntity();
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);
        userDto.setUsername("Nate");
        userDto.setEmail("some@some.some");
        userDto.setId(1111);

        boolean isUserCreated = userService.addUser(userDto);
        Assertions.assertTrue(isUserCreated);
        boolean isUserDeleted = userService.deleteUser(userDto.getId());
        Assertions.assertFalse(isUserDeleted);
        verify(userRepo, Mockito.times(1)).findById(userDto.getId());
    }
}