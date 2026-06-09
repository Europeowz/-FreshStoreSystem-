package com.freshstore.repository;

import com.freshstore.entity.User;
import java.util.List;

/** 用户数据访问接口 */
public interface UserRepository {
    User login(String username, String password) throws Exception;
    List<User> listAll() throws Exception;
    void add(User u) throws Exception;
    void update(User u) throws Exception;
    void delete(String id) throws Exception;
    User findByIdWithPassword(String id) throws Exception;
    void updatePassword(String id, String newPassword) throws Exception;
}
