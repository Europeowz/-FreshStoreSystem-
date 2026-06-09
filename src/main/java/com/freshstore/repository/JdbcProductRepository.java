package com.freshstore.repository;

import com.freshstore.entity.Product;
import com.freshstore.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** 商品数据访问 JDBC 实现 */
public class JdbcProductRepository implements ProductRepository {

    @Override
    public List<Product> listAll() throws Exception {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM Product p "
                   + "JOIN Category c ON p.category_id = c.category_id "
                   + "ORDER BY p.product_id";
        try (Connection conn = DBUtil.getConnection();
             Statement s = conn.createStatement();
             ResultSet r = s.executeQuery(sql)) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    @Override
    public List<Product> searchByName(String keyword) throws Exception {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM Product p "
                   + "JOIN Category c ON p.category_id = c.category_id "
                   + "WHERE p.name LIKE ? ORDER BY p.product_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, "%" + keyword + "%");
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) list.add(map(r));
            }
        }
        return list;
    }

    @Override
    public List<Product> listByCategory(String categoryId) throws Exception {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM Product p "
                   + "JOIN Category c ON p.category_id = c.category_id "
                   + "WHERE p.category_id=? ORDER BY p.product_id";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement p = conn.prepareStatement(sql)) {
            p.setString(1, categoryId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) list.add(map(r));
            }
        }
        return list;
    }

    @Override
    public void add(Product p) throws Exception {
        String sql = "INSERT INTO Product(product_id,name,category_id,spec,unit,"
                   + "cost,price,shelf_life,supplier_name,reg_date) "
                   + "VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,  p.getProductId());
            ps.setString(2,  p.getName());
            ps.setString(3,  p.getCategoryId());
            ps.setString(4,  p.getSpec());
            ps.setString(5,  p.getUnit());
            ps.setDouble(6,  p.getCost());
            ps.setDouble(7,  p.getPrice());
            ps.setInt(8,     p.getShelfLife());
            ps.setString(9,  p.getSupplierName());
            ps.setString(10, p.getRegDate());
            ps.executeUpdate();
        }
    }

    @Override
    public void update(Product p) throws Exception {
        String sql = "UPDATE Product SET name=?,category_id=?,spec=?,unit=?,"
                   + "cost=?,price=?,shelf_life=?,supplier_name=?,reg_date=? "
                   + "WHERE product_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1,  p.getName());
            ps.setString(2,  p.getCategoryId());
            ps.setString(3,  p.getSpec());
            ps.setString(4,  p.getUnit());
            ps.setDouble(5,  p.getCost());
            ps.setDouble(6,  p.getPrice());
            ps.setInt(7,     p.getShelfLife());
            ps.setString(8,  p.getSupplierName());
            ps.setString(9,  p.getRegDate());
            ps.setString(10, p.getProductId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws Exception {
        String sql = "DELETE FROM Product WHERE product_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Product findById(String id) throws Exception {
        String sql = "SELECT p.*, c.category_name FROM Product p "
                   + "JOIN Category c ON p.category_id = c.category_id "
                   + "WHERE p.product_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet r = ps.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    private Product map(ResultSet r) throws SQLException {
        Product p = new Product();
        p.setProductId(r.getString("product_id"));
        p.setName(r.getString("name"));
        p.setCategoryId(r.getString("category_id"));
        p.setCategoryName(r.getString("category_name"));
        p.setSpec(r.getString("spec"));
        p.setUnit(r.getString("unit"));
        p.setCost(r.getDouble("cost"));
        p.setPrice(r.getDouble("price"));
        p.setShelfLife(r.getInt("shelf_life"));
        p.setSupplierName(r.getString("supplier_name"));
        p.setRegDate(r.getString("reg_date"));
        return p;
    }
}
