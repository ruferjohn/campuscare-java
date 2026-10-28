
package campuscare;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import campuscare.ui.LoginFrame;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.err.println(
                    "Could not set system look and feel."
                );
            }

            new LoginFrame().setVisible(true);
        });
    }
}



