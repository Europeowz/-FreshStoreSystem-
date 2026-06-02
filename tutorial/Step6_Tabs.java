import javax.swing.*;
import java.awt.*;

/**
 * Step 6: 多标签页（JTabbedPane）
 *
 * 你现在看到的完整系统主界面就是：
 *   一个 JFrame 里面放一个 JTabbedPane
 *   每个标签页是一个 JPanel
 *
 * 就这么简单。下面是一个 3 个标签页的例子。
 */
public class Step6_Tabs {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Step 6 - Tabbed Pane");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // === 创建标签面板 ===
        JTabbedPane tabs = new JTabbedPane();

        // 添加标签页：每个标签页就是一个普通的 JPanel
        tabs.addTab("Products",  createPanel("Product management goes here"));
        tabs.addTab("Inventory", createPanel("Inventory management goes here"));
        tabs.addTab("Sales",     createPanel("Sales records go here"));

        frame.add(tabs);
        frame.setVisible(true);
    }

    // 帮你快速创建一个有文字的空白面板
    static JPanel createPanel(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));
        panel.add(label);
        return panel;
    }
}
