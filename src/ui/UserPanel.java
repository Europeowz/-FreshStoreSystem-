package ui;

import dao.UserDAO;
import entity.User;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** 用户管理面板（仅管理员可访问） */
public class UserPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private UserDAO dao = new UserDAO();

    public UserPanel() {
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

        add(toolbar, BorderLayout.NORTH);

        String[] cols = {"用户编号", "用户名", "角色"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
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
            User u = new User(
                (String) tableModel.getValueAt(row, 0),
                (String) tableModel.getValueAt(row, 1),
                "", // 不显示密码哈希
                (String) tableModel.getValueAt(row, 2)
            );
            showDialog(u);
        });
        bottom.add(editBtn);

        JButton delBtn = MainFrame.makeButton("删除", new Color(0xC6, 0x28, 0x28));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            String id = (String) tableModel.getValueAt(row, 0);
            if (JOptionPane.showConfirmDialog(this, "确定删除用户 [" + id + "] 吗？",
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
            for (User u : dao.listAll())
                tableModel.addRow(new Object[]{u.getUserId(), u.getUsername(), u.getRole()});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载失败: " + e.getMessage());
        }
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
        JTextField roleF = new JTextField(isNew ? "" : existing.getRole());

        form.add(new JLabel("用户编号:")); form.add(idF);
        form.add(new JLabel("用户名:")); form.add(nameF);
        form.add(new JLabel("密码:")); form.add(passField);
        form.add(new JLabel("角色:")); form.add(roleF);
        dlg.add(form, BorderLayout.CENTER);

        JButton saveBtn = MainFrame.makeButton("保存", MainFrame.MAIN_COLOR);
        saveBtn.addActionListener(e -> {
            try {
                User u = new User(idF.getText().trim(), nameF.getText().trim(),
                        new String(passField.getPassword()).trim(), roleF.getText().trim());
                if (isNew) dao.add(u); else dao.update(u);
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
