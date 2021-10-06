package com.example.servingwebcontent.service;


import com.example.servingwebcontent.domain.Role;
import com.example.servingwebcontent.domain.UserEntity;
import com.example.servingwebcontent.repos.UserRepo;
import lombok.RequiredArgsConstructor;
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

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepo.findByUsername(username);
//        UserDto userDto = MessageConvertor.ConvertToDto(userEntity);

        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return userEntity;
    }

    private void sendMessage(UserEntity userEntity) {
        String welcomeMessage = "Hello, %s! \n" +
                "Welcome to PhotoMoto. Please, visit next link " +
                "to verify your account: http://localhost:8080/activate/%s";
        if (StringUtils.hasLength(userEntity.getEmail())) {
            String message = String.format(
                    welcomeMessage,
                    userEntity.getUsername(),
                    userEntity.getActivationCode()
            );

            String activationCodeName = "Activation code";
            mailSender.send(userEntity.getEmail(), activationCodeName, message);
        }
    }

    @Transactional
    public boolean addUser(UserEntity userEntity) {
        UserEntity userEntityFromDb = userRepo.findByUsername(userEntity.getUsername());

        if (userEntityFromDb != null) {
            return false;
        }

        userEntity.setActive(true);
        userEntity.setRoles(Collections.singleton(Role.USER));
        userEntity.setActivationCode(UUID.randomUUID().toString());
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        userRepo.save(userEntity);

        sendMessage(userEntity);

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
    public void updateProfile(UserEntity userEntity, String password, String email) {
        String userEmail = userEntity.getEmail();

        if (isEmailChanged(email, userEmail)) {
            userEntity.setEmail(email);

            if (StringUtils.hasLength(email)) {
                userEntity.setActivationCode(UUID.randomUUID().toString());
            }
        }

        if (StringUtils.hasLength(password)) {
            userEntity.setPassword(passwordEncoder.encode(password));
        }

        userRepo.save(userEntity);

        if (isEmailChanged(email, userEmail)) {
            sendMessage(userEntity);
        }
    }

    private boolean isEmailChanged(String email, String userEmail) {
        return email != null && !email.equals((userEmail)) ||
                userEmail != null && !userEmail.equals(email);
    }
}
