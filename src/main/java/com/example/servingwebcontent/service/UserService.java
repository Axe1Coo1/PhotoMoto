package com.example.servingwebcontent.service;


import com.example.servingwebcontent.domain.Role;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.dto.UserDto;
import com.example.servingwebcontent.repos.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepo userRepo;
    private final MailSender mailSender;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findByUsername(username);
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        if (userDto == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userDto;
    }

    private void sendMessage(UserDto userDto) {
        String welcomeMessage = "Hello, %s! \n" +
                "Welcome to PhotoMoto. Please, visit next link " +
                "to verify your account: http://localhost:8080/activate/%s";
        if (StringUtils.hasLength(userDto.getEmail())) {
            String message = String.format(
                    welcomeMessage,
                    userDto.getUsername(),
                    userDto.getActivationCode()
            );

            String activationCodeName = "Activation code";
            mailSender.send(userDto.getEmail(), activationCodeName, message);
        }
    }

    @Transactional
    public boolean addUser(UserDto userDto) {
        UserEntity userEntityFromDb = userRepo.findByUsername(userDto.getUsername());

        if (userEntityFromDb != null) {
            return false;
        }

        userDto.setActive(true);
        userDto.setRoles(Collections.singleton(Role.USER));
        userDto.setActivationCode(UUID.randomUUID().toString());
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userRepo.save(userEntity);

        sendMessage(userDto);

        return true;
    }

    @Transactional
    public boolean activateUser(String code) {
        UserEntity userEntity = userRepo.findByActivationCode(code);
        if (userEntity == null) {
            return false;
        }
        userEntity.setActivationCode(null);
        userRepo.save(userEntity);
        return true;
    }

    public List<UserEntity> findAll() {
        return userRepo.findAll();
    }

    @Transactional
    public void saveUser(UserEntity userEntity, String username, Map<String, String> form) {
        userEntity.setUsername(username);
        Set<String> roles = Arrays.stream(Role.values())
                .map(Role::name)
                .collect(Collectors.toSet());
        userEntity.getRoles().clear();
        for (String key : form.keySet()) {
            if (roles.contains(key)) {
                userEntity.getRoles().add(Role.valueOf(key));
            }
        }
        userRepo.save(userEntity);
    }

    @Transactional
    public void updateProfile(UserDto userDto, String password, String email) {
        String userEmail = userDto.getEmail();

        if (isEmailChanged(email, userEmail)) {
            userDto.setEmail(email);

            if (StringUtils.hasLength(email)) {
                userDto.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (StringUtils.hasLength(password)) {
            userDto.setPassword(passwordEncoder.encode(password));
        }

        userRepo.save(modelMapper.map(userDto, UserEntity.class));

        if (isEmailChanged(email, userEmail)) {
            sendMessage(userDto);
        }
    }

    private boolean isEmailChanged(String email, String userEmail) {
        return email != null && !email.equals((userEmail)) ||
                userEmail != null && !userEmail.equals(email);
    }
}
