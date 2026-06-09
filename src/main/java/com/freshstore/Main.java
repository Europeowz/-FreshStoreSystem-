package com.freshstore;

import com.freshstore.util.DatabaseInitializer;
import javax.swing.SwingUtilities;

public final class Main {
    private Main() {}

    public static void main(String[] args) throws Exception {
        DatabaseInitializer.init();
        SwingUtilities.invokeLater(() ->
            new com.freshstore.ui.LoginFrame().setVisible(true));
    }
}
