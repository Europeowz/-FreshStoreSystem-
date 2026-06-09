package com.freshstore.ui;

import com.freshstore.entity.User;
import com.freshstore.exception.AuthenticationException;
import com.freshstore.service.ServiceFactory;
import com.freshstore.service.UserService;
import java.awt.*;
import javax.swing.*;

public class LoginFrame extends JFrame {
    private JTextField userField;
    private JPasswordField passField;
    private JButton loginBtn;
    private final UserService userService = ServiceFactory.getUserService();

    private static final Color BG_COLOR   = new Color(0xFA, 0xF7, 0xF0);
    private static final Color MAIN_COLOR = new Color(0x2E, 0x7D, 0x32);

    public LoginFrame() {
        setTitle("生鲜门店管理系统");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(20, 10));
        panel.setBackground(BG_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("生鲜门店管理系统", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 22));
        title.setForeground(MAIN_COLOR);
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 15));
        form.setBackground(BG_COLOR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        form.add(new JLabel("用户名："));
        userField = new JTextField(15);
        form.add(userField);

        form.add(new JLabel("密  码："));
        passField = new JPasswordField(15);
        form.add(passField);

        form.add(new JLabel(""));
        loginBtn = new JButton("登 录");
        loginBtn.setBackground(MAIN_COLOR);
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("微软雅黑", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.addActionListener(e -> doLogin());
        form.add(loginBtn);

        panel.add(form, BorderLayout.CENTER);
        add(panel);
        getRootPane().setDefaultButton(loginBtn);
    }

    private void doLogin() {
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "请输入用户名和密码。",
                "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginBtn.setEnabled(false);
        loginBtn.setText("连接中...");

        try {
            User user = userService.login(username, password);
            dispose();
            new MainFrame(user).setVisible(true);
        } catch (AuthenticationException e) {
            loginBtn.setEnabled(true);
            loginBtn.setText("登 录");
            JOptionPane.showMessageDialog(this,
                "用户名或密码错误！",
                "登录失败", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            loginBtn.setEnabled(true);
            loginBtn.setText("登 录");
            JOptionPane.showMessageDialog(this,
                "数据库连接失败！\n\n"
                + "请检查：\n"
                + "1. freshstore.db 文件是否存在\n"
                + "2. 数据库文件未被其他程序占用\n\n"
                + "错误详情：\n" + ex,
                "连接错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
