package com.freshstore.ui;

import com.freshstore.entity.Category;
import com.freshstore.entity.Product;
import com.freshstore.service.ProductService;
import com.freshstore.service.ServiceFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProductPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final JComboBox<String> categoryCombo;
    private final ProductService productService = ServiceFactory.getProductService();

    public ProductPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(MainFrame.BG_COLOR);

        searchField = new JTextField(15);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toolbar.add(new JLabel("搜索:"));
        toolbar.add(searchField);

        JButton searchBtn = MainFrame.makeButton("搜索", MainFrame.MAIN_COLOR);
        searchBtn.addActionListener(e -> search());
        toolbar.add(searchBtn);

        toolbar.add(new JLabel("  类别:"));
        categoryCombo = new JComboBox<>();
        categoryCombo.addItem("全部");
        categoryCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        try {
            for (Category c : ServiceFactory.getCategoryService().listAll())
                categoryCombo.addItem(c.getCategoryId() + " " + c.getCategoryName());
        } catch (Exception ignored) {}
        toolbar.add(categoryCombo);

        JButton filterBtn = MainFrame.makeButton("筛选", new Color(0x15, 0x65, 0xC0));
        filterBtn.addActionListener(e -> filterByCategory());
        toolbar.add(filterBtn);

        JButton addBtn = MainFrame.makeButton("+ 添加", MainFrame.ACCENT_COLOR);
        addBtn.addActionListener(e -> showEditDialog(null));
        toolbar.add(addBtn);

        JButton refreshBtn = MainFrame.makeButton("刷新", new Color(0x75, 0x75, 0x75));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        JButton exportBtn = MainFrame.makeButton("导出CSV", new Color(0x42, 0x42, 0x42));
        exportBtn.addActionListener(e -> exportCSV());
        toolbar.add(exportBtn);

        add(toolbar, BorderLayout.NORTH);

        String[] cols = {"编号", "名称", "类别", "规格", "单位", "进价", "售价", "保质期(天)", "供应商", "登记日期"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(MainFrame.MAIN_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0xC8, 0xE6, 0xC9));
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottom.setBackground(MainFrame.BG_COLOR);

        JButton editBtn = MainFrame.makeButton("修改", new Color(0x15, 0x65, 0xC0));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) tableModel.getValueAt(modelRow, 0);
            try {
                for (Product p : productService.listAll()) {
                    if (p.getProductId().equals(id)) { showEditDialog(p); return; }
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "查询失败: " + ex.getMessage()); }
        });
        bottom.add(editBtn);

        JButton delBtn = MainFrame.makeButton("删除", new Color(0xC6, 0x28, 0x28));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) tableModel.getValueAt(modelRow, 0);
            String name = (String) tableModel.getValueAt(modelRow, 1);
            int ok = JOptionPane.showConfirmDialog(this,
                "确定删除商品 [" + name + "] 吗？", "确认删除", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                try { productService.delete(id); loadData(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage()); }
            }
        });
        bottom.add(delBtn);
        add(bottom, BorderLayout.SOUTH);

        bindF5Refresh();
        loadData();
    }

    void loadData() {
        tableModel.setRowCount(0);
        try {
            for (Product p : productService.listAll()) addRow(p);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载数据失败: " + e.getMessage());
        }
    }

    private void search() {
        String kw = searchField.getText().trim();
        tableModel.setRowCount(0);
        try {
            List<Product> list = kw.isEmpty() ? productService.listAll() : productService.searchByName(kw);
            for (Product p : list) addRow(p);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "查询失败: " + e.getMessage());
        }
    }

    private void filterByCategory() {
        String sel = (String) categoryCombo.getSelectedItem();
        if (sel == null || "全部".equals(sel)) { loadData(); return; }
        String catId = sel.split(" ")[0];
        tableModel.setRowCount(0);
        try {
            for (Product p : productService.listByCategory(catId)) addRow(p);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "查询失败: " + e.getMessage());
        }
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("products.csv"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try (PrintWriter w = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(fc.getSelectedFile()), "UTF-8"))) {
            for (int c = 0; c < tableModel.getColumnCount(); c++)
                w.print((c > 0 ? "," : "") + "\"" + tableModel.getColumnName(c) + "\"");
            w.println();
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                for (int c = 0; c < tableModel.getColumnCount(); c++)
                    w.print((c > 0 ? "," : "") + "\"" + tableModel.getValueAt(r, c) + "\"");
                w.println();
            }
            JOptionPane.showMessageDialog(this, "导出成功");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage());
        }
    }

    private void bindF5Refresh() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), "refresh");
        getActionMap().put("refresh", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { loadData(); }
        });
    }

    private void addRow(Product p) {
        tableModel.addRow(new Object[]{
            p.getProductId(), p.getName(), p.getCategoryName(),
            p.getSpec(), p.getUnit(), p.getCost(), p.getPrice(),
            p.getShelfLife(), p.getSupplierName(), p.getRegDate()
        });
    }

    private void showEditDialog(Product existing) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            existing == null ? "添加商品" : "修改商品", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 520);
        dlg.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBackground(MainFrame.BG_COLOR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField idF = new JTextField(existing != null ? existing.getProductId() : "");
        JTextField nameF = new JTextField(existing != null ? existing.getName() : "");

        JComboBox<String> catCombo = new JComboBox<>();
        catCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        try {
            for (Category c : ServiceFactory.getCategoryService().listAll()) {
                catCombo.addItem(c.getCategoryId() + " - " + c.getCategoryName());
            }
        } catch (Exception ignored) {}
        if (existing != null) {
            try {
                for (Category c : ServiceFactory.getCategoryService().listAll()) {
                    if (c.getCategoryId().equals(existing.getCategoryId()))
                        catCombo.setSelectedItem(c.getCategoryId() + " - " + c.getCategoryName());
                }
            } catch (Exception ignored) {}
        }

        JTextField specF = new JTextField(existing != null ? existing.getSpec() : "");
        JTextField unitF = new JTextField(existing != null ? existing.getUnit() : "");

        SpinnerNumberModel costModel = new SpinnerNumberModel(
            existing != null ? existing.getCost() : 0.0, 0.0, 99999.0, 0.5);
        JSpinner costSp = new JSpinner(costModel);
        costSp.setFont(new Font("SansSerif", Font.PLAIN, 13));

        SpinnerNumberModel priceModel = new SpinnerNumberModel(
            existing != null ? existing.getPrice() : 0.0, 0.0, 99999.0, 0.5);
        JSpinner priceSp = new JSpinner(priceModel);
        priceSp.setFont(new Font("SansSerif", Font.PLAIN, 13));

        SpinnerNumberModel shelfModel = new SpinnerNumberModel(
            existing != null ? existing.getShelfLife() : 0, 0, 99999, 1);
        JSpinner shelfSp = new JSpinner(shelfModel);
        shelfSp.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTextField suppF = new JTextField(existing != null ? existing.getSupplierName() : "");
        JTextField dateF = new JTextField(existing != null ? existing.getRegDate() : "");

        idF.setEnabled(existing == null);

        addField(form, "商品编号:", idF);
        addField(form, "商品名称:", nameF);
        addField(form, "商品类别:", catCombo);
        addField(form, "规格:", specF);
        addField(form, "单位:", unitF);
        addField(form, "进价:", costSp);
        addField(form, "售价:", priceSp);
        addField(form, "保质期(天):", shelfSp);
        addField(form, "供应商:", suppF);
        addField(form, "登记日期:", dateF);

        dlg.add(form, BorderLayout.CENTER);

        JButton saveBtn = MainFrame.makeButton("保存", MainFrame.MAIN_COLOR);
        JRootPane root = dlg.getRootPane();
        root.setDefaultButton(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                Product p = existing != null ? existing : new Product();
                p.setProductId(idF.getText().trim());
                p.setName(nameF.getText().trim());
                String catItem = (String) catCombo.getSelectedItem();
                if (catItem != null) p.setCategoryId(catItem.split(" - ")[0]);
                p.setSpec(specF.getText().trim());
                p.setUnit(unitF.getText().trim());
                p.setCost(((Number) costSp.getValue()).doubleValue());
                p.setPrice(((Number) priceSp.getValue()).doubleValue());
                p.setShelfLife(((Number) shelfSp.getValue()).intValue());
                p.setSupplierName(suppF.getText().trim());
                p.setRegDate(dateF.getText().trim());
                if (existing == null) productService.add(p); else productService.update(p);
                dlg.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "保存失败: " + ex.getMessage());
            }
        });
        JPanel btnPanel = new JPanel();
        btnPanel.setBackground(MainFrame.BG_COLOR);
        btnPanel.add(saveBtn);
        dlg.add(btnPanel, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private static void addField(JPanel form, String label, JComponent field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        form.add(l);
        form.add(field);
    }

    private static void addField(JPanel form, String label, JTextField field) {
        addField(form, label, (JComponent) field);
    }
}
