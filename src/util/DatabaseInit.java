package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 数据库初始化 —— 首次运行时自动建表并插入示例数据
 */
public class DatabaseInit {

    public static void init() throws Exception {
        try (Connection c = DBUtil.getConnection();
             Statement s = c.createStatement()) {

            // ============ 建表（IF NOT EXISTS：已存在则跳过） ============

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

            // ============ 插入初始数据（仅当表为空时） ============

            // 检查是否已有数据
            var rs = s.executeQuery("SELECT COUNT(*) FROM Category");
            if (rs.next() && rs.getInt(1) > 0) {
                return; // 已有数据，跳过初始化
            }

            // 类别
            s.execute("INSERT INTO Category VALUES ('C01','蔬菜')");
            s.execute("INSERT INTO Category VALUES ('C02','水果')");
            s.execute("INSERT INTO Category VALUES ('C03','肉类')");
            s.execute("INSERT INTO Category VALUES ('C04','水产')");
            s.execute("INSERT INTO Category VALUES ('C05','乳制品')");

            // 用户（密码 SHA-256 哈希存储）
            s.execute("INSERT INTO Users VALUES ('U001','admin','"
                + HashUtil.hash("123456") + "','管理员')");
            s.execute("INSERT INTO Users VALUES ('U002','buyer1','"
                + HashUtil.hash("123456") + "','采购员')");
            s.execute("INSERT INTO Users VALUES ('U003','seller1','"
                + HashUtil.hash("123456") + "','销售员')");

            // 商品
            s.execute("INSERT INTO Product VALUES "
                + "('P001','西红柿','C01','1斤/袋','斤',2.00,3.50,5,'绿源蔬菜基地','2024-01-10')");
            s.execute("INSERT INTO Product VALUES "
                + "('P002','黄瓜','C01','1斤/袋','斤',1.50,2.80,4,'绿源蔬菜基地','2024-01-11')");
            s.execute("INSERT INTO Product VALUES "
                + "('P003','苹果','C02','2斤/袋','斤',3.00,5.50,15,'鲜果供应基地','2024-01-11')");
            s.execute("INSERT INTO Product VALUES "
                + "('P004','香蕉','C02','1斤/把','斤',2.20,4.00,7,'鲜果供应基地','2024-01-12')");
            s.execute("INSERT INTO Product VALUES "
                + "('P005','猪肉','C03','500g/盒','盒',10.00,16.00,3,'牧原肉品集团','2024-01-12')");
            s.execute("INSERT INTO Product VALUES "
                + "('P006','牛肉','C03','500g/盒','盒',20.00,32.00,3,'牧原肉品集团','2024-01-13')");
            s.execute("INSERT INTO Product VALUES "
                + "('P007','娃娃菜','C01','2斤/袋','斤',1.80,3.20,5,'绿源蔬菜基地','2024-01-14')");
            s.execute("INSERT INTO Product VALUES "
                + "('P008','三文鱼','C04','200g/盒','盒',25.00,45.00,2,'海鲜直供中心','2024-01-15')");
            s.execute("INSERT INTO Product VALUES "
                + "('P009','牛奶','C05','1L/瓶','瓶',5.00,8.50,7,'乳品牧场','2024-01-15')");
            s.execute("INSERT INTO Product VALUES "
                + "('P010','鸡蛋','C03','10枚/盒','盒',6.00,10.00,15,'牧原肉品集团','2024-01-16')");

            // 库存
            s.execute("INSERT INTO Inventory VALUES "
                + "('P001',80,100,20,'A区-01架','2024-01-15','U002')");
            s.execute("INSERT INTO Inventory VALUES "
                + "('P002',60,100,20,'A区-02架','2024-01-15','U002')");
            s.execute("INSERT INTO Inventory VALUES "
                + "('P003',120,150,30,'B区-01架','2024-01-16','U002')");
            s.execute("INSERT INTO Inventory VALUES "
                + "('P004',90,120,25,'B区-02架','2024-01-16','U002')");
            s.execute("INSERT INTO Inventory VALUES "
                + "('P005',40,60,10,'C区-01架','2024-01-17','U002')");
            s.execute("INSERT INTO Inventory VALUES "
                + "('P006',35,60,10,'C区-02架','2024-01-17','U002')");
            s.execute("INSERT INTO Inventory VALUES "
                + "('P007',70,100,20,'A区-03架','2024-01-18','U002')");
            s.execute("INSERT INTO Inventory VALUES "
                + "('P008',25,50,8,'D区-01架','2024-01-18','U002')");
            s.execute("INSERT INTO Inventory VALUES "
                + "('P009',55,80,15,'E区-01架','2024-01-19','U002')");
            s.execute("INSERT INTO Inventory VALUES "
                + "('P010',100,150,30,'C区-03架','2024-01-19','U002')");

            // 销售记录
            s.execute("INSERT INTO Sale VALUES "
                + "('S001','门店01','P001',10,35.00,'2024-01-20','U003')");
            s.execute("INSERT INTO Sale VALUES "
                + "('S002','门店01','P002',5,14.00,'2024-01-20','U003')");
            s.execute("INSERT INTO Sale VALUES "
                + "('S003','门店02','P003',8,44.00,'2024-01-21','U003')");
            s.execute("INSERT INTO Sale VALUES "
                + "('S004','门店02','P005',3,48.00,'2024-01-21','U003')");
            s.execute("INSERT INTO Sale VALUES "
                + "('S005','门店01','P001',15,52.50,'2024-01-22','U003')");
            s.execute("INSERT INTO Sale VALUES "
                + "('S006','门店03','P004',12,48.00,'2024-01-22','U003')");
            s.execute("INSERT INTO Sale VALUES "
                + "('S007','门店01','P009',6,51.00,'2024-01-23','U003')");
            s.execute("INSERT INTO Sale VALUES "
                + "('S008','门店02','P010',8,80.00,'2024-01-23','U003')");

            System.out.println("[OK] Database initialized with sample data.");
        }
    }
}
