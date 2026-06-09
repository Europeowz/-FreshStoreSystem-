package com.freshstore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import com.freshstore.entity.User;
import com.freshstore.exception.AuthenticationException;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.UserRepository;
import com.freshstore.util.HashUtil;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepo;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepo);
    }

    @Test
    @DisplayName("login with correct credentials returns user")
    void login_valid_returnsUser() throws Exception {
        User user = new User("U001", "admin", "hashed", "管理员");
        when(userRepo.login("admin", "123456")).thenReturn(user);
        assertThat(userService.login("admin", "123456")).isSameAs(user);
    }

    @Test
    @DisplayName("login with wrong credentials throws AuthenticationException")
    void login_wrong_throws() throws Exception {
        when(userRepo.login("admin", "wrong")).thenReturn(null);
        assertThatThrownBy(() -> userService.login("admin", "wrong"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("错误");
    }

    @Test
    @DisplayName("login with blank username throws ValidationException")
    void login_blankUsername_throws() {
        assertThatThrownBy(() -> userService.login("", "pwd"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("login with blank password throws ValidationException")
    void login_blankPassword_throws() {
        assertThatThrownBy(() -> userService.login("admin", ""))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("listAll strips password hashes")
    void listAll_stripsPasswords() throws Exception {
        when(userRepo.listAll()).thenReturn(
                List.of(new User("U001", "admin", "hash123", "管理员")));
        List<User> users = userService.listAll();
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getPassword()).isEmpty();
    }

    @Test
    @DisplayName("add valid user succeeds")
    void add_valid_succeeds() throws Exception {
        User u = new User("U001", "admin", "123456", "管理员");
        userService.add(u);
        verify(userRepo).add(u);
    }

    @Test
    @DisplayName("add with blank password throws ValidationException")
    void add_blankPassword_throws() {
        User u = new User("U001", "admin", "", "管理员");
        assertThatThrownBy(() -> userService.add(u))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("密码");
        verifyNoInteractions(userRepo);
    }

    @Test
    @DisplayName("update delegates to repository")
    void update_delegates() throws Exception {
        User u = new User("U001", "admin", "123456", "管理员");
        userService.update(u);
        verify(userRepo).update(u);
    }

    @Test
    @DisplayName("update with blank id throws ValidationException")
    void update_blankId_throws() {
        User u = new User("", "admin", "123456", "管理员");
        assertThatThrownBy(() -> userService.update(u))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("delete delegates to repository")
    void delete_delegates() throws Exception {
        userService.delete("U001");
        verify(userRepo).delete("U001");
    }

    @Test
    @DisplayName("changePassword with correct old password succeeds")
    void changePassword_valid_succeeds() throws Exception {
        String oldHash = HashUtil.hash("oldPass");
        User user = new User("U001", "admin", oldHash, "管理员");
        when(userRepo.findByIdWithPassword("U001")).thenReturn(user);
        doNothing().when(userRepo).updatePassword(eq("U001"), anyString());

        userService.changePassword("U001", "oldPass", "newPass");

        verify(userRepo).findByIdWithPassword("U001");
        verify(userRepo).updatePassword(eq("U001"), anyString());
    }

    @Test
    @DisplayName("changePassword with blank old password throws")
    void changePassword_blankOld_throws() {
        assertThatThrownBy(() -> userService.changePassword("U001", "", "newPass"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("原密码");
    }

    @Test
    @DisplayName("changePassword with blank new password throws")
    void changePassword_blankNew_throws() {
        assertThatThrownBy(() -> userService.changePassword("U001", "oldPass", ""))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("新密码");
    }

    @Test
    @DisplayName("changePassword with short new password throws")
    void changePassword_shortNew_throws() {
        assertThatThrownBy(() -> userService.changePassword("U001", "oldPass", "12"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("长度");
    }

    @Test
    @DisplayName("changePassword with same old and new password throws")
    void changePassword_samePassword_throws() {
        assertThatThrownBy(() -> userService.changePassword("U001", "same", "same"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("相同");
    }

    @Test
    @DisplayName("changePassword with wrong old password throws")
    void changePassword_wrongOld_throws() throws Exception {
        User user = new User("U001", "admin",
                "$2a$12$abcdefghijklmnopqrstuuABCDEFGHIJKLMNOPQRSTUVWXYZ1234",
                "管理员");
        when(userRepo.findByIdWithPassword("U001")).thenReturn(user);

        assertThatThrownBy(() -> userService.changePassword("U001", "wrongPass", "newPass"))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("原密码错误");
        verify(userRepo, never()).updatePassword(anyString(), anyString());
    }

    @Test
    @DisplayName("changePassword for non-existent user throws")
    void changePassword_userNotFound_throws() throws Exception {
        when(userRepo.findByIdWithPassword("U999")).thenReturn(null);

        assertThatThrownBy(() -> userService.changePassword("U999", "old", "new12"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("用户不存在");
        verify(userRepo, never()).updatePassword(anyString(), anyString());
    }
}
