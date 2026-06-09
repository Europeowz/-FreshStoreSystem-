package com.freshstore.repository;

import com.freshstore.entity.Sale;
import com.freshstore.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** 销售数据访问 JDBC 实现 */
public class JdbcSaleRepository implements SaleRepository {

    @Override
    public List<Sale> listAll() throws Exception {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.*, p.name AS product_name FROM Sale s "
                   + "JOIN Product p ON s.product_id = p.product_id "
                   + "ORDER BY s.sale_date DESC, s.sale_id";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet r = st.executeQuery(sql)) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    @Override
    public List<Sale> searchByProduct(String keyword) throws Exception {
        List<Sale> list = new ArrayList<>();
        String sql = "SELECT s.*, p.name AS product_name FROM Sale s "
                   + "JOIN Product p ON s.product_id = p.product_id "
                   + "WHERE p.name LIKE ? ORDER BY s.sale_date DESC";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, "%" + keyword + "%");
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) list.add(map(r));
            }
        }
        return list;
    }

    @Override
    public void add(Sale s) throws Exception {
        String sql = "INSERT INTO Sale(sale_id,store_id,product_id,quantity,"
                   + "amount,sale_date,user_id) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, s.getSaleId());
            p.setString(2, s.getStoreId());
            p.setString(3, s.getProductId());
            p.setInt(4,    s.getQuantity());
            p.setDouble(5, s.getAmount());
            p.setString(6, s.getSaleDate());
            p.setString(7, s.getUserId());
            p.executeUpdate();
        }
    }

    @Override
    public void delete(String saleId) throws Exception {
        String sql = "DELETE FROM Sale WHERE sale_id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, saleId);
            p.executeUpdate();
        }
    }

    private Sale map(ResultSet r) throws SQLException {
        Sale s = new Sale();
        s.setSaleId(r.getString("sale_id"));
        s.setStoreId(r.getString("store_id"));
        s.setProductId(r.getString("product_id"));
        s.setProductName(r.getString("product_name"));
        s.setQuantity(r.getInt("quantity"));
        s.setAmount(r.getDouble("amount"));
        s.setSaleDate(r.getString("sale_date"));
        s.setUserId(r.getString("user_id"));
        return s;
    }
}
