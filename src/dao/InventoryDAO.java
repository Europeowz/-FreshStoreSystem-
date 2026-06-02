package dao;

import entity.Inventory;
import util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** 库存信息数据访问 */
public class InventoryDAO {

    /** 查询全部库存（含商品名称） */
    public List<Inventory> listAll() throws Exception {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT i.*, p.name AS product_name FROM Inventory i "
                   + "JOIN Product p ON i.product_id = p.product_id "
                   + "ORDER BY i.product_id";
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    /** 新增库存记录 */
    public void add(Inventory inv) throws Exception {
        String sql = "INSERT INTO Inventory(product_id,stock,max_stock,min_stock,"
                   + "location,last_in,user_id) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, inv.getProductId());
            p.setInt(2,    inv.getStock());
            p.setInt(3,    inv.getMaxStock());
            p.setInt(4,    inv.getMinStock());
            p.setString(5, inv.getLocation());
            p.setString(6, inv.getLastIn());
            p.setString(7, inv.getUserId());
            p.executeUpdate();
        }
    }

    /** 修改库存 */
    public void update(Inventory inv) throws Exception {
        String sql = "UPDATE Inventory SET stock=?,max_stock=?,min_stock=?,"
                   + "location=?,last_in=?,user_id=? WHERE product_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1,    inv.getStock());
            p.setInt(2,    inv.getMaxStock());
            p.setInt(3,    inv.getMinStock());
            p.setString(4, inv.getLocation());
            p.setString(5, inv.getLastIn());
            p.setString(6, inv.getUserId());
            p.setString(7, inv.getProductId());
            p.executeUpdate();
        }
    }

    /** 删除库存记录 */
    public void delete(String productId) throws Exception {
        String sql = "DELETE FROM Inventory WHERE product_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, productId);
            p.executeUpdate();
        }
    }

    private Inventory map(ResultSet r) throws SQLException {
        Inventory i = new Inventory();
        i.setProductId(r.getString("product_id"));
        i.setProductName(r.getString("product_name"));
        i.setStock(r.getInt("stock"));
        i.setMaxStock(r.getInt("max_stock"));
        i.setMinStock(r.getInt("min_stock"));
        i.setLocation(r.getString("location"));
        i.setLastIn(r.getString("last_in"));
        i.setUserId(r.getString("user_id"));
        return i;
    }
}
