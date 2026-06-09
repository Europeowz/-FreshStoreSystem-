package com.freshstore.service;

import com.freshstore.entity.User;
import com.freshstore.exception.AuthenticationException;
import com.freshstore.exception.ValidationException;
import com.freshstore.repository.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public User login(String username, String password) {
        if (username == null || username.isBlank())
            throw new ValidationException("用户名不能为空");
        if (password == null || password.isBlank())
            throw new ValidationException("密码不能为空");
        try {
            User user = userRepo.login(username, password);
            if (user == null) throw new AuthenticationException("用户名或密码错误");
            return user;
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("登录失败", e);
        }
    }

    public List<User> listAll() {
        try {
            List<User> users = userRepo.listAll();
            // strip password hashes from list results — not needed for display
            users.forEach(u -> u.setPassword(""));
            return users;
        } catch (Exception e) {
            throw new RuntimeException("查询用户失败", e);
        }
    }

    public void add(User u) {
        validate(u, true);
        try { userRepo.add(u); }
        catch (Exception e) { throw new RuntimeException("添加用户失败", e); }
    }

    public void update(User u) {
        validate(u, false);
        try { userRepo.update(u); }
        catch (Exception e) { throw new RuntimeException("更新用户失败", e); }
    }

    public void changePassword(String userId, String oldPassword, String newPassword) {
        if (oldPassword == null || oldPassword.isBlank())
            throw new ValidationException("原密码不能为空");
        if (newPassword == null || newPassword.isBlank())
            throw new ValidationException("新密码不能为空");
        if (newPassword.length() < 4)
            throw new ValidationException("新密码长度不能少于4位");
        if (oldPassword.equals(newPassword))
            throw new ValidationException("新密码不能与原密码相同");
        User u;
        try {
            u = userRepo.findByIdWithPassword(userId);
        } catch (Exception e) {
            throw new RuntimeException("修改密码失败", e);
        }
        if (u == null) throw new RuntimeException("用户不存在");
        if (!com.freshstore.util.HashUtil.verify(oldPassword, u.getPassword()))
            throw new AuthenticationException("原密码错误");
        try {
            userRepo.updatePassword(userId, newPassword);
        } catch (Exception e) {
            throw new RuntimeException("修改密码失败", e);
        }
    }

    public void delete(String id) {
        try { userRepo.delete(id); }
        catch (Exception e) { throw new RuntimeException("删除用户失败", e); }
    }

    private void validate(User u, boolean requirePassword) {
        if (u.getUserId() == null || u.getUserId().isBlank())
            throw new ValidationException("用户编号不能为空");
        if (u.getUsername() == null || u.getUsername().isBlank())
            throw new ValidationException("用户名不能为空");
        if (requirePassword && (u.getPassword() == null || u.getPassword().isBlank()))
            throw new ValidationException("密码不能为空");
        if (u.getRole() == null || u.getRole().isBlank())
            throw new ValidationException("角色不能为空");
    }
}
