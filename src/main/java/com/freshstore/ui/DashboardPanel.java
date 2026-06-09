package com.freshstore.ui;

import com.freshstore.entity.*;
import com.freshstore.service.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;

public class DashboardPanel extends JPanel {
    private final JPanel cardPanel;
    private final JLabel lowStockLabel;
    private final JLabel recentSalesLabel;

    public DashboardPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBackground(MainFrame.BG_COLOR);
        JButton refreshBtn = MainFrame.makeButton("刷新", new Color(0x75, 0x75, 0x75));
        refreshBtn.addActionListener(e -> refresh());
        toolbar.add(refreshBtn);
        add(toolbar, BorderLayout.NORTH);

        cardPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        cardPanel.setBackground(MainFrame.BG_COLOR);
        add(cardPanel, BorderLayout.CENTER);

        JPanel alertPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        alertPanel.setBackground(MainFrame.BG_COLOR);
        lowStockLabel = new JLabel(" ");
        lowStockLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        alertPanel.add(lowStockLabel);
        recentSalesLabel = new JLabel(" ");
        recentSalesLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        alertPanel.add(recentSalesLabel);
        add(alertPanel, BorderLayout.SOUTH);

        refresh();
    }

    void refresh() {
        cardPanel.removeAll();
        try {
            var productSvc = ServiceFactory.getProductService();
            var categorySvc = ServiceFactory.getCategoryService();
            var inventorySvc = ServiceFactory.getInventoryService();
            var saleSvc = ServiceFactory.getSaleService();

            List<Product> products = productSvc.listAll();
            List<Category> categories = categorySvc.listAll();
            List<Inventory> inventory = inventorySvc.listAll();
            List<Sale> sales = saleSvc.listAll();

            long lowStockCount = inventory.stream()
                    .filter(i -> i.getStock() < i.getMinStock()).count();
            double totalSales = sales.stream().mapToDouble(Sale::getAmount).sum();
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            double todaySales = sales.stream()
                    .filter(s -> today.equals(s.getSaleDate()))
                    .mapToDouble(Sale::getAmount).sum();

            cardPanel.add(makeCard("商品总数", String.valueOf(products.size()), "共 " + categories.size() + " 个类别"));
            cardPanel.add(makeCard("库存项目", String.valueOf(inventory.size()),
                    "缺货预警: " + lowStockCount + " 项"));
            cardPanel.add(makeCard("销售总额", String.format("%.2f", totalSales), "历史累计销售"));
            cardPanel.add(makeCard("今日销售额", String.format("%.2f", todaySales), "日期: " + today));
            cardPanel.add(makeCard("销售记录数", String.valueOf(sales.size()), "共 " + sales.size() + " 笔交易"));
            cardPanel.add(makeCard("库存总件数", String.valueOf(inventory.stream().mapToInt(Inventory::getStock).sum()),
                    "所有商品库存合计"));

            if (lowStockCount > 0) {
                lowStockLabel.setText(" 库存预警: " + lowStockCount + " 项商品库存低于下限，需要补货！");
                lowStockLabel.setForeground(new Color(0xC6, 0x28, 0x28));
            } else {
                lowStockLabel.setText(" 库存状态正常");
                lowStockLabel.setForeground(MainFrame.MAIN_COLOR);
            }
            recentSalesLabel.setText(" 最近 " + Math.min(sales.size(), 5) + " 笔销售记录 | 今日销售: "
                    + String.format("%.2f", todaySales) + " 元");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载仪表盘失败: " + e.getMessage());
        }
    }

    private static JPanel makeCard(String title, String value, String sub) {
        JPanel card = new JPanel(new BorderLayout(0, 8));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xE0, 0xE0, 0xE0), 1),
                BorderFactory.createEmptyBorder(18, 20, 18, 20)));

        JLabel titleL = new JLabel(title);
        titleL.setFont(new Font("SansSerif", Font.PLAIN, 13));
        titleL.setForeground(new Color(0x75, 0x75, 0x75));
        card.add(titleL, BorderLayout.NORTH);

        JLabel valL = new JLabel(value);
        valL.setFont(new Font("SansSerif", Font.BOLD, 28));
        valL.setForeground(MainFrame.MAIN_COLOR);
        card.add(valL, BorderLayout.CENTER);

        JLabel subL = new JLabel(sub);
        subL.setFont(new Font("SansSerif", Font.PLAIN, 11));
        subL.setForeground(new Color(0x9E, 0x9E, 0x9E));
        card.add(subL, BorderLayout.SOUTH);

        return card;
    }
}
