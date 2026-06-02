package ui;

import dao.InventoryDAO;
import entity.Inventory;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** 库存管理面板 */
public class InventoryPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private InventoryDAO dao = new InventoryDAO();

    public InventoryPanel() {
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

        JLabel hint = new JLabel("红色行 = 库存低于下限，需要补货");
        hint.setFont(new Font("SansSerif", Font.ITALIC, 12));
        hint.setForeground(Color.RED);
        toolbar.add(hint);
        add(toolbar, BorderLayout.NORTH);

        String[] cols = {"商品编号", "商品名称", "库存数量", "上限", "下限", "位置", "最近入库", "操作员"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel) {
            // 库存不足的行标红
            public java.awt.Component prepareRenderer(
                    javax.swing.table.TableCellRenderer renderer, int row, int col) {
                java.awt.Component c = super.prepareRenderer(renderer, row, col);
                try {
                    int stock = Integer.parseInt(getValueAt(row, 2).toString());
                    int min   = Integer.parseInt(getValueAt(row, 4).toString());
                    if (stock < min) {
                        c.setBackground(new Color(0xFF, 0xEB, 0xEE)); // 浅红
                        c.setForeground(new Color(0xC6, 0x28, 0x28));
                    } else {
                        c.setBackground(Color.WHITE);
                        c.setForeground(Color.BLACK);
                    }
                } catch (Exception ignored) {}
                return c;
            }
        };
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
            String id = (String) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "确定删除库存记录 [" + id + "] 吗？",
                    "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try { dao.delete(id); loadData(); }
                catch (Exception ex) { JOptionPane.showMessageDialog(this, "删除失败: " + ex.getMessage()); }
            }
        });
        bottom.add(delBtn);
        add(bottom, BorderLayout.SOUTH);

        loadData();
    }

    void loadData() {
        tableModel.setRowCount(0);
        try {
            for (Inventory i : dao.listAll()) {
                tableModel.addRow(new Object[]{
                    i.getProductId(), i.getProductName(), i.getStock(),
                    i.getMaxStock(), i.getMinStock(), i.getLocation(),
                    i.getLastIn(), i.getUserId()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载失败: " + e.getMessage());
        }
    }

    private void editSelected() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
        Inventory inv = new Inventory();
        inv.setProductId((String) tableModel.getValueAt(row, 0));
        inv.setStock(Integer.parseInt(tableModel.getValueAt(row, 2).toString()));
        inv.setMaxStock(Integer.parseInt(tableModel.getValueAt(row, 3).toString()));
        inv.setMinStock(Integer.parseInt(tableModel.getValueAt(row, 4).toString()));
        inv.setLocation((String) tableModel.getValueAt(row, 5));
        inv.setLastIn((String) tableModel.getValueAt(row, 6));
        inv.setUserId((String) tableModel.getValueAt(row, 7));
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
        JTextField stockF = new JTextField(isNew ? "" : String.valueOf(existing.getStock()));
        JTextField maxF = new JTextField(isNew ? "" : String.valueOf(existing.getMaxStock()));
        JTextField minF = new JTextField(isNew ? "" : String.valueOf(existing.getMinStock()));
        JTextField locF = new JTextField(isNew ? "" : existing.getLocation());
        JTextField lastF = new JTextField(isNew ? "" : existing.getLastIn());
        JTextField uidF = new JTextField(isNew ? "" : existing.getUserId());

        addF(form, "商品编号:", pidF);
        addF(form, "库存数量:", stockF);
        addF(form, "库存上限:", maxF);
        addF(form, "库存下限:", minF);
        addF(form, "存放位置:", locF);
        addF(form, "最近入库:", lastF);
        addF(form, "操作员:", uidF);
        dlg.add(form, BorderLayout.CENTER);

        JButton saveBtn = MainFrame.makeButton("保存", MainFrame.MAIN_COLOR);
        saveBtn.addActionListener(e -> {
            try {
                Inventory i = new Inventory();
                i.setProductId(pidF.getText().trim());
                i.setStock(Integer.parseInt(stockF.getText().trim()));
                i.setMaxStock(Integer.parseInt(maxF.getText().trim()));
                i.setMinStock(Integer.parseInt(minF.getText().trim()));
                i.setLocation(locF.getText().trim());
                i.setLastIn(lastF.getText().trim());
                i.setUserId(uidF.getText().trim());
                if (isNew) dao.add(i); else dao.update(i);
                dlg.dispose(); loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "保存失败: " + ex.getMessage());
            }
        });
        JPanel bp = new JPanel(); bp.setBackground(MainFrame.BG_COLOR); bp.add(saveBtn);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void addF(JPanel f, String label, JTextField tf) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tf.setFont(new Font("SansSerif", Font.PLAIN, 13));
        f.add(l); f.add(tf);
    }
}
