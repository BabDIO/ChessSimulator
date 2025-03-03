package com.chess;

import com.chess.gui.EcranAccueil;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EcranAccueil accueil = new EcranAccueil();
            accueil.setVisible(true);
        });
    }
}
