package com.freshstore.repository;

import com.freshstore.entity.Category;
import com.freshstore.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** 商品类别数据访问 JDBC 实现 */
public class JdbcCategoryRepository implements CategoryRepository {

    @Override
    public List<Category> listAll() throws Exception {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category ORDER BY category_id";
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) {
                list.add(new Category(
                    r.getString("category_id"),
                    r.getString("category_name")
                ));
            }
        }
        return list;
    }

    @Override
    public void add(Category cat) throws Exception {
        String sql = "INSERT INTO Category(category_id, category_name) VALUES(?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, cat.getCategoryId());
            p.setString(2, cat.getCategoryName());
            p.executeUpdate();
        }
    }

    @Override
    public void update(Category cat) throws Exception {
        String sql = "UPDATE Category SET category_name=? WHERE category_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, cat.getCategoryName());
            p.setString(2, cat.getCategoryId());
            p.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws Exception {
        String sql = "DELETE FROM Category WHERE category_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.executeUpdate();
        }
    }
}
