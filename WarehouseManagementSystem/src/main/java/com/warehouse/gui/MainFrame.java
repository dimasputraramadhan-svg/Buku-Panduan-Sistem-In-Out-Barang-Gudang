package com.warehouse.gui;

import com.warehouse.model.User;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private User currentUser;
    
    // Panel-panel
    private DashboardPanel dashboardPanel;
    private StockInPanel stockInPanel;
    private StockOutPanel stockOutPanel;
    private ItemManagementPanel itemPanel;
    private CategoryManagementPanel categoryPanel;
    private SupplierManagementPanel supplierPanel;
    private StockReportPanel reportPanel;
    
    public MainFrame(User user) {
        this.currentUser = user;
        initializeFrame();
        createMenuBar();
        createContentArea();
    }
    
    private void initializeFrame() {
        setTitle("Sistem In Out Barang Gudang - " + currentUser.getFullName());
        setSize(1400, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Dashboard
        JMenu menuDashboard = new JMenu("Dashboard");
        JMenuItem itemDashboard = new JMenuItem("Beranda");
        itemDashboard.addActionListener(e -> showDashboard());
        menuDashboard.add(itemDashboard);
        
        // Menu Transaksi
        JMenu menuTransaksi = new JMenu("Transaksi");
        JMenuItem itemStockIn = new JMenuItem("Barang Masuk");
        JMenuItem itemStockOut = new JMenuItem("Barang Keluar");
        
        itemStockIn.addActionListener(e -> showStockIn());
        itemStockOut.addActionListener(e -> showStockOut());
        
        menuTransaksi.add(itemStockIn);
        menuTransaksi.add(itemStockOut);
        
        // Menu Master Data
        JMenu menuMaster = new JMenu("Master Data");
        JMenuItem itemItems = new JMenuItem("Data Barang");
        JMenuItem itemCategories = new JMenuItem("Kategori Barang");
        JMenuItem itemSuppliers = new JMenuItem("Data Supplier");
        
        itemItems.addActionListener(e -> showItemManagement());
        itemCategories.addActionListener(e -> showCategoryManagement());
        itemSuppliers.addActionListener(e -> showSupplierManagement());
        
        menuMaster.add(itemItems);
        menuMaster.add(itemCategories);
        menuMaster.add(itemSuppliers);
        
        // Menu Laporan
        JMenu menuLaporan = new JMenu("Laporan");
        JMenuItem itemStockReport = new JMenuItem("Laporan Stok");
        JMenuItem itemTransactionReport = new JMenuItem("Laporan Transaksi");
        
        itemStockReport.addActionListener(e -> showStockReport());
        
        menuLaporan.add(itemStockReport);
        menuLaporan.add(itemTransactionReport);
        
        // Menu Sistem
        JMenu menuSistem = new JMenu("Sistem");
        JMenuItem itemLogout = new JMenuItem("Logout");
        JMenuItem itemExit = new JMenuItem("Keluar");
        
        itemLogout.addActionListener(e -> logout());
        itemExit.addActionListener(e -> System.exit(0));
        
        menuSistem.add(itemLogout);
        menuSistem.addSeparator();
        menuSistem.add(itemExit);
        
        // Tambahkan ke menu bar
        menuBar.add(menuDashboard);
        menuBar.add(menuTransaksi);
        menuBar.add(menuMaster);
        menuBar.add(menuLaporan);
        
        // Push menu sistem ke kanan
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(menuSistem);
        
        setJMenuBar(menuBar);
    }
    
    private void createContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Inisialisasi semua panel
        dashboardPanel = new DashboardPanel();
        stockInPanel = new StockInPanel(currentUser);
        stockOutPanel = new StockOutPanel(currentUser);
        itemPanel = new ItemManagementPanel();
        categoryPanel = new CategoryManagementPanel();
        supplierPanel = new SupplierManagementPanel();
        reportPanel = new StockReportPanel();
        
        // Tambahkan ke card layout
        contentPanel.add(dashboardPanel, "DASHBOARD");
        contentPanel.add(stockInPanel, "STOCK_IN");
        contentPanel.add(stockOutPanel, "STOCK_OUT");
        contentPanel.add(itemPanel, "ITEMS");
        contentPanel.add(categoryPanel, "CATEGORIES");
        contentPanel.add(supplierPanel, "SUPPLIERS");
        contentPanel.add(reportPanel, "REPORTS");
        
        add(contentPanel);
        
        // Tampilkan dashboard pertama
        showDashboard();
    }
    
    // Navigation methods
    public void showDashboard() {
        dashboardPanel.refreshData();
        cardLayout.show(contentPanel, "DASHBOARD");
    }
    
    public void showStockIn() {
        stockInPanel.resetForm();
        cardLayout.show(contentPanel, "STOCK_IN");
    }
    
    public void showStockOut() {
        stockOutPanel.resetForm();
        cardLayout.show(contentPanel, "STOCK_OUT");
    }
    
    public void showItemManagement() {
        itemPanel.refreshData();
        cardLayout.show(contentPanel, "ITEMS");
    }
    
    public void showCategoryManagement() {
        categoryPanel.refreshData();
        cardLayout.show(contentPanel, "CATEGORIES");
    }
    
    public void showSupplierManagement() {
        supplierPanel.refreshData();
        cardLayout.show(contentPanel, "SUPPLIERS");
    }
    
    public void showStockReport() {
        reportPanel.refreshData();
        cardLayout.show(contentPanel, "REPORTS");
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            // Kembali ke login atau langsung buat instance baru
            // new LoginDialog().setVisible(true);
            
            // Bypass untuk testing
            User dummyUser = new User();
            dummyUser.setId(1);
            dummyUser.setUsername("admin");
            dummyUser.setFullName("Administrator");
            dummyUser.setRole("admin");
            dummyUser.setActive(true);
            new MainFrame(dummyUser).setVisible(true);
        }
    }
}