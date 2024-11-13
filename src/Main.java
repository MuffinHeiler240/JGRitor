import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;
import java.awt.*;

public class Main{

    public static void main(String[] args) {
        try {
            System.setProperty("flatlaf.useWindowDecorations", "True");
            System.setProperty("flatlaf.menuBarEmbedded", "True");
            UIManager.put("TabbedPane.showTabSeparators", true);
            UIManager.put("TabbedPane.selectedBackground", Color.darkGray);
            FlatDarkLaf.setup();

            SwingUtilities.invokeLater(JGRitor::new);
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An error occurred while initializing the application. Please try again later.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
