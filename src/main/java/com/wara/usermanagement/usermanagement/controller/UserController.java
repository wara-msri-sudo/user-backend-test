package com.wara.usermanagement.usermanagement.controller;


import com.wara.usermanagement.usermanagement.dto.request.UserRequest;
import com.wara.usermanagement.usermanagement.dto.response.MessageResponse;
import com.wara.usermanagement.usermanagement.dto.response.UserResponse;
import com.wara.usermanagement.usermanagement.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserRequest userRequest) {
        return new ResponseEntity<>(this.userService.createUser(userRequest), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return new ResponseEntity<>(this.userService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable(name = "userId") Long userId) {
        return new ResponseEntity<UserResponse>(this.userService.getByUserId(userId), HttpStatus.OK);
    }

    @PutMapping("{userId}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable(name = "userId") Long userId, @Valid @RequestBody UserRequest userRequest) {
        return new ResponseEntity<UserResponse>(this.userService.updateUser(userId, userRequest), HttpStatus.OK);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable(name = "userId") Long userId) {
        return new ResponseEntity<MessageResponse>(this.userService.deleteUser(userId), HttpStatus.NO_CONTENT);
    }
}
