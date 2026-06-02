import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Step 5: 搜索筛选——把你在 SQL 里学的 SELECT ... WHERE name LIKE '%菜%'
 *         变成一个带搜索框的 GUI
 *
 * 核心思路：
 *   用户输入 → 点搜索 → 拼接 LIKE 语句 → 查询数据库 → 刷新表格
 *
 * 这一步你就掌握了 GUI 程序里最常用的模式：
 *   搜索框 + 表格 = 数据浏览界面
 */
public class Step5_Search {
    static JTable table;
    static DefaultTableModel model;

    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");

        JFrame frame = new JFrame("Step 5 - Search");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // === 顶部：搜索栏 ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        JTextField searchField = new JTextField(15);
        topPanel.add(searchField);
        JButton searchBtn = new JButton("Search");
        topPanel.add(searchBtn);
        frame.add(topPanel, BorderLayout.NORTH);

        // === 中间：表格 ===
        String[] cols = {"ID", "Name", "Category", "Price"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        frame.add(new JScrollPane(table), BorderLayout.CENTER);

        // 初始加载全部数据
        loadData("");

        // === 搜索按钮事件 ===
        searchBtn.addActionListener(e -> loadData(searchField.getText().trim()));

        // 回车也能搜索
        searchField.addActionListener(e -> loadData(searchField.getText().trim()));

        frame.setVisible(true);
    }

    /** 根据关键词从数据库查数据并刷新表格 */
    static void loadData(String keyword) {
        model.setRowCount(0);  // 先清空表格
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:freshstore.db");

            String sql = "SELECT p.product_id, p.name, c.category_name, p.price "
                       + "FROM Product p JOIN Category c ON p.category_id = c.category_id";

            // 如果有关键词，加上 WHERE LIKE
            if (!keyword.isEmpty()) {
                sql += " WHERE p.name LIKE ?";
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            if (!keyword.isEmpty()) {
                ps.setString(1, "%" + keyword + "%");  // LIKE '%keyword%'
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("product_id"),
                    rs.getString("name"),
                    rs.getString("category_name"),
                    rs.getDouble("price")
                });
            }
            rs.close(); ps.close(); conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
