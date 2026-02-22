package com.warehouse;  // ← BUKAN com.warehouse.gui

import com.warehouse.gui.LoginDialog;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {  // ← public class Main (bukan MainFrame)
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            LoginDialog login = new LoginDialog();
            login.setVisible(true);
        });
    }
}