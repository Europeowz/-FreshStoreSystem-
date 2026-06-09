package com.freshstore.ui;

import com.freshstore.entity.User;
import com.freshstore.service.ServiceFactory;
import java.awt.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private final User currentUser;

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

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP);
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.setBackground(BG_COLOR);

        tabs.addTab("仪表盘", new DashboardPanel());
        tabs.addTab("商品管理", new ProductPanel());
        tabs.addTab("商品类别", new CategoryPanel());
        tabs.addTab("库存管理", new InventoryPanel());
        tabs.addTab("销售记录", new SalePanel());
        if ("管理员".equals(user.getRole())) {
            tabs.addTab("用户管理", new UserPanel());
        }

        add(tabs, BorderLayout.CENTER);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        statusBar.setBackground(MAIN_COLOR);
        JLabel statusLabel = new JLabel("当前用户: " + user.getUsername()
                + "  |  角色: " + user.getRole() + "  ");
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.add(statusLabel);

        JButton changePwBtn = new JButton("修改密码");
        changePwBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        changePwBtn.setBackground(new Color(0x15, 0x65, 0xC0));
        changePwBtn.setForeground(Color.WHITE);
        changePwBtn.setFocusPainted(false);
        changePwBtn.addActionListener(e -> showChangePasswordDialog());
        statusBar.add(changePwBtn);

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

    static JButton makeButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));
        return btn;
    }

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

    private void showChangePasswordDialog() {
        JDialog dialog = new JDialog(this, "修改密码", true);
        dialog.setSize(380, 240);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(BG_COLOR);
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel oldLabel = new JLabel("原密码:");
        JLabel newLabel = new JLabel("新密码:");
        JLabel confirmLabel = new JLabel("确认密码:");
        JPasswordField oldPw = new JPasswordField(16);
        JPasswordField newPw = new JPasswordField(16);
        JPasswordField confirmPw = new JPasswordField(16);

        gbc.gridx = 0; gbc.gridy = 0; form.add(oldLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0; form.add(oldPw, gbc);
        gbc.gridx = 0; gbc.gridy = 1; form.add(newLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1; form.add(newPw, gbc);
        gbc.gridx = 0; gbc.gridy = 2; form.add(confirmLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2; form.add(confirmPw, gbc);

        dialog.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttons.setBackground(BG_COLOR);

        JButton okBtn = makeButton("确认修改", MAIN_COLOR);
        JButton cancelBtn = makeButton("取消", new Color(0x9E, 0x9E, 0x9E));
        okBtn.addActionListener(e -> {
            String oldPass = new String(oldPw.getPassword());
            String newPass = new String(newPw.getPassword());
            String confirmPass = new String(confirmPw.getPassword());

            if (oldPass.isBlank() || newPass.isBlank() || confirmPass.isBlank()) {
                JOptionPane.showMessageDialog(dialog, "所有字段不能为空", "提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(dialog, "两次输入的新密码不一致", "提示",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                ServiceFactory.getUserService()
                        .changePassword(currentUser.getUserId(), oldPass, newPass);
                JOptionPane.showMessageDialog(dialog, "密码修改成功", "提示",
                        JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttons.add(okBtn);
        buttons.add(cancelBtn);
        dialog.add(buttons, BorderLayout.SOUTH);

        dialog.getRootPane().setDefaultButton(okBtn);
        dialog.setVisible(true);
    }
}
