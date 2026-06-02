import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Step 4: GUI + Database = 表格显示数据
 *
 * 核心：JTable（表格） + DefaultTableModel（表格的数据模型）
 * 把 Step3 的 ResultSet 数据灌到 Step2 的窗口中
 *
 * 这个文件是完整可运行的！
 * 运行命令（在 simple_gui 目录下）：
 *   javac -cp "lib\sqlite-jdbc-3.49.1.0.jar" tutorial\Step4_Table.java
 *   java  -cp ".;tutorial;lib\sqlite-jdbc-3.49.1.0.jar" Step4_Table
 */
public class Step4_Table {
    public static void main(String[] args) throws Exception {
        // === 1. 从数据库拿数据 ===
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:freshstore.db");

        // === 2. 创建窗口 ===
        JFrame frame = new JFrame("Step 4 - Table Display");
        frame.setSize(700, 350);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // === 3. 创建表格 ===
        // 表格 = 列名 + 数据模型
        String[] columns = {"ID", "Name", "Category", "Cost", "Price"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // 从数据库取数据，一行一行灌进去
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(
            "SELECT p.product_id, p.name, c.category_name, p.cost, p.price "
            + "FROM Product p JOIN Category c ON p.category_id = c.category_id");

        while (rs.next()) {
            model.addRow(new Object[]{
                rs.getString("product_id"),
                rs.getString("name"),
                rs.getString("category_name"),
                rs.getDouble("cost"),
                rs.getDouble("price")
            });
        }
        rs.close(); stmt.close(); conn.close();

        // 用数据模型创建 JTable，放进滚动面板
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);  // 数据多了可以滚动
        frame.add(scrollPane);

        frame.setVisible(true);
    }
}
