package com.chess;

import com.chess.gui.EcranAccueil;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainGUI {
    public static void main(String[] args) {
        try {
            // Utiliser le look and feel du système
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                EcranAccueil accueil = new EcranAccueil();
                accueil.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
