package com.wara.usermanagement.usermanagement.service;


import com.wara.usermanagement.usermanagement.dto.request.UserRequest;
import com.wara.usermanagement.usermanagement.dto.response.MessageResponse;
import com.wara.usermanagement.usermanagement.dto.response.UserResponse;
import com.wara.usermanagement.usermanagement.exception.ApplicationException;
import com.wara.usermanagement.usermanagement.model.User;
import com.wara.usermanagement.usermanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserResponse createUser(UserRequest userRequest) {
        User user = User.builder()
                .name(userRequest.getName())
                .username(userRequest.getUsername())
                .email(userRequest.getEmail())
                .phone(userRequest.getPhone())
                .website(userRequest.getWebsite())
                .build();
        userRepository.save(user);
        return mapUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserService::mapUserResponse)
                .toList();
    }

    public UserResponse getByUserId(Long userId) {
        return mapUserResponse(getUser(userId));
    }

    public UserResponse updateUser(Long userId, UserRequest userRequest) {
        User user = getUser(userId);
        user.setName(userRequest.getName());
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setWebsite(userRequest.getWebsite());
        userRepository.save(user);
        return mapUserResponse(user);
    }

    public MessageResponse deleteUser(Long userId) {
        User user = getUser(userId);
        userRepository.delete(user);
        return new MessageResponse("delete user id " + userId + " successfully");
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new ApplicationException("user does not exist", HttpStatus.NOT_FOUND));
    }

    private static UserResponse mapUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .website(user.getWebsite())
                .build();
    }
}
