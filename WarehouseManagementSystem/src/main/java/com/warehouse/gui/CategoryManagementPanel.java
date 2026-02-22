package com.warehouse.gui;

import com.warehouse.dao.CategoryDAO;
import com.warehouse.model.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoryManagementPanel extends JPanel {
    private JTable tableCategories;
    private DefaultTableModel tableModel;
    
    public CategoryManagementPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 123, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        JLabel lblTitle = new JLabel("MANAJEMEN KATEGORI BARANG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Toolbar
        JPanel toolbarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("➕ Tambah Kategori");
        JButton btnEdit = new JButton("✏️ Edit");
        JButton btnDelete = new JButton("🗑️ Hapus");
        JButton btnRefresh = new JButton("🔄 Refresh");
        
        btnAdd.addActionListener(e -> showAddDialog());
        btnEdit.addActionListener(e -> showEditDialog());
        btnDelete.addActionListener(e -> deleteCategory());
        btnRefresh.addActionListener(e -> refreshData());
        
        toolbarPanel.add(btnAdd);
        toolbarPanel.add(btnEdit);
        toolbarPanel.add(btnDelete);
        toolbarPanel.add(btnRefresh);
        
        add(toolbarPanel, BorderLayout.PAGE_START);
        
        // Table
        String[] columns = {"ID", "Nama Kategori", "Deskripsi"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableCategories = new JTable(tableModel);
        tableCategories.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(tableCategories);
        add(scrollPane, BorderLayout.CENTER);
        
        refreshData();
    }
    
    public void refreshData() {
        try {
            CategoryDAO dao = new CategoryDAO();
            List<Category> categories = dao.findAll();
            
            tableModel.setRowCount(0);
            for (Category cat : categories) {
                Object[] row = {
                    cat.getId(),
                    cat.getName(),
                    cat.getDescription()
                };
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void showAddDialog() {
        CategoryDialog dialog = new CategoryDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            refreshData();
        }
    }
    
    private void showEditDialog() {
        int selectedRow = tableCategories.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang akan diedit!");
            return;
        }
        
        int catId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            CategoryDAO dao = new CategoryDAO();
            Category cat = dao.findById(catId);
            
            if (cat != null) {
                CategoryDialog dialog = new CategoryDialog((JFrame) SwingUtilities.getWindowAncestor(this), cat);
                dialog.setVisible(true);
                if (dialog.isSaved()) {
                    refreshData();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void deleteCategory() {
        int selectedRow = tableCategories.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Pilih kategori yang akan dihapus!");
            return;
        }
        
        int catId = (int) tableModel.getValueAt(selectedRow, 0);
        String catName = (String) tableModel.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin hapus kategori '" + catName + "'?\nBarang dengan kategori ini akan kehilangan kategorinya.",
            "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                CategoryDAO dao = new CategoryDAO();
                if (dao.delete(catId)) {
                    JOptionPane.showMessageDialog(this, "Kategori berhasil dihapus!");
                    refreshData();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private class CategoryDialog extends JDialog {
        private JTextField txtName;
        private JTextArea txtDescription;
        private boolean saved = false;
        private Category category;
        
        public CategoryDialog(JFrame parent, Category category) {
            super(parent, category == null ? "Tambah Kategori" : "Edit Kategori", true);
            this.category = category;
            
            setSize(400, 300);
            setLocationRelativeTo(parent);
            
            initComponents();
            
            if (category != null) {
                txtName.setText(category.getName());
                txtDescription.setText(category.getDescription());
            }
        }
        
        private void initComponents() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            panel.add(new JLabel("Nama Kategori:*"), gbc);
            
            txtName = new JTextField(20);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            panel.add(txtName, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
            panel.add(new JLabel("Deskripsi:"), gbc);
            
            txtDescription = new JTextArea(5, 20);
            txtDescription.setLineWrap(true);
            JScrollPane scroll = new JScrollPane(txtDescription);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
            panel.add(scroll, gbc);
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnSave = new JButton("Simpan");
            JButton btnCancel = new JButton("Batal");
            
            btnSave.setBackground(new Color(40, 167, 69));
            btnSave.setForeground(Color.WHITE);
            
            btnSave.addActionListener(e -> saveCategory());
            btnCancel.addActionListener(e -> dispose());
            
            buttonPanel.add(btnCancel);
            buttonPanel.add(btnSave);
            
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.CENTER; gbc.weighty = 0;
            panel.add(buttonPanel, gbc);
            
            add(panel);
            getRootPane().setDefaultButton(btnSave);
        }
        
        private void saveCategory() {
            String name = txtName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nama kategori wajib diisi!");
                return;
            }
            
            try {
                CategoryDAO dao = new CategoryDAO();
                Category saveCat = (category != null) ? category : new Category();
                saveCat.setName(name);
                saveCat.setDescription(txtDescription.getText().trim());
                
                if (dao.save(saveCat)) {
                    saved = true;
                    JOptionPane.showMessageDialog(this, "Kategori berhasil disimpan!");
                    dispose();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
        
        public boolean isSaved() {
            return saved;
        }
    }
}