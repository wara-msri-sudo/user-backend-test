package com.wara.usermanagement.usermanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wara.usermanagement.usermanagement.dto.request.UserRequest;
import com.wara.usermanagement.usermanagement.dto.response.MessageResponse;
import com.wara.usermanagement.usermanagement.dto.response.UserResponse;
import com.wara.usermanagement.usermanagement.exception.ApplicationException;
import com.wara.usermanagement.usermanagement.model.User;
import com.wara.usermanagement.usermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void createUser_whenValidRequest_thenReturn201() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Leanne Graham")
                .username("Bret")
                .email("sincere@april.biz")
                .phone("1-770-736-8031")
                .website("hildegard.org")
                .build();


        when(userService.createUser(eq(request))).thenReturn(UserResponse.builder()
                .id(1L).name("Leanne Graham").username("Bret").email("sincere@april.biz")
                .phone("1-770-736-8031").website("hildegard.org")
                .build());

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Leanne Graham"))
                .andExpect(jsonPath("$.username").value("Bret"))
                .andExpect(jsonPath("$.email").value("sincere@april.biz"))
                .andExpect(jsonPath("$.phone").value("1-770-736-8031"))
                .andExpect(jsonPath("$.website").value("hildegard.org"))
        ;
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createUser_whenInvalidRequest_thenReturn400(String username) throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Taylor Swift")
                .username(username)
                .email("taylor@swift.com")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("field 'username' must not be null or empty"));
    }

    @Test
    void createUser_whenServiceThrowsException_thenReturnErrorMessage() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Leanne Graham")
                .username("Bret")
                .email("sincere@april.biz")
                .build();

        doThrow(new ApplicationException("something went wrong", HttpStatus.BAD_REQUEST))
                .when(userService).createUser(any(UserRequest.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("something went wrong"));
    }

    @Test
    void getUsers_whenServiceThrowsApplicationException_thenReturn204() throws Exception {
        when(userService.getAllUsers()).thenThrow(new ApplicationException("user is not exist", HttpStatus.NO_CONTENT));

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUsers_whenServiceReturnListOfUser_thenReturn200() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(
                        UserResponse.builder()
                                .id(1L)
                                .name("Leanne Graham")
                                .username("Bret")
                                .email("sincere@april.biz")
                                .build(),
                        UserResponse.builder()
                                .id(2L)
                                .name("Taylor Swift")
                                .username("taylorleak")
                                .email("taylor@swift.com")
                                .build()
                )
        );

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Leanne Graham"))
                .andExpect(jsonPath("$[0].username").value("Bret"))
                .andExpect(jsonPath("$[0].email").value("sincere@april.biz"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Taylor Swift"))
                .andExpect(jsonPath("$[1].username").value("taylorleak"))
                .andExpect(jsonPath("$[1].email").value("taylor@swift.com"))
        ;
    }

    @Test
    void getUserById_WhenUserNotExist_thenReturn404() throws Exception {
        when(userService.getByUserId(eq(2L))).thenThrow(new ApplicationException("user does not exist", HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user does not exist"));
    }

    @Test
    void getUserById_WhenUserIsExist_thenReturn200() throws Exception {
        when(userService.getByUserId(3L)).thenReturn(UserResponse.builder()
                .id(3L)
                .name("Taylor Swift")
                .username("taylorleak")
                .email("taylor@swift.com")
                .build());

        mockMvc.perform(get("/users/3").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("Taylor Swift"))
                .andExpect(jsonPath("$.username").value("taylorleak"))
                .andExpect(jsonPath("$.email").value("taylor@swift.com"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void updateUser_WhenUsernameInvalid_thenReturn400(String username) throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Taylor Swift")
                .username(username)
                .email("taylor@swift.com")
                .build();


        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("field 'username' must not be null or empty"));
    }

    @Test
    void updateUser_WhenUserIsNotExist_thenReturn404() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Taylor Swift")
                .username("theera")
                .email("taylor@swift.com")
                .build();

        when(userService.updateUser(eq(1L), eq(request))).thenThrow(new ApplicationException("user does not exist", HttpStatus.NOT_FOUND));

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user does not exist"));
    }

    @Test
    void updateUser_WhenUserIsExist_thenReturn200() throws Exception {
        UserRequest request = UserRequest.builder()
                .name("Taylor Swift")
                .username("theera")
                .email("taylor@swift.com")
                .build();


        when(userService.updateUser(eq(1L), eq(request))).thenReturn(UserResponse.builder()
                .id(1L).name("Taylor Swift").username("theera").email("taylor@swift.com")
                .build());

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Taylor Swift"))
                .andExpect(jsonPath("$.username").value("theera"));

    }

    @Test
    void deleteUser_WhenUserIsNotExist_thenReturn404() throws Exception {
        when(userService.deleteUser(eq(1L))).thenThrow(new ApplicationException("user does not exist", HttpStatus.NOT_FOUND));

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("user does not exist"));
    }

    @Test
    void deleteUser_WhenUserIsExist_thenReturn200() throws Exception {

        when(userService.deleteUser(eq(1L))).thenReturn(new MessageResponse("delete user id 1 successfully"));

        mockMvc.perform(delete("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
