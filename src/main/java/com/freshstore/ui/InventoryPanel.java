package com.freshstore.ui;

import com.freshstore.entity.Inventory;
import com.freshstore.service.InventoryService;
import com.freshstore.service.ServiceFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class InventoryPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final InventoryService inventoryService = ServiceFactory.getInventoryService();

    public InventoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(MainFrame.BG_COLOR);

        searchField = new JTextField(15);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toolbar.add(new JLabel("搜索商品:"));
        toolbar.add(searchField);

        JButton searchBtn = MainFrame.makeButton("搜索", MainFrame.MAIN_COLOR);
        searchBtn.addActionListener(e -> search());
        toolbar.add(searchBtn);

        JButton addBtn = MainFrame.makeButton("+ 添加", MainFrame.ACCENT_COLOR);
        addBtn.addActionListener(e -> showDialog(null));
        toolbar.add(addBtn);

        JButton refreshBtn = MainFrame.makeButton("刷新", new Color(0x75, 0x75, 0x75));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        JButton exportBtn = MainFrame.makeButton("导出CSV", new Color(0x42, 0x42, 0x42));
        exportBtn.addActionListener(e -> exportCSV());
        toolbar.add(exportBtn);

        JLabel hint = new JLabel("  红色行 = 库存低于下限，需要补货");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hint.setForeground(Color.RED);
        toolbar.add(hint);
        add(toolbar, BorderLayout.NORTH);

        String[] cols = {"商品编号", "商品名称", "库存数量", "上限", "下限", "位置", "最近入库", "操作员"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel) {
            public Component prepareRenderer(
                    javax.swing.table.TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                try {
                    int modelRow = convertRowIndexToModel(row);
                    int stock = Integer.parseInt(getModel().getValueAt(modelRow, 2).toString());
                    int min   = Integer.parseInt(getModel().getValueAt(modelRow, 4).toString());
                    if (stock < min) {
                        c.setBackground(new Color(0xFF, 0xEB, 0xEE));
                        c.setForeground(new Color(0xC6, 0x28, 0x28));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                } catch (Exception ignored) {}
                return c;
            }
        };
        table.setAutoCreateRowSorter(true);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(MainFrame.MAIN_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottom.setBackground(MainFrame.BG_COLOR);

        JButton editBtn = MainFrame.makeButton("修改", new Color(0x15, 0x65, 0xC0));
        editBtn.addActionListener(e -> editSelected());
        bottom.add(editBtn);

        JButton delBtn = MainFrame.makeButton("删除", new Color(0xC6, 0x28, 0x28));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) tableModel.getValueAt(modelRow, 0);
            if (JOptionPane.showConfirmDialog(this, "确定删除库存记录 [" + id + "] 吗？",
                    "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try { inventoryService.delete(id); loadData(); }
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
            for (Inventory i : inventoryService.listAll()) addRow(i);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载失败: " + e.getMessage());
        }
    }

    private void search() {
        String kw = searchField.getText().trim().toLowerCase();
        if (kw.isEmpty()) { loadData(); return; }
        tableModel.setRowCount(0);
        try {
            for (Inventory i : inventoryService.listAll()) {
                if (i.getProductName() != null && i.getProductName().toLowerCase().contains(kw))
                    addRow(i);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "查询失败: " + e.getMessage());
        }
    }

    private void addRow(Inventory i) {
        tableModel.addRow(new Object[]{
            i.getProductId(), i.getProductName(), i.getStock(),
            i.getMaxStock(), i.getMinStock(), i.getLocation(),
            i.getLastIn(), i.getUserId()
        });
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("inventory.csv"));
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

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
        int modelRow = table.convertRowIndexToModel(row);
        Inventory inv = new Inventory();
        inv.setProductId((String) tableModel.getValueAt(modelRow, 0));
        inv.setStock(Integer.parseInt(tableModel.getValueAt(modelRow, 2).toString()));
        inv.setMaxStock(Integer.parseInt(tableModel.getValueAt(modelRow, 3).toString()));
        inv.setMinStock(Integer.parseInt(tableModel.getValueAt(modelRow, 4).toString()));
        inv.setLocation((String) tableModel.getValueAt(modelRow, 5));
        inv.setLastIn((String) tableModel.getValueAt(modelRow, 6));
        inv.setUserId((String) tableModel.getValueAt(modelRow, 7));
        showDialog(inv);
    }

    private void showDialog(Inventory existing) {
        boolean isNew = existing == null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            isNew ? "添加库存" : "修改库存", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(420, 350);
        dlg.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBackground(MainFrame.BG_COLOR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField pidF = new JTextField(isNew ? "" : existing.getProductId());
        pidF.setEnabled(isNew);

        SpinnerNumberModel stockModel = new SpinnerNumberModel(
            isNew ? 0 : existing.getStock(), 0, 99999, 1);
        JSpinner stockSp = new JSpinner(stockModel);
        stockSp.setFont(new Font("SansSerif", Font.PLAIN, 13));

        SpinnerNumberModel maxModel = new SpinnerNumberModel(
            isNew ? 0 : existing.getMaxStock(), 0, 99999, 1);
        JSpinner maxSp = new JSpinner(maxModel);
        maxSp.setFont(new Font("SansSerif", Font.PLAIN, 13));

        SpinnerNumberModel minModel = new SpinnerNumberModel(
            isNew ? 0 : existing.getMinStock(), 0, 99999, 1);
        JSpinner minSp = new JSpinner(minModel);
        minSp.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTextField locF = new JTextField(isNew ? "" : existing.getLocation());
        JTextField lastF = new JTextField(isNew ? "" : existing.getLastIn());
        JTextField uidF = new JTextField(isNew ? "" : existing.getUserId());

        addF(form, "商品编号:", pidF);
        addF(form, "库存数量:", stockSp);
        addF(form, "库存上限:", maxSp);
        addF(form, "库存下限:", minSp);
        addF(form, "存放位置:", locF);
        addF(form, "最近入库:", lastF);
        addF(form, "操作员:", uidF);
        dlg.add(form, BorderLayout.CENTER);

        JButton saveBtn = MainFrame.makeButton("保存", MainFrame.MAIN_COLOR);
        dlg.getRootPane().setDefaultButton(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                Inventory i = new Inventory();
                i.setProductId(pidF.getText().trim());
                i.setStock(((Number) stockSp.getValue()).intValue());
                i.setMaxStock(((Number) maxSp.getValue()).intValue());
                i.setMinStock(((Number) minSp.getValue()).intValue());
                i.setLocation(locF.getText().trim());
                i.setLastIn(lastF.getText().trim());
                i.setUserId(uidF.getText().trim());
                if (isNew) inventoryService.add(i); else inventoryService.update(i);
                dlg.dispose(); loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "保存失败: " + ex.getMessage());
            }
        });
        JPanel bp = new JPanel(); bp.setBackground(MainFrame.BG_COLOR); bp.add(saveBtn);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private static void addF(JPanel f, String label, JComponent comp) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comp.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.add(l); f.add(comp);
    }

    private static void addF(JPanel f, String label, JTextField tf) {
        addF(f, label, (JComponent) tf);
    }
}
