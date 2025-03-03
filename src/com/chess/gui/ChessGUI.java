package com.chess.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import com.chess.model.Echiquier;
import com.chess.model.Position;
import com.chess.model.Piece;
import javax.swing.UIManager;
import com.chess.ai.ChessAI;
import javax.sound.sampled.*;
import java.io.File;
import com.chess.model.EtatPartie;

public class ChessGUI extends JFrame {
    private Echiquier echiquier;
    private JButton[][] casesBoutons;
    private Position positionSelectionnee;
    private Piece.Couleur joueurActuel;
    private JLabel statusLabel;
    private boolean contreOrdinateur;
    private int niveauDifficulte;
    private ChessAI ai;
    private boolean partieEnPause;
    private Stack<Coup> historiqueCoup;
    private EtatPartie etatPartie;
    private static final Color CASE_CLAIRE = new Color(238, 216, 192);  // Beige clair
    private static final Color CASE_FONCEE = new Color(171, 122, 101);  // Marron
    private static final Color ACCENT_COLOR = new Color(171, 122, 101); // Ajout de cette constante
    private static final Color SURBRILLANCE_SELECTION = new Color(130, 151, 105, 200);  // Vert transparent
    private static final Color SURBRILLANCE_DESTINATION = new Color(170, 162, 58, 200);  // Jaune transparent
    private static final Color PIECE_BLANCHE = new Color(255, 255, 255);
    private static final Color PIECE_NOIRE = new Color(0, 0, 0);
    private static final Font PIECE_FONT = new Font("DejaVu Sans", Font.PLAIN, 52);
    private static final Color FOND_PANEL = new Color(49, 46, 43);  // Fond sombre
    private JLabel piecesBlanchesCapturees;
    private JLabel piecesNoiresCapturees;
    private int nbPiecesBlanchesCapturees = 0;
    private int nbPiecesNoiresCapturees = 0;
    
    public ChessGUI(boolean contreOrdinateur, int niveauDifficulte) {
        this.contreOrdinateur = contreOrdinateur;
        this.niveauDifficulte = niveauDifficulte;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        echiquier = new Echiquier();
        casesBoutons = new JButton[8][8];
        joueurActuel = Piece.Couleur.BLANC;
        positionSelectionnee = null;
        
        if (contreOrdinateur) {
            ai = new ChessAI(niveauDifficulte);
        }
        
        historiqueCoup = new Stack<>();
        partieEnPause = false;
        etatPartie = new EtatPartie();
        initGUI();
    }
    
    private void initGUI() {
        setTitle("AfoRA-Echec-Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(FOND_PANEL);
        
        // Panel de l'échiquier
        JPanel echiquierPanel = new JPanel(new GridLayout(8, 8));
        echiquierPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 3));
        
        // Configuration de la police pour les pièces
        Font pieceFont = new Font("DejaVu Sans", Font.PLAIN, 52);
        
        // Création des boutons de l'échiquier
        for(int i = 0; i < 8; i++) {
            for(int j = 0; j < 8; j++) {
                final int ligne = i;
                final int colonne = j;
                JButton bouton = new JButton();
                bouton.setFont(pieceFont);
                bouton.setPreferredSize(new Dimension(80, 80));
                styliserBouton(bouton, i, j);
                bouton.addActionListener(e -> gererClic(ligne, colonne));
                casesBoutons[i][j] = bouton;
                echiquierPanel.add(bouton);
            }
        }
        
        // Panel d'information et de contrôle
        JPanel infoPanel = new JPanel(new BorderLayout(5, 5));
        infoPanel.setBackground(FOND_PANEL);
        
        // Status panel avec style
        statusLabel = new JLabel("Tour des " + (joueurActuel == Piece.Couleur.BLANC ? "Blancs" : "Noirs"));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Panel des boutons de contrôle avec plus d'espace
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(FOND_PANEL);
        
        JButton pauseButton = creerBoutonControle("Pause");
        JButton annulerButton = creerBoutonControle("Annuler coup");
        JButton finirButton = creerBoutonControle("Terminer partie");
        
        // Ajouter les actions
        pauseButton.addActionListener(e -> togglePause());
        annulerButton.addActionListener(e -> annulerDernierCoup());
        finirButton.addActionListener(e -> confirmerFinPartie());
        
        // Ajouter des tooltips
        pauseButton.setToolTipText("Mettre la partie en pause");
        annulerButton.setToolTipText("Annuler le dernier coup joué");
        finirButton.setToolTipText("Terminer la partie en cours");
        
        controlPanel.add(pauseButton);
        controlPanel.add(annulerButton);
        controlPanel.add(finirButton);
        
        // Panel pour les pièces capturées
        JPanel capturesPanel = new JPanel(new GridLayout(2, 1));
        capturesPanel.setBackground(FOND_PANEL);
        
        piecesBlanchesCapturees = new JLabel("Pièces blanches capturées: 0");
        piecesNoiresCapturees = new JLabel("Pièces noires capturées: 0");
        piecesBlanchesCapturees.setForeground(Color.WHITE);
        piecesNoiresCapturees.setForeground(Color.WHITE);
        
        capturesPanel.add(piecesBlanchesCapturees);
        capturesPanel.add(piecesNoiresCapturees);
        
        // Assemblage des panels
        infoPanel.add(statusLabel, BorderLayout.NORTH);
        infoPanel.add(controlPanel, BorderLayout.CENTER);
        infoPanel.add(capturesPanel, BorderLayout.EAST);
        
        mainPanel.add(echiquierPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        actualiserEchiquier();
    }
    
    private JButton creerBoutonControle(String texte) {
        JButton bouton = new JButton(texte);
        bouton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        // Couleurs vives pour chaque bouton
        Color couleurBouton;
        Color couleurHover;
        Color couleurTexte = Color.WHITE;
        
        switch(texte) {
            case "Pause":
                couleurBouton = new Color(52, 152, 219);  // Bleu
                couleurHover = new Color(41, 128, 185);   // Bleu foncé
                break;
            case "Annuler coup":
                couleurBouton = new Color(231, 76, 60);   // Rouge
                couleurHover = new Color(192, 57, 43);    // Rouge foncé
                break;
            case "Terminer partie":
                couleurBouton = new Color(46, 204, 113);  // Vert
                couleurHover = new Color(39, 174, 96);    // Vert foncé
                break;
            default:
                couleurBouton = new Color(149, 165, 166); // Gris
                couleurHover = new Color(127, 140, 141);  // Gris foncé
        }
        
        bouton.setBackground(couleurBouton);
        bouton.setForeground(couleurTexte);
        bouton.setFocusPainted(false);
        bouton.setOpaque(true);
        bouton.setBorderPainted(true);
        bouton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(couleurHover, 2),
            BorderFactory.createEmptyBorder(8, 20, 8, 20)
        ));
        
        // Effet de survol amélioré
        bouton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                bouton.setBackground(couleurHover);
                bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                bouton.setBackground(couleurBouton);
                bouton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                bouton.setBackground(couleurHover.darker());
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                bouton.setBackground(couleurHover);
            }
        });
        
        return bouton;
    }
    
    private void styliserBouton(JButton bouton, int i, int j) {
        bouton.setBackground(((i + j) % 2 == 0) ? CASE_CLAIRE : CASE_FONCEE);
        bouton.setBorderPainted(false);
        bouton.setFocusPainted(false);
        bouton.setOpaque(true);
        bouton.setMargin(new Insets(0, 0, 0, 0));
        bouton.setContentAreaFilled(true);
        
        // Ajouter un effet de survol
        bouton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (bouton.getText() != null && !bouton.getText().isEmpty()) {
                    bouton.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
            }
            public void mouseExited(MouseEvent e) {
                bouton.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
    }
    
    private void gererClic(int ligne, int colonne) {
        if (partieEnPause) return;
        
        // Si c'est le tour de l'IA, ignorer les clics du joueur
        if (contreOrdinateur && joueurActuel == Piece.Couleur.NOIR) {
            return;
        }
        
        Position positionCliquee = new Position(ligne, colonne);
        Piece pieceCliquee = echiquier.getPiece(positionCliquee);
        
        if (positionSelectionnee == null) {
            // Premier clic : sélection d'une pièce
            if (pieceCliquee != null && pieceCliquee.getCouleur() == joueurActuel) {
                positionSelectionnee = positionCliquee;
                surlignerCasesValides(pieceCliquee);
            }
        } else {
            // Deuxième clic : déplacement ou nouvelle sélection
            Piece pieceADeplacer = echiquier.getPiece(positionSelectionnee);
            
            // Vérifier si on clique sur une autre pièce de même couleur
            if (pieceCliquee != null && pieceCliquee.getCouleur() == joueurActuel) {
                resetSurlignage();
                positionSelectionnee = positionCliquee;
                surlignerCasesValides(pieceCliquee);
                return;
            }
            
            // Vérifier si le déplacement est valide
            if (pieceADeplacer != null && pieceADeplacer.peutSeDeplacer(positionCliquee, echiquier)) {
                effectuerCoup(positionSelectionnee, positionCliquee);
                resetSurlignage();
                positionSelectionnee = null;
            } else {
                // Si le coup n'est pas valide, reset la sélection
                resetSurlignage();
                positionSelectionnee = null;
                // Optionnel : jouer un son d'erreur
                jouerSon("erreur");
            }
        }
        
        actualiserEchiquier();
    }
    
    private void surlignerCasesValides(Piece piece) {
        if (piece == null) return;
        
        // Mettre en surbrillance la pièce sélectionnée
        Position pos = piece.getPosition();
        if (pos != null) {
            casesBoutons[pos.getLigne()][pos.getColonne()]
                .setBackground(SURBRILLANCE_SELECTION);
            
            // Mettre en surbrillance les destinations possibles
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Position destination = new Position(i, j);
                    if (piece.peutSeDeplacer(destination, echiquier)) {
                        casesBoutons[i][j].setBackground(SURBRILLANCE_DESTINATION);
                    }
                }
            }
        }
    }
    
    private void resetSurlignage() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                // Remettre la couleur d'origine de la case (alternance claire/foncée)
                casesBoutons[i][j].setBackground(((i + j) % 2 == 0) ? CASE_CLAIRE : CASE_FONCEE);
            }
        }
    }
    
    private void actualiserEchiquier() {
        statusLabel.setText("Tour des " + (joueurActuel == Piece.Couleur.BLANC ? "Blancs" : "Noirs"));
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                JButton bouton = casesBoutons[i][j];
                // Remettre la couleur de base de la case
                bouton.setBackground(((i + j) % 2 == 0) ? CASE_CLAIRE : CASE_FONCEE);
                
                Piece piece = echiquier.getPiece(new Position(i, j));
                if (piece != null) {
                    String symbole = getSymbolePiece(piece);
                    bouton.setText(symbole);
                    bouton.setFont(PIECE_FONT);
                    bouton.setForeground(piece.getCouleur() == Piece.Couleur.BLANC ? PIECE_BLANCHE : PIECE_NOIRE);
                    
                    // Effet d'ombre pour les pièces blanches
                    if (piece.getCouleur() == Piece.Couleur.BLANC) {
                        bouton.setBorder(BorderFactory.createEmptyBorder(2, 2, 4, 4));
                    }
                } else {
                    bouton.setText("");
                    bouton.setBorder(null);
                }
            }
        }
    }
    
    private String getSymbolePiece(Piece piece) {
        String nomClasse = piece.getClass().getSimpleName();
        boolean estBlanc = piece.getCouleur() == Piece.Couleur.BLANC;
        
        return switch (nomClasse) {
            case "Pion" -> estBlanc ? "♙" : "♟";
            case "Tour" -> estBlanc ? "♖" : "♜";
            case "Cavalier" -> estBlanc ? "♘" : "♞";
            case "Fou" -> estBlanc ? "♗" : "♝";
            case "Reine" -> estBlanc ? "♕" : "♛";
            case "Roi" -> estBlanc ? "♔" : "♚";
            default -> "?";
        };
    }
    
    private void togglePause() {
        partieEnPause = !partieEnPause;
        if (partieEnPause) {
            JOptionPane.showMessageDialog(this, "Partie en pause", "Pause", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void annulerDernierCoup() {
        if (!historiqueCoup.isEmpty()) {
            Coup dernierCoup = historiqueCoup.pop();
            echiquier.setPiece(dernierCoup.depart, dernierCoup.piece);
            echiquier.setPiece(dernierCoup.arrivee, dernierCoup.piecePrise);
            joueurActuel = (joueurActuel == Piece.Couleur.BLANC) ? 
                          Piece.Couleur.NOIR : Piece.Couleur.BLANC;
            actualiserEchiquier();
        }
    }
    
    private void confirmerFinPartie() {
        int reponse = JOptionPane.showConfirmDialog(this,
            "Voulez-vous vraiment terminer la partie ?",
            "Confirmation",
            JOptionPane.YES_NO_OPTION);
            
        if (reponse == JOptionPane.YES_OPTION) {
            dispose();
            new EcranAccueil().setVisible(true);
        }
    }
    
    private void effectuerCoup(Position depart, Position arrivee) {
        Piece pieceADeplacer = echiquier.getPiece(depart);
        Piece piecePrise = echiquier.getPiece(arrivee);
        
        if (echiquier.deplacerPiece(depart, arrivee)) {
            // Jouer le son approprié
            jouerSon(piecePrise != null ? "capture" : "deplacement");
            
            // Mettre à jour le compteur de captures
            mettreAJourCompteurCaptures(piecePrise);
            
            // Sauvegarder le coup dans l'historique
            historiqueCoup.push(new Coup(depart, arrivee, pieceADeplacer, piecePrise));
            
            // Vérifier l'état de la partie
            etatPartie.verifierEtat(echiquier, joueurActuel.opposite());
            
            if (etatPartie.estEnEchecEtMat()) {
                JOptionPane.showMessageDialog(this,
                    "Échec et mat ! " + joueurActuel + " gagne !",
                    "Fin de partie",
                    JOptionPane.INFORMATION_MESSAGE);
                confirmerFinPartie();
            } else if (etatPartie.estPat()) {
                JOptionPane.showMessageDialog(this,
                    "Pat ! La partie est nulle.",
                    "Fin de partie",
                    JOptionPane.INFORMATION_MESSAGE);
                confirmerFinPartie();
            } else {
                // Changer de joueur
                joueurActuel = joueurActuel.opposite();
                
                if (etatPartie.estEnEchec()) {
                    statusLabel.setText("Échec ! Tour des " +
                        (joueurActuel == Piece.Couleur.BLANC ? "Blancs" : "Noirs"));
                } else {
                    statusLabel.setText("Tour des " +
                        (joueurActuel == Piece.Couleur.BLANC ? "Blancs" : "Noirs"));
                }
                
                // Si c'est au tour de l'IA
                if (contreOrdinateur && joueurActuel == Piece.Couleur.NOIR) {
                    statusLabel.setText("L'ordinateur réfléchit...");
                    // Ajouter un délai pour que le joueur puisse voir son coup
                    Timer timer = new Timer(1000, e -> {
                        jouerCoupIA();
                        ((Timer)e.getSource()).stop();
                    });
                    timer.start();
                }
            }
        }
    }
    
    private void jouerCoupIA() {
        setEnabled(false); // Désactiver l'interface pendant que l'IA réfléchit
        
        SwingWorker<Position[], Void> worker = new SwingWorker<>() {
            @Override
            protected Position[] doInBackground() {
                return ai.calculerMeilleurCoup(echiquier, Piece.Couleur.NOIR);
            }
            
            @Override
            protected void done() {
                try {
                    Position[] coup = get();
                    if (coup != null && coup[0] != null && coup[1] != null) {
                        effectuerCoup(coup[0], coup[1]);
                    }
                    setEnabled(true);
                    actualiserEchiquier();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void jouerSon(String type) {
        try {
            String fichierSon = switch(type) {
                case "deplacement" -> "/resources/move.wav";
                case "capture" -> "/resources/capture.wav";
                case "erreur" -> "/resources/error.wav";
                default -> null;
            };
            
            if (fichierSon != null) {
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(
                    getClass().getResource(fichierSon));
                Clip clip = AudioSystem.getClip();
                clip.open(audioIn);
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void mettreAJourCompteurCaptures(Piece piecePrise) {
        if (piecePrise != null) {
            if (piecePrise.getCouleur() == Piece.Couleur.BLANC) {
                nbPiecesBlanchesCapturees++;
                piecesBlanchesCapturees.setText("Pièces blanches capturées: " + nbPiecesBlanchesCapturees);
            } else {
                nbPiecesNoiresCapturees++;
                piecesNoiresCapturees.setText("Pièces noires capturées: " + nbPiecesNoiresCapturees);
            }
        }
    }
    
    private static class Coup {
        final Position depart, arrivee;
        final Piece piece, piecePrise;
        
        Coup(Position depart, Position arrivee, Piece piece, Piece piecePrise) {
            this.depart = depart;
            this.arrivee = arrivee;
            this.piece = piece;
            this.piecePrise = piecePrise;
        }
    }
}
