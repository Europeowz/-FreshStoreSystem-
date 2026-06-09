package com.freshstore.ui;

import com.freshstore.entity.Sale;
import com.freshstore.service.SaleService;
import com.freshstore.service.ServiceFactory;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class SalePanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final SaleService saleService = ServiceFactory.getSaleService();

    public SalePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(MainFrame.BG_COLOR);

        searchField = new JTextField(15);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toolbar.add(new JLabel("商品名:"));
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

        add(toolbar, BorderLayout.NORTH);

        String[] cols = {"销售单号", "门店", "商品编号", "商品名称", "数量", "金额", "日期", "操作员"};
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
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottom.setBackground(MainFrame.BG_COLOR);

        JButton delBtn = MainFrame.makeButton("删除", new Color(0xC6, 0x28, 0x28));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) tableModel.getValueAt(modelRow, 0);
            if (JOptionPane.showConfirmDialog(this, "确定删除销售记录 [" + id + "] 吗？",
                    "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try { saleService.delete(id); loadData(); }
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
            for (Sale s : saleService.listAll()) addRow(s);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载失败: " + e.getMessage());
        }
    }

    private void search() {
        String kw = searchField.getText().trim();
        tableModel.setRowCount(0);
        try {
            for (Sale s : kw.isEmpty() ? saleService.listAll() : saleService.searchByProduct(kw))
                addRow(s);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "查询失败: " + e.getMessage());
        }
    }

    private void addRow(Sale s) {
        tableModel.addRow(new Object[]{
            s.getSaleId(), s.getStoreId(), s.getProductId(), s.getProductName(),
            s.getQuantity(), s.getAmount(), s.getSaleDate(), s.getUserId()
        });
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("sales.csv"));
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

    private void showDialog(Sale existing) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            "添加销售记录", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(420, 350);
        dlg.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBackground(MainFrame.BG_COLOR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField sidF = new JTextField();
        JTextField storeF = new JTextField();
        JTextField pidF = new JTextField();

        SpinnerNumberModel qtyModel = new SpinnerNumberModel(1, 1, 99999, 1);
        JSpinner qtySp = new JSpinner(qtyModel);
        qtySp.setFont(new Font("SansSerif", Font.PLAIN, 13));

        SpinnerNumberModel amtModel = new SpinnerNumberModel(0.0, 0.0, 999999.0, 0.5);
        JSpinner amtSp = new JSpinner(amtModel);
        amtSp.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JTextField dateF = new JTextField(
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        JTextField uidF = new JTextField();

        addF(form, "销售单号:", sidF);
        addF(form, "门店编号:", storeF);
        addF(form, "商品编号:", pidF);
        addF(form, "数量:", qtySp);
        addF(form, "金额:", amtSp);
        addF(form, "日期:", dateF);
        addF(form, "操作员:", uidF);
        dlg.add(form, BorderLayout.CENTER);

        JButton saveBtn = MainFrame.makeButton("保存", MainFrame.MAIN_COLOR);
        dlg.getRootPane().setDefaultButton(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                Sale s = new Sale();
                s.setSaleId(sidF.getText().trim());
                s.setStoreId(storeF.getText().trim());
                s.setProductId(pidF.getText().trim());
                s.setQuantity(((Number) qtySp.getValue()).intValue());
                s.setAmount(((Number) amtSp.getValue()).doubleValue());
                s.setSaleDate(dateF.getText().trim());
                s.setUserId(uidF.getText().trim());
                saleService.add(s);
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
