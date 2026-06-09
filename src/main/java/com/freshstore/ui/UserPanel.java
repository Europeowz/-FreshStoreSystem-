package com.freshstore.ui;

import com.freshstore.entity.User;
import com.freshstore.service.ServiceFactory;
import com.freshstore.service.UserService;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class UserPanel extends JPanel {
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField searchField;
    private final UserService userService = ServiceFactory.getUserService();
    private static final String[] ROLES = {"管理员", "采购员", "销售员"};

    public UserPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(MainFrame.BG_COLOR);

        searchField = new JTextField(15);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toolbar.add(new JLabel("搜索用户名:"));
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

        String[] cols = {"用户编号", "用户名", "角色"};
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
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottom.setBackground(MainFrame.BG_COLOR);

        JButton editBtn = MainFrame.makeButton("修改", new Color(0x15, 0x65, 0xC0));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            int modelRow = table.convertRowIndexToModel(row);
            User u = new User(
                (String) tableModel.getValueAt(modelRow, 0),
                (String) tableModel.getValueAt(modelRow, 1),
                "",
                (String) tableModel.getValueAt(modelRow, 2)
            );
            showDialog(u);
        });
        bottom.add(editBtn);

        JButton delBtn = MainFrame.makeButton("删除", new Color(0xC6, 0x28, 0x28));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            int modelRow = table.convertRowIndexToModel(row);
            String id = (String) tableModel.getValueAt(modelRow, 0);
            if (JOptionPane.showConfirmDialog(this, "确定删除用户 [" + id + "] 吗？",
                    "确认", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                try { userService.delete(id); loadData(); }
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
            for (User u : userService.listAll())
                tableModel.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getRole()});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载失败: " + e.getMessage());
        }
    }

    private void search() {
        String kw = searchField.getText().trim().toLowerCase();
        if (kw.isEmpty()) { loadData(); return; }
        tableModel.setRowCount(0);
        try {
            for (User u : userService.listAll()) {
                if (u.getUsername().toLowerCase().contains(kw))
                    tableModel.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getRole()});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "查询失败: " + e.getMessage());
        }
    }

    private void exportCSV() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("users.csv"));
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

    private void showDialog(User existing) {
        boolean isNew = existing == null;
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            isNew ? "添加用户" : "修改用户", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(350, 250);
        dlg.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
        form.setBackground(MainFrame.BG_COLOR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField idF = new JTextField(isNew ? "" : existing.getUserId());
        idF.setEnabled(isNew);
        JTextField nameF = new JTextField(isNew ? "" : existing.getUsername());
        JPasswordField passField = new JPasswordField();

        JComboBox<String> roleCombo = new JComboBox<>(ROLES);
        roleCombo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        if (existing != null) {
            for (int i = 0; i < ROLES.length; i++) {
                if (ROLES[i].equals(existing.getRole())) { roleCombo.setSelectedIndex(i); break; }
            }
        }

        form.add(new JLabel("用户编号:")); form.add(idF);
        form.add(new JLabel("用户名:")); form.add(nameF);
        form.add(new JLabel("密码:")); form.add(passField);
        form.add(new JLabel("角色:")); form.add(roleCombo);
        dlg.add(form, BorderLayout.CENTER);

        JButton saveBtn = MainFrame.makeButton("保存", MainFrame.MAIN_COLOR);
        dlg.getRootPane().setDefaultButton(saveBtn);
        saveBtn.addActionListener(e -> {
            try {
                User u = new User(idF.getText().trim(), nameF.getText().trim(),
                        new String(passField.getPassword()).trim(),
                        (String) roleCombo.getSelectedItem());
                if (isNew) userService.add(u); else userService.update(u);
                dlg.dispose(); loadData();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "保存失败: " + ex.getMessage());
            }
        });
        JPanel bp = new JPanel(); bp.setBackground(MainFrame.BG_COLOR); bp.add(saveBtn);
        dlg.add(bp, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
}
