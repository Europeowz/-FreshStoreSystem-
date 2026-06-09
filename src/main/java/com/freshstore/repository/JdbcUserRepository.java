package com.freshstore.repository;

import com.freshstore.entity.User;
import com.freshstore.util.DBUtil;
import com.freshstore.util.HashUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** 用户数据访问 JDBC 实现 */
public class JdbcUserRepository implements UserRepository {

    @Override
    public User login(String username, String password) throws Exception {
        String sql = "SELECT * FROM Users WHERE username=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) {
                    String storedHash = r.getString("password");
                    if (HashUtil.verify(password, storedHash)) {
                        return new User(
                            r.getString("user_id"),
                            r.getString("username"),
                            storedHash,
                            r.getString("role")
                        );
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<User> listAll() throws Exception {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY user_id";
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new User(
                    r.getString("user_id"),
                    r.getString("username"),
                    r.getString("password"),
                    r.getString("role")
                ));
            }
        }
        return list;
    }

    @Override
    public void add(User u) throws Exception {
        String sql = "INSERT INTO Users(user_id,username,password,role) VALUES(?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, u.getUserId());
            p.setString(2, u.getUsername());
            p.setString(3, HashUtil.hash(u.getPassword()));
            p.setString(4, u.getRole());
            p.executeUpdate();
        }
    }

    @Override
    public void update(User u) throws Exception {
        String pw = u.getPassword();
        boolean changePw = pw != null && !pw.isBlank();
        String sql = changePw
            ? "UPDATE Users SET username=?,password=?,role=? WHERE user_id=?"
            : "UPDATE Users SET username=?,role=? WHERE user_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            if (changePw) {
                p.setString(1, u.getUsername());
                p.setString(2, HashUtil.hash(pw));
                p.setString(3, u.getRole());
                p.setString(4, u.getUserId());
            } else {
                p.setString(1, u.getUsername());
                p.setString(2, u.getRole());
                p.setString(3, u.getUserId());
            }
            p.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws Exception {
        String sql = "DELETE FROM Users WHERE user_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.executeUpdate();
        }
    }

    @Override
    public User findByIdWithPassword(String id) throws Exception {
        String sql = "SELECT * FROM Users WHERE user_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return new User(
                    r.getString("user_id"), r.getString("username"),
                    r.getString("password"), r.getString("role"));
            }
        }
        return null;
    }

    @Override
    public void updatePassword(String id, String newPassword) throws Exception {
        String sql = "UPDATE Users SET password=? WHERE user_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, HashUtil.hash(newPassword));
            p.setString(2, id);
            p.executeUpdate();
        }
    }
}
