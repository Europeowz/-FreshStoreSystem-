import javax.swing.*;
import java.awt.*;

/**
 * Step 2: 往窗口里放东西
 *
 * 新概念：
 *   JPanel    = 面板，用来装其他组件（相当于一个盒子）
 *   JLabel    = 文字标签
 *   JTextField = 输入框
 *   JButton   = 按钮
 *   Layout    = 布局管理器，控制组件怎么排列
 *               BorderLayout: 上北下南左西右东中
 *               GridLayout:   N行M列的网格
 */
public class Step2_Components {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Step 2 - Components");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // ===== 创建一个面板，用网格布局（3行2列） =====
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        //  border 是边距，让内容不贴边，好看一点

        // 第1行第1列：标签
        panel.add(new JLabel("Username:"));
        // 第1行第2列：输入框
        panel.add(new JTextField());

        // 第2行第1列：标签
        panel.add(new JLabel("Password:"));
        // 第2行第2列：密码输入框（输入时显示 ***）
        panel.add(new JPasswordField());

        // 第3行跨两列：按钮
        JButton loginBtn = new JButton("Login");
        panel.add(new JLabel());        // 占位
        panel.add(loginBtn);

        // ===== 按钮点击事件 =====
        loginBtn.addActionListener(e -> {
            // 弹出一个对话框
            JOptionPane.showMessageDialog(frame, "Button clicked!");
        });

        frame.add(panel);
        frame.setVisible(true);
    }
}
