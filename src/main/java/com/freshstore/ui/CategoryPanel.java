package com.freshstore.ui;

import com.freshstore.entity.Category;
import com.freshstore.service.CategoryService;
import com.freshstore.service.ServiceFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class CategoryPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final CategoryService categoryService = ServiceFactory.getCategoryService();

    public CategoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(MainFrame.BG_COLOR);

        JButton addBtn = MainFrame.makeButton("+ 添加", MainFrame.ACCENT_COLOR);
        addBtn.addActionListener(e -> showDialog(null));
        toolbar.add(addBtn);

        JButton refreshBtn = MainFrame.makeButton("刷新", new Color(0x75, 0x75, 0x75));
        refreshBtn.addActionListener(e -> loadData());
        toolbar.add(refreshBtn);

        JButton exportBtn = MainFrame.makeButton("导出CSV", new Color(0x42, 0x42, 0x42));
        exportBtn.addActionListener(e -> exportCSV());
        toolbar.add(exportBtn);

        add(toolbar, BorderLayout.NORTH);

        String[] cols = {"类别编号", "类别名称"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(MainFrame.MAIN_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
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
            String name = (String) tableModel.getValueAt(modelRow, 1);
            showDialog(new Category(id, name));
        });
        bottom.add(editBtn);

        JButton delBtn = MainFrame.makeButton("删除", new Color(0xC6, 0x28, 0x28));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) tableModel.getValueAt(modelRow, 0);
            String name = (String) tableModel.getValueAt(modelRow, 1);
            if (JOptionPane.showConfirmDialog(this, "确定删除 [" + name + "] 吗？",
                    "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try { categoryService.delete(id); loadData(); }
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
            for (Category c : categoryService.listAll())
                tableModel.addRow(new Object[]{c.getCategoryId(), c.getCategoryName()});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载失败: " + e.getMessage());
        }
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("categories.csv"));
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

    private void showDialog(Category existing) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            existing == null ? "添加类别" : "修改类别", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(350, 180);
        dlg.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(2, 2, 10, 15));
        form.setBackground(MainFrame.BG_COLOR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField idF = new JTextField(existing != null ? existing.getCategoryId() : "");
        idF.setEnabled(existing == null);
        JTextField nameF = new JTextField(existing != null ? existing.getCategoryName() : "");

        form.add(new JLabel("类别编号:")); form.add(idF);
        form.add(new JLabel("类别名称:")); form.add(nameF);
        dlg.add(form, BorderLayout.CENTER);

        JButton saveBtn = MainFrame.makeButton("保存", MainFrame.MAIN_COLOR);
        dlg.getRootPane().setDefaultButton(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                Category c = new Category(idF.getText().trim(), nameF.getText().trim());
                if (existing == null) categoryService.add(c); else categoryService.update(c);
                dlg.dispose();
                loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "保存失败: " + ex.getMessage());
            }
        });
        JPanel bp = new JPanel(); bp.setBackground(MainFrame.BG_COLOR); bp.add(saveBtn);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
