import javax.swing.*;

/**
 * Step 1: 你的第一个 Swing 窗口
 *
 * 三个核心概念：
 *   JFrame   = 窗口容器，相当于一张桌子
 *   setSize  = 窗口宽高（像素）
 *   setVisible = 让窗口显示出来
 *
 * 跑起来你会看到一个空白窗口，点 X 可以关掉。
 */
public class Step1_Window {
    public static void main(String[] args) {
        // 1. 创建一个窗口对象
        JFrame frame = new JFrame("Step 1 - Hello Window");

        // 2. 设置窗口大小：宽 400，高 300
        frame.setSize(400, 300);

        // 3. 点 X 时程序退出
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 4. 居中显示
        frame.setLocationRelativeTo(null);

        // 5. 让窗口可见（这步最重要！忘了窗口就不会出来）
        frame.setVisible(true);
    }
}
