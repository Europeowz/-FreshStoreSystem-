package dao;

import entity.User;
import util.DBUtil;
import util.HashUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** 用户数据访问（密码使用 SHA-256 哈希存储） */
public class UserDAO {

    /** 登录验证：SHA-256 哈希比对，成功返回 User，失败返回 null */
    public User login(String username, String password) throws Exception {
        // 先查用户，再用 HashUtil.verify 比对（不直接拼 SQL）
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

    /** 查询全部用户 */
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

    /** 添加用户（密码自动 SHA-256 哈希） */
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

    /** 修改用户（密码自动 SHA-256 哈希） */
    public void update(User u) throws Exception {
        String sql = "UPDATE Users SET username=?,password=?,role=? WHERE user_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, u.getUsername());
            p.setString(2, HashUtil.hash(u.getPassword()));
            p.setString(3, u.getRole());
            p.setString(4, u.getUserId());
            p.executeUpdate();
        }
    }

    /** 删除用户 */
    public void delete(String id) throws Exception {
        String sql = "DELETE FROM Users WHERE user_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.executeUpdate();
        }
    }
}
