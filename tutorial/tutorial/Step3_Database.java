import java.sql.*;

/**
 * Step 3: Java 连接数据库（JDBC）
 *
 * 你把 SQL 里学的 SELECT * FROM Product 搬到 Java 里，就三步：
 *   1. Connection  = 拨号（连接数据库）
 *   2. Statement   = 说话（发送 SQL）
 *   3. ResultSet   = 听回答（接收结果）
 *
 * 运行前：确保 simple_gui/ 下已经有 freshstore.db 文件
 *         （双击 run.bat 跑过一次就会自动生成）
 */
public class Step3_Database {
    public static void main(String[] args) throws Exception {
        // 1. 加载 SQLite 驱动（告诉 Java 怎么和 SQLite 说话）
        Class.forName("org.sqlite.JDBC");

        // 2. 连接数据库（freshstore.db 就是那个数据库文件）
        Connection conn = DriverManager.getConnection("jdbc:sqlite:freshstore.db");
        System.out.println("Connected!");

        // 3. 发送 SQL，拿到结果
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT product_id, name, price FROM Product");

        // 4. 遍历结果，一行一行打印
        System.out.println("====== Products ======");
        while (rs.next()) {
            String id    = rs.getString("product_id");
            String name  = rs.getString("name");
            double price = rs.getDouble("price");

            System.out.println(id + " | " + name + " | $" + price);
        }

        // 5. 关闭连接（好习惯）
        rs.close();
        stmt.close();
        conn.close();
    }
}
