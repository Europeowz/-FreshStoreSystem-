package ui;

import dao.CategoryDAO;
import dao.ProductDAO;
import entity.Category;
import entity.Product;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** 商品管理面板 */
public class ProductPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryCombo;
    private ProductDAO dao = new ProductDAO();
    private CategoryDAO catDAO = new CategoryDAO();

    public ProductPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(MainFrame.BG_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---- 顶部工具栏 ----
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(MainFrame.BG_COLOR);

        searchField = new JTextField(15);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toolbar.add(new JLabel("搜索:"));
        toolbar.add(searchField);

        JButton searchBtn = MainFrame.makeButton("🔍 搜索", MainFrame.MAIN_COLOR);
        searchBtn.addActionListener(e -> search());
        toolbar.add(searchBtn);

        toolbar.add(new JLabel("  类别:"));
        categoryCombo = new JComboBox<>();
        categoryCombo.addItem("全部");
        categoryCombo.setFont(new Font("SansSerif", Font.PLAIN, 14));
        try {
            for (Category c : catDAO.listAll())
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

        add(toolbar, BorderLayout.NORTH);

        // ---- 表格 ----
        String[] cols = {"编号", "名称", "类别", "规格", "单位", "进价", "售价", "保质期(天)", "供应商", "登记日期"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(MainFrame.MAIN_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(new Color(0xC8, 0xE6, 0xC9));
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        // ---- 底部操作栏 ----
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        bottom.setBackground(MainFrame.BG_COLOR);

        JButton editBtn = MainFrame.makeButton("修改", new Color(0x15, 0x65, 0xC0));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            String id = (String) tableModel.getValueAt(row, 0);
            try {
                for (Product p : dao.listAll()) {
                    if (p.getProductId().equals(id)) { showEditDialog(p); return; }
                }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "查询失败: " + ex.getMessage()); }
        });
        bottom.add(editBtn);

        JButton delBtn = MainFrame.makeButton("删除", new Color(0xC6, 0x28, 0x28));
        delBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "请先选中一行"); return; }
            String id = (String) tableModel.getValueAt(row, 0);
            String name = (String) tableModel.getValueAt(row, 1);
            int ok = JOptionPane.showConfirmDialog(this,
                "确定删除商品 [" + name + "] 吗？", "确认删除", JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
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
            for (Product p : dao.listAll()) {
                tableModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getCategoryName(),
                    p.getSpec(), p.getUnit(), p.getCost(), p.getPrice(),
                    p.getShelfLife(), p.getSupplierName(), p.getRegDate()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "加载数据失败: " + e.getMessage());
        }
    }

    private void search() {
        String kw = searchField.getText().trim();
        tableModel.setRowCount(0);
        try {
            List<Product> list = kw.isEmpty() ? dao.listAll() : dao.searchByName(kw);
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
            for (Product p : dao.listByCategory(catId)) addRow(p);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "查询失败: " + e.getMessage());
        }
    }

    private void addRow(Product p) {
        tableModel.addRow(new Object[]{
            p.getProductId(), p.getName(), p.getCategoryName(),
            p.getSpec(), p.getUnit(), p.getCost(), p.getPrice(),
            p.getShelfLife(), p.getSupplierName(), p.getRegDate()
        });
    }

    /** 添加/修改对话框 */
    private void showEditDialog(Product existing) {
        JDialog dlg = new JDialog(SwingUtilities.getWindowAncestor(this),
            existing == null ? "添加商品" : "修改商品", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(480, 500);
        dlg.setLocationRelativeTo(this);
        JPanel form = new JPanel(new GridLayout(0, 2, 8, 10));
        form.setBackground(MainFrame.BG_COLOR);
        form.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JTextField idF = new JTextField(existing != null ? existing.getProductId() : "");
        JTextField nameF = new JTextField(existing != null ? existing.getName() : "");
        JTextField catF = new JTextField(existing != null ? existing.getCategoryId() : "");
        JTextField specF = new JTextField(existing != null ? existing.getSpec() : "");
        JTextField unitF = new JTextField(existing != null ? existing.getUnit() : "");
        JTextField costF = new JTextField(existing != null ? String.valueOf(existing.getCost()) : "");
        JTextField priceF = new JTextField(existing != null ? String.valueOf(existing.getPrice()) : "");
        JTextField shelfF = new JTextField(existing != null ? String.valueOf(existing.getShelfLife()) : "");
        JTextField suppF = new JTextField(existing != null ? existing.getSupplierName() : "");
        JTextField dateF = new JTextField(existing != null ? existing.getRegDate() : "");

        idF.setEnabled(existing == null); // 新增时可编辑编号

        addField(form, "商品编号:", idF);
        addField(form, "商品名称:", nameF);
        addField(form, "类别编号:", catF);
        addField(form, "规格:", specF);
        addField(form, "单位:", unitF);
        addField(form, "进价:", costF);
        addField(form, "售价:", priceF);
        addField(form, "保质期(天):", shelfF);
        addField(form, "供应商:", suppF);
        addField(form, "登记日期:", dateF);

        dlg.add(form, BorderLayout.CENTER);

        JButton saveBtn = MainFrame.makeButton("保存", MainFrame.MAIN_COLOR);
        saveBtn.addActionListener(e -> {
            try {
                Product p = existing != null ? existing : new Product();
                p.setProductId(idF.getText().trim());
                p.setName(nameF.getText().trim());
                p.setCategoryId(catF.getText().trim());
                p.setSpec(specF.getText().trim());
                p.setUnit(unitF.getText().trim());
                p.setCost(Double.parseDouble(costF.getText().trim()));
                p.setPrice(Double.parseDouble(priceF.getText().trim()));
                p.setShelfLife(Integer.parseInt(shelfF.getText().trim()));
                p.setSupplierName(suppF.getText().trim());
                p.setRegDate(dateF.getText().trim());
                if (existing == null) dao.add(p); else dao.update(p);
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

    private void addField(JPanel form, String label, JTextField field) {
        JLabel l = new JLabel(label);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        form.add(l);
        form.add(field);
    }
}
