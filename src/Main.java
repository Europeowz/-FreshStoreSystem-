import ui.LoginFrame;
import util.DatabaseInit;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Init database...");
            DatabaseInit.init();
        } catch (Exception e) {
            System.err.println("DB INIT FAILED: " + e.getMessage());
            e.printStackTrace();
            // Show error in GUI dialog so you can see it
            javax.swing.JOptionPane.showMessageDialog(null,
                "Database init failed!\n\n"
                + e.toString() + "\n\n"
                + "Check console for details.",
                "Startup Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        javax.swing.SwingUtilities.invokeLater(() ->
            new LoginFrame().setVisible(true)
        );
    }
}
