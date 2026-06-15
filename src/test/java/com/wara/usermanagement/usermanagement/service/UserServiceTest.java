package com.wara.usermanagement.usermanagement.service;

import com.wara.usermanagement.usermanagement.dto.request.UserRequest;
import com.wara.usermanagement.usermanagement.dto.response.MessageResponse;
import com.wara.usermanagement.usermanagement.dto.response.UserResponse;
import com.wara.usermanagement.usermanagement.exception.ApplicationException;
import com.wara.usermanagement.usermanagement.model.User;
import com.wara.usermanagement.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createUser_whenValidRequest_thenSaveUserWithCorrectFields() {
        UserRequest request = UserRequest.builder()
                .name("Taylor Swift")
                .username("tay")
                .email("taylor@swift.com")
                .phone("123-456-7890")
                .website("taylorswift.com")
                .build();

        userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Taylor Swift");
        assertThat(saved.getUsername()).isEqualTo("tay");
        assertThat(saved.getEmail()).isEqualTo("taylor@swift.com");
        assertThat(saved.getPhone()).isEqualTo("123-456-7890");
        assertThat(saved.getWebsite()).isEqualTo("taylorswift.com");
    }

    @Test
    void createUser_whenOptionalFieldsN_thenSaveWithNullOptionals() {
        UserRequest request = UserRequest.builder()
                .name("Taylor Swift")
                .username("tay")
                .email("taylor@swift.com")
                .build();

        userService.createUser(request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getPhone()).isNull();
        assertThat(saved.getWebsite()).isNull();
    }

    @Test
    void getAllUsers_whenUsersIsEmpty_thenReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        assertThat(userService.getAllUsers()).isEmpty();
    }

    @Test
    void getAllUsers_whenUsersIsNotEmpty_thenReturnAListUser() {
        when(userRepository.findAll()).thenReturn(List.of(
                User.builder()
                        .id(1L)
                        .username("usr001")
                        .build(),
                User.builder()
                        .id(2L)
                        .username("usr002")
                        .build()
        ));
        List<UserResponse> users = userService.getAllUsers();
        assertThat(users.size()).isEqualTo(2L);
        assertThat(users.get(0).getId()).isEqualTo(1L);
        assertThat(users.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void getByUserId_whenUsersIsNotExist_thenThrowApplicationException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getByUserId(99L))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("user does not exist");
    }

    @Test
    void getByUserId_whenUsersIsExist_thenReturnUser() {
        when(userRepository.findById(eq(99L))).thenReturn(Optional.of(User.builder()
                .id(99L)
                .username("bts")
                .name("JK")
                .phone("123124")
                .build()));

        UserResponse userResponse = userService.getByUserId(99L);

        assertThat(userResponse.getId()).isEqualTo(99L);
        assertThat(userResponse.getUsername()).isEqualTo("bts");
        assertThat(userResponse.getName()).isEqualTo("JK");
        assertThat(userResponse.getPhone()).isEqualTo("123124");
    }

    @Test
    void updateUser_whenUserNotFound_thenThrowApplicationException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, UserRequest.builder()
                .name("New Name").username("new").email("new@email.com").build()))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("user does not exist");
    }

    @Test
    void updateUser_whenUserFound_thenUpdateFieldsAndReturnResponse() {
        User existing = User.builder()
                .id(1L)
                .name("Old Name")
                .username("olduser")
                .email("old@email.com")
                .phone("000")
                .website("old.com")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        UserRequest request = UserRequest.builder()
                .name("New Name")
                .username("newuser")
                .email("new@email.com")
                .phone("999")
                .website("new.com")
                .build();

        UserResponse response = userService.updateUser(1L, request);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("New Name");
        assertThat(saved.getUsername()).isEqualTo("newuser");
        assertThat(saved.getEmail()).isEqualTo("new@email.com");
        assertThat(saved.getPhone()).isEqualTo("999");
        assertThat(saved.getWebsite()).isEqualTo("new.com");

        assertThat(response.getName()).isEqualTo("New Name");
        assertThat(response.getUsername()).isEqualTo("newuser");
        assertThat(response.getEmail()).isEqualTo("new@email.com");
    }

    @Test
    void deleteUser_whenUserNotFound_thenThrowApplicationException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(99L))
                .isInstanceOf(ApplicationException.class)
                .hasMessage("user does not exist");
    }

    @Test
    void deleteUser_whenUserFound_thenDeleteAndReturnMessage() {
        User existing = User.builder().id(1L).name("Taylor Swift").build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        MessageResponse response = userService.deleteUser(1L);

        verify(userRepository).delete(existing);
        assertThat(response.getMessage()).isEqualTo("delete user id 1 successfully");
    }
}
