package dao;

import entity.Category;
import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** 商品类别数据访问 */
public class CategoryDAO {

    /** 查询全部类别 */
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

    /** 新增类别 */
    public void add(Category cat) throws Exception {
        String sql = "INSERT INTO Category(category_id, category_name) VALUES(?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, cat.getCategoryId());
            p.setString(2, cat.getCategoryName());
            p.executeUpdate();
        }
    }

    /** 修改类别名称 */
    public void update(Category cat) throws Exception {
        String sql = "UPDATE Category SET category_name=? WHERE category_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, cat.getCategoryName());
            p.setString(2, cat.getCategoryId());
            p.executeUpdate();
        }
    }

    /** 删除类别 */
    public void delete(String id) throws Exception {
        String sql = "DELETE FROM Category WHERE category_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id);
            p.executeUpdate();
        }
    }
}
