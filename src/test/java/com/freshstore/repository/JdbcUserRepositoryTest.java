package com.freshstore.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.freshstore.entity.User;
import com.freshstore.util.DBUtil;
import com.freshstore.util.HashUtil;
import java.io.File;
import java.sql.PreparedStatement;
import org.junit.jupiter.api.*;

@DisplayName("JdbcUserRepository integration tests")
class JdbcUserRepositoryTest {

    private JdbcUserRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        DBUtil.setUrl("jdbc:sqlite:"
                + File.createTempFile("test_usr_", ".db").getAbsolutePath().replace('\\', '/'));
        try (var c = DBUtil.getConnection(); var s = c.createStatement()) {
            s.execute("CREATE TABLE Users(user_id TEXT PRIMARY KEY, username TEXT NOT NULL UNIQUE, password TEXT NOT NULL, role TEXT NOT NULL)");
        }
        try (var c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement("INSERT INTO Users VALUES(?,?,?,?)")) {
            ps.setString(1, "U001"); ps.setString(2, "admin");
            ps.setString(3, HashUtil.hash("123456")); ps.setString(4, "管理员");
            ps.executeUpdate();
            ps.setString(1, "U002"); ps.setString(2, "buyer1");
            ps.setString(3, HashUtil.hash("123456")); ps.setString(4, "采购员");
            ps.executeUpdate();
        }
        repo = new JdbcUserRepository();
    }

    @AfterEach
    void tearDown() {
        DBUtil.resetUrl();
    }

    @Test
    @DisplayName("login with correct password returns user")
    void login_correct_returnsUser() throws Exception {
        User u = repo.login("admin", "123456");
        assertThat(u).isNotNull();
        assertThat(u.getUserId()).isEqualTo("U001");
        assertThat(u.getRole()).isEqualTo("管理员");
    }

    @Test
    @DisplayName("login with wrong password returns null")
    void login_wrong_returnsNull() throws Exception {
        assertThat(repo.login("admin", "wrong")).isNull();
    }

    @Test
    @DisplayName("login with nonexistent username returns null")
    void login_nonexistent_returnsNull() throws Exception {
        assertThat(repo.login("nobody", "123456")).isNull();
    }

    @Test
    @DisplayName("listAll returns all users with password hashes")
    void listAll_returnsWithHashes() throws Exception {
        var users = repo.listAll();
        assertThat(users).hasSize(2);
        assertThat(users.get(0).getPassword()).isNotEmpty();
    }

    @Test
    @DisplayName("add, then login with new user works")
    void add_then_login() throws Exception {
        repo.add(new User("U003", "seller1", "mypass", "销售员"));
        User login = repo.login("seller1", "mypass");
        assertThat(login).isNotNull();
        assertThat(login.getUserId()).isEqualTo("U003");
    }

    @Test
    @DisplayName("update changes username and old login still works")
    void update_changesFields() throws Exception {
        repo.update(new User("U001", "admin2", "", "超级管理员"));
        var users = repo.listAll();
        User updated = users.stream().filter(u -> "U001".equals(u.getUserId())).findFirst().orElseThrow();
        assertThat(updated.getUsername()).isEqualTo("admin2");
        assertThat(updated.getRole()).isEqualTo("超级管理员");
    }

    @Test
    @DisplayName("update with new password changes hash")
    void update_withPassword_changesHash() throws Exception {
        User before = repo.login("admin", "123456");
        String oldHash = before.getPassword();

        repo.update(new User("U001", "admin", "newpass", "管理员"));

        User after = repo.login("admin", "newpass");
        assertThat(after).isNotNull();
        assertThat(after.getPassword()).isNotEqualTo(oldHash);
    }

    @Test
    @DisplayName("delete removes user")
    void delete_removes() throws Exception {
        repo.delete("U001");
        assertThat(repo.login("admin", "123456")).isNull();
    }
}
