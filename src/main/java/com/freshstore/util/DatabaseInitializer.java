package com.freshstore.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/** Auto-creates tables and seeds sample data on first run. */
public final class DatabaseInitializer {
    private DatabaseInitializer() {}

    public static void init() throws Exception {
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement()) {

            createTables(s);
            if (hasData(s)) return;
            seedData(c);
        }
    }

    private static void createTables(Statement s) throws Exception {
        s.execute("CREATE TABLE IF NOT EXISTS Category ("
                + "category_id   TEXT PRIMARY KEY, "
                + "category_name TEXT NOT NULL UNIQUE)");
        s.execute("CREATE TABLE IF NOT EXISTS Users ("
                + "user_id   TEXT PRIMARY KEY, "
                + "username  TEXT NOT NULL UNIQUE, "
                + "password  TEXT NOT NULL, "
                + "role      TEXT NOT NULL)");
        s.execute("CREATE TABLE IF NOT EXISTS Product ("
                + "product_id    TEXT PRIMARY KEY, "
                + "name          TEXT NOT NULL, "
                + "category_id   TEXT NOT NULL, "
                + "spec          TEXT, "
                + "unit          TEXT NOT NULL, "
                + "cost          REAL NOT NULL, "
                + "price         REAL NOT NULL, "
                + "shelf_life    INTEGER, "
                + "supplier_name TEXT, "
                + "reg_date      TEXT, "
                + "FOREIGN KEY (category_id) REFERENCES Category(category_id))");
        s.execute("CREATE TABLE IF NOT EXISTS Inventory ("
                + "product_id TEXT PRIMARY KEY, "
                + "stock      INTEGER NOT NULL, "
                + "max_stock  INTEGER, "
                + "min_stock  INTEGER, "
                + "location   TEXT, "
                + "last_in    TEXT, "
                + "user_id    TEXT, "
                + "FOREIGN KEY (product_id) REFERENCES Product(product_id), "
                + "FOREIGN KEY (user_id)    REFERENCES Users(user_id))");
        s.execute("CREATE TABLE IF NOT EXISTS Sale ("
                + "sale_id    TEXT PRIMARY KEY, "
                + "store_id   TEXT, "
                + "product_id TEXT NOT NULL, "
                + "quantity   INTEGER NOT NULL, "
                + "amount     REAL NOT NULL, "
                + "sale_date  TEXT, "
                + "user_id    TEXT, "
                + "FOREIGN KEY (product_id) REFERENCES Product(product_id), "
                + "FOREIGN KEY (user_id)    REFERENCES Users(user_id))");
    }

    private static boolean hasData(Statement s) throws Exception {
        try (ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM Category")) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private static void seedData(Connection c) throws Exception {
        seedCategories(c);
        seedUsers(c);
        seedProducts(c);
        seedInventory(c);
        seedSales(c);
        System.out.println("[OK] Database initialized with sample data.");
    }

    private static void seedCategories(Connection c) throws Exception {
        try (PreparedStatement p = c.prepareStatement(
                "INSERT INTO Category VALUES (?,?)")) {
            p.setString(1, "C01"); p.setString(2, "蔬菜"); p.executeUpdate();
            p.setString(1, "C02"); p.setString(2, "水果"); p.executeUpdate();
            p.setString(1, "C03"); p.setString(2, "肉类"); p.executeUpdate();
            p.setString(1, "C04"); p.setString(2, "水产"); p.executeUpdate();
            p.setString(1, "C05"); p.setString(2, "乳制品"); p.executeUpdate();
        }
    }

    private static void seedUsers(Connection c) throws Exception {
        String adminPw = HashUtil.hash("123456");
        String buyerPw = HashUtil.hash("123456");
        String sellerPw = HashUtil.hash("123456");
        try (PreparedStatement p = c.prepareStatement(
                "INSERT INTO Users VALUES (?,?,?,?)")) {
            p.setString(1, "U001"); p.setString(2, "admin");
            p.setString(3, adminPw); p.setString(4, "管理员"); p.executeUpdate();
            p.setString(1, "U002"); p.setString(2, "buyer1");
            p.setString(3, buyerPw); p.setString(4, "采购员"); p.executeUpdate();
            p.setString(1, "U003"); p.setString(2, "seller1");
            p.setString(3, sellerPw); p.setString(4, "销售员"); p.executeUpdate();
        }
    }

    private static void seedProducts(Connection c) throws Exception {
        String sql = "INSERT INTO Product VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            // P001
            p.setString(1, "P001"); p.setString(2, "西红柿");
            p.setString(3, "C01");  p.setString(4, "1斤/袋");
            p.setString(5, "斤");   p.setDouble(6, 2.00);
            p.setDouble(7, 3.50);    p.setInt(8, 5);
            p.setString(9, "绿源蔬菜基地"); p.setString(10, "2024-01-10");
            p.executeUpdate();
            // P002
            p.setString(1, "P002"); p.setString(2, "黄瓜");
            p.setString(3, "C01");  p.setString(4, "1斤/袋");
            p.setString(5, "斤");   p.setDouble(6, 1.50);
            p.setDouble(7, 2.80);    p.setInt(8, 4);
            p.setString(9, "绿源蔬菜基地"); p.setString(10, "2024-01-11");
            p.executeUpdate();
            // P003
            p.setString(1, "P003"); p.setString(2, "苹果");
            p.setString(3, "C02");  p.setString(4, "2斤/袋");
            p.setString(5, "斤");   p.setDouble(6, 3.00);
            p.setDouble(7, 5.50);    p.setInt(8, 15);
            p.setString(9, "鲜果供应基地"); p.setString(10, "2024-01-11");
            p.executeUpdate();
            // P004
            p.setString(1, "P004"); p.setString(2, "香蕉");
            p.setString(3, "C02");  p.setString(4, "1斤/把");
            p.setString(5, "斤");   p.setDouble(6, 2.20);
            p.setDouble(7, 4.00);    p.setInt(8, 7);
            p.setString(9, "鲜果供应基地"); p.setString(10, "2024-01-12");
            p.executeUpdate();
            // P005
            p.setString(1, "P005"); p.setString(2, "猪肉");
            p.setString(3, "C03");  p.setString(4, "500g/盒");
            p.setString(5, "盒");   p.setDouble(6, 10.00);
            p.setDouble(7, 16.00);   p.setInt(8, 3);
            p.setString(9, "牧原肉品集团"); p.setString(10, "2024-01-12");
            p.executeUpdate();
            // P006
            p.setString(1, "P006"); p.setString(2, "牛肉");
            p.setString(3, "C03");  p.setString(4, "500g/盒");
            p.setString(5, "盒");   p.setDouble(6, 20.00);
            p.setDouble(7, 32.00);   p.setInt(8, 3);
            p.setString(9, "牧原肉品集团"); p.setString(10, "2024-01-13");
            p.executeUpdate();
            // P007
            p.setString(1, "P007"); p.setString(2, "娃娃菜");
            p.setString(3, "C01");  p.setString(4, "2斤/袋");
            p.setString(5, "斤");   p.setDouble(6, 1.80);
            p.setDouble(7, 3.20);    p.setInt(8, 5);
            p.setString(9, "绿源蔬菜基地"); p.setString(10, "2024-01-14");
            p.executeUpdate();
            // P008
            p.setString(1, "P008"); p.setString(2, "三文鱼");
            p.setString(3, "C04");  p.setString(4, "200g/盒");
            p.setString(5, "盒");   p.setDouble(6, 25.00);
            p.setDouble(7, 45.00);   p.setInt(8, 2);
            p.setString(9, "海鲜直供中心"); p.setString(10, "2024-01-15");
            p.executeUpdate();
            // P009
            p.setString(1, "P009"); p.setString(2, "牛奶");
            p.setString(3, "C05");  p.setString(4, "1L/瓶");
            p.setString(5, "瓶");   p.setDouble(6, 5.00);
            p.setDouble(7, 8.50);    p.setInt(8, 7);
            p.setString(9, "乳品牧场"); p.setString(10, "2024-01-15");
            p.executeUpdate();
            // P010
            p.setString(1, "P010"); p.setString(2, "鸡蛋");
            p.setString(3, "C03");  p.setString(4, "10枚/盒");
            p.setString(5, "盒");   p.setDouble(6, 6.00);
            p.setDouble(7, 10.00);   p.setInt(8, 15);
            p.setString(9, "牧原肉品集团"); p.setString(10, "2024-01-16");
            p.executeUpdate();
        }
    }

    private static void seedInventory(Connection c) throws Exception {
        String sql = "INSERT INTO Inventory VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, "P001"); p.setInt(2, 80);  p.setInt(3, 100);
            p.setInt(4, 20);       p.setString(5, "A区-01架");
            p.setString(6, "2024-01-15"); p.setString(7, "U002");
            p.executeUpdate();
            p.setString(1, "P002"); p.setInt(2, 60);  p.setInt(3, 100);
            p.setInt(4, 20);       p.setString(5, "A区-02架");
            p.setString(6, "2024-01-15"); p.setString(7, "U002");
            p.executeUpdate();
            p.setString(1, "P003"); p.setInt(2, 120); p.setInt(3, 150);
            p.setInt(4, 30);       p.setString(5, "B区-01架");
            p.setString(6, "2024-01-16"); p.setString(7, "U002");
            p.executeUpdate();
            p.setString(1, "P004"); p.setInt(2, 90);  p.setInt(3, 120);
            p.setInt(4, 25);       p.setString(5, "B区-02架");
            p.setString(6, "2024-01-16"); p.setString(7, "U002");
            p.executeUpdate();
            p.setString(1, "P005"); p.setInt(2, 40);  p.setInt(3, 60);
            p.setInt(4, 10);       p.setString(5, "C区-01架");
            p.setString(6, "2024-01-17"); p.setString(7, "U002");
            p.executeUpdate();
            p.setString(1, "P006"); p.setInt(2, 35);  p.setInt(3, 60);
            p.setInt(4, 10);       p.setString(5, "C区-02架");
            p.setString(6, "2024-01-17"); p.setString(7, "U002");
            p.executeUpdate();
            p.setString(1, "P007"); p.setInt(2, 70);  p.setInt(3, 100);
            p.setInt(4, 20);       p.setString(5, "A区-03架");
            p.setString(6, "2024-01-18"); p.setString(7, "U002");
            p.executeUpdate();
            p.setString(1, "P008"); p.setInt(2, 25);  p.setInt(3, 50);
            p.setInt(4, 8);        p.setString(5, "D区-01架");
            p.setString(6, "2024-01-18"); p.setString(7, "U002");
            p.executeUpdate();
            p.setString(1, "P009"); p.setInt(2, 55);  p.setInt(3, 80);
            p.setInt(4, 15);       p.setString(5, "E区-01架");
            p.setString(6, "2024-01-19"); p.setString(7, "U002");
            p.executeUpdate();
            p.setString(1, "P010"); p.setInt(2, 100); p.setInt(3, 150);
            p.setInt(4, 30);       p.setString(5, "C区-03架");
            p.setString(6, "2024-01-19"); p.setString(7, "U002");
            p.executeUpdate();
        }
    }

    private static void seedSales(Connection c) throws Exception {
        String sql = "INSERT INTO Sale VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, "S001"); p.setString(2, "门店01");
            p.setString(3, "P001"); p.setInt(4, 10);
            p.setDouble(5, 35.00);  p.setString(6, "2024-01-20");
            p.setString(7, "U003"); p.executeUpdate();
            p.setString(1, "S002"); p.setString(2, "门店01");
            p.setString(3, "P002"); p.setInt(4, 5);
            p.setDouble(5, 14.00);  p.setString(6, "2024-01-20");
            p.setString(7, "U003"); p.executeUpdate();
            p.setString(1, "S003"); p.setString(2, "门店02");
            p.setString(3, "P003"); p.setInt(4, 8);
            p.setDouble(5, 44.00);  p.setString(6, "2024-01-21");
            p.setString(7, "U003"); p.executeUpdate();
            p.setString(1, "S004"); p.setString(2, "门店02");
            p.setString(3, "P005"); p.setInt(4, 3);
            p.setDouble(5, 48.00);  p.setString(6, "2024-01-21");
            p.setString(7, "U003"); p.executeUpdate();
            p.setString(1, "S005"); p.setString(2, "门店01");
            p.setString(3, "P001"); p.setInt(4, 15);
            p.setDouble(5, 52.50);  p.setString(6, "2024-01-22");
            p.setString(7, "U003"); p.executeUpdate();
            p.setString(1, "S006"); p.setString(2, "门店03");
            p.setString(3, "P004"); p.setInt(4, 12);
            p.setDouble(5, 48.00);  p.setString(6, "2024-01-22");
            p.setString(7, "U003"); p.executeUpdate();
            p.setString(1, "S007"); p.setString(2, "门店01");
            p.setString(3, "P009"); p.setInt(4, 6);
            p.setDouble(5, 51.00);  p.setString(6, "2024-01-23");
            p.setString(7, "U003"); p.executeUpdate();
            p.setString(1, "S008"); p.setString(2, "门店02");
            p.setString(3, "P010"); p.setInt(4, 8);
            p.setDouble(5, 80.00);  p.setString(6, "2024-01-23");
            p.setString(7, "U003"); p.executeUpdate();
        }
    }
}
