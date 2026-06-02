package ui;

import entity.User;
import javax.swing.*;
import java.awt.*;

/**
 * 主窗口 —— 使用 JTabbedPane 组织5个功能模块
 */
public class MainFrame extends JFrame {
    private User currentUser;

    static final Color BG_COLOR    = new Color(0xFA, 0xF7, 0xF0);
    static final Color MAIN_COLOR  = new Color(0x2E, 0x7D, 0x32);
    static final Color ACCENT_COLOR = new Color(0xE6, 0x51, 0x00);

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("生鲜门店管理系统 - " + user.getUsername()
                + " (" + user.getRole() + ")");
        setSize(960, 640);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // 标签面板
        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.setBackground(BG_COLOR);

        tabs.addTab("🥬 商品管理", new ProductPanel());
        tabs.addTab("📂 商品类别", new CategoryPanel());
        tabs.addTab("📦 库存管理", new InventoryPanel());
        tabs.addTab("🛒 销售记录", new SalePanel());
        // 用户管理仅管理员可见
        if ("管理员".equals(user.getRole())) {
            tabs.addTab("👤 用户管理", new UserPanel());
        }

        add(tabs, BorderLayout.CENTER);

        // 底部状态栏
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        statusBar.setBackground(MAIN_COLOR);
        JLabel statusLabel = new JLabel("当前用户: " + user.getUsername()
                + "  |  角色: " + user.getRole() + "  ");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.add(statusLabel);

        JButton logoutBtn = new JButton("退出登录");
        logoutBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        logoutBtn.setBackground(new Color(0xC6, 0x28, 0x28));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        statusBar.add(logoutBtn);
        add(statusBar, BorderLayout.SOUTH);
    }

    /** 统一按钮样式 */
    static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        return btn;
    }

    /** 统一创建带标题边框的面板 */
    static JPanel makeTitledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(BG_COLOR);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(MAIN_COLOR, 1),
                title, 0, 0,
                new Font("SansSerif", Font.BOLD, 13), MAIN_COLOR)
        ));
        return p;
    }
}
