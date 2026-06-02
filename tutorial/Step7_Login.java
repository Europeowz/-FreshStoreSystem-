import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 * Step 7: 登录流程
 *
 * 这是你完整系统里 LoginFrame 的简化版。
 * 原理：用户名密码 → 查数据库 → 匹配成功就打开主窗口
 *
 * 关键 SQL：
 *   SELECT * FROM Users WHERE username=? AND password=?
 *
 * 用 PreparedStatement（不用拼字符串！），防 SQL 注入。
 */
public class Step7_Login {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");

        // === 登录窗口 ===
        JFrame loginFrame = new JFrame("Login");
        loginFrame.setSize(350, 200);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JLabel msgLabel = new JLabel("Hint: admin / 123456", SwingConstants.CENTER);

        panel.add(new JLabel("Username:"));
        panel.add(userField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(new JLabel());
        panel.add(loginBtn);
        panel.add(new JLabel());  // spacer
        panel.add(msgLabel);

        loginFrame.add(panel);

        // === 登录按钮逻辑 ===
        loginBtn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword()).trim();

            try {
                Connection conn = DriverManager.getConnection("jdbc:sqlite:freshstore.db");

                // PreparedStatement: ? 是占位符，自动防注入
                PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM Users WHERE username=? AND password=?");
                ps.setString(1, user);
                ps.setString(2, pass);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    // 登录成功 → 关登录窗 → 开主窗
                    String role = rs.getString("role");
                    loginFrame.dispose();
                    openMainWindow(user, role);
                } else {
                    msgLabel.setText("Wrong username or password!");
                    msgLabel.setForeground(Color.RED);
                }
                rs.close(); ps.close(); conn.close();
            } catch (Exception ex) {
                msgLabel.setText("Error: " + ex.getMessage());
            }
        });

        // 回车键也能登录
        loginFrame.getRootPane().setDefaultButton(loginBtn);

        loginFrame.setVisible(true);
    }

    /** 主窗口（简化版：只有一个标签页） */
    static void openMainWindow(String username, String role) {
        JFrame main = new JFrame("Welcome, " + username + " [" + role + "]");
        main.setSize(600, 400);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setLocationRelativeTo(null);

        JLabel welcome = new JLabel("Login successful! Role: " + role,
                SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 20));
        main.add(welcome);

        main.setVisible(true);
    }
}
