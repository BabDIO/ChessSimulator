package com.chess.gui;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.EmptyBorder;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;

public class EcranAccueil extends JFrame {
    private static final String TITRE = "AfoRA-Echec-Game";
    private JComboBox<String> modeJeuCombo;
    private JComboBox<String> niveauDifficulteCombo;
    private static final Color BACKGROUND_COLOR = new Color(49, 46, 43);
    private static final Color ACCENT_COLOR = new Color(171, 122, 101);
    private static final Color TEXT_COLOR = new Color(238, 216, 192);
    
    public EcranAccueil() {
        modeJeuCombo = new JComboBox<>(new String[]{"2 Joueurs", "Contre l'ordinateur"});
        niveauDifficulteCombo = new JComboBox<>(new String[]{"Facile", "Moyen", "Difficile"});
        
        setTitle(TITRE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setPreferredSize(new Dimension(800, 900));
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        initComposants();
        pack();
        setLocationRelativeTo(null);
    }
    
    private void initComposants() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        // En-tête avec logo et titre
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        headerPanel.setBackground(BACKGROUND_COLOR);
        
        // Images rondes
        JPanel imagesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        imagesPanel.setBackground(BACKGROUND_COLOR);
        
        try {
            // Charger les images depuis le classpath
            ClassLoader classLoader = getClass().getClassLoader();
            
            // Première image
            URL imageUrl1 = classLoader.getResource("resources/chess_knight.png.jpg");
            if (imageUrl1 != null) {
                ImageIcon icon1 = new ImageIcon(imageUrl1);
                JLabel imageLabel1 = createRoundImageLabel(icon1, 180);
                imagesPanel.add(imageLabel1);
            }
            
            // Deuxième image
            URL imageUrl2 = classLoader.getResource("resources/ccc.jpg");
            if (imageUrl2 != null) {
                ImageIcon icon2 = new ImageIcon(imageUrl2);
                JLabel imageLabel2 = createRoundImageLabel(icon2, 180);
                imagesPanel.add(imageLabel2);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des images: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Titre stylisé
        JLabel titreLabel = new JLabel("Bienvenue sur " + TITRE);
        titreLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titreLabel.setForeground(TEXT_COLOR);
        titreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panneau de configuration stylisé
        JPanel configPanel = new JPanel();
        configPanel.setLayout(new BoxLayout(configPanel, BoxLayout.Y_AXIS));
        configPanel.setBackground(BACKGROUND_COLOR);
        configPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            new EmptyBorder(20, 30, 20, 30)
        ));
        
        // Mode de jeu stylisé
        JPanel modePanel = createStyledComboPanel("Mode de jeu:", 
            new String[]{"2 Joueurs", "Contre l'ordinateur"}, modeJeuCombo);
        
        // Niveau de difficulté stylisé
        JPanel niveauPanel = createStyledComboPanel("Niveau:", 
            new String[]{"Facile", "Moyen", "Difficile"}, niveauDifficulteCombo);
        
        // Bouton Jouer stylisé
        JButton jouerButton = new JButton("JOUER");
        styleButton(jouerButton);
        jouerButton.addActionListener(e -> demarrerPartie());
        
        // Règles du jeu stylisées
        JTextArea reglesText = new JTextArea(
            "Règles du jeu :\n\n" +
            "1. Les Blancs commencent toujours\n\n" +
            "2. Chaque pièce a son propre mouvement :\n" +
            "   ♙ Pion : Avance d'une case (deux au premier coup)\n" +
            "   ♖ Tour : Se déplace horizontalement et verticalement\n" +
            "   ♘ Cavalier : Se déplace en 'L'\n" +
            "   ♗ Fou : Se déplace en diagonale\n" +
            "   ♕ Reine : Combine les mouvements de la tour et du fou\n" +
            "   ♔ Roi : Se déplace d'une case dans toutes les directions\n\n" +
            "3. Le but est de mettre le roi adverse en échec et mat"
        );
        styleTextArea(reglesText);
        
        // Assemblage des composants
        configPanel.add(modePanel);
        configPanel.add(Box.createVerticalStrut(15));
        configPanel.add(niveauPanel);
        configPanel.add(Box.createVerticalStrut(25));
        configPanel.add(jouerButton);
        
        mainPanel.add(imagesPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(titreLabel);
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(configPanel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(new JScrollPane(reglesText));
        
        add(mainPanel);
    }
    
    private JLabel createRoundImageLabel(ImageIcon icon, int size) {
        Image img = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        JLabel label = new JLabel(new ImageIcon(img));
        
        // Créer un bord rond avec un effet de relief
        label.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Ajouter un effet de survol
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR.brighter(), 3),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_COLOR, 3),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        });
        
        return label;
    }
    
    private JPanel createStyledComboPanel(String label, String[] items, JComboBox<String> combo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panel.setBackground(BACKGROUND_COLOR);
        
        JLabel lblTitle = new JLabel(label);
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        // Style de la ComboBox
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(200, 30));
        combo.setBackground(new Color(52, 152, 219)); // Bleu
        combo.setForeground(Color.WHITE);
        ((JComponent) combo.getRenderer()).setBackground(new Color(52, 152, 219));
        ((JComponent) combo.getRenderer()).setForeground(Color.WHITE);
        
        // Bordure personnalisée
        combo.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));
        
        panel.add(lblTitle);
        panel.add(combo);
        return panel;
    }
    
    private void styleButton(JButton button) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        // Couleurs vives pour le bouton
        Color bgColor = new Color(46, 204, 113);  // Vert vif
        Color hoverColor = new Color(39, 174, 96); // Vert plus foncé
        Color textColor = Color.WHITE;
        
        button.setForeground(textColor);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(hoverColor, 2),
            BorderFactory.createEmptyBorder(10, 30, 10, 30)
        ));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Effet de survol amélioré
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
            
            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor.darker());
            }
            
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
        });
    }
    
    private void styleTextArea(JTextArea textArea) {
        textArea.setEditable(false);
        textArea.setBackground(BACKGROUND_COLOR);
        textArea.setForeground(TEXT_COLOR);
        textArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ACCENT_COLOR, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
    }
    
    private void demarrerPartie() {
        try {
            String mode = (String) modeJeuCombo.getSelectedItem();
            String niveau = (String) niveauDifficulteCombo.getSelectedItem();
            
            boolean contreOrdinateur = mode.equals("Contre l'ordinateur");
            int niveauDifficulte = niveauDifficulteCombo.getSelectedIndex();
            
            // Fermer l'écran d'accueil
            this.dispose();
            
            // Lancer le jeu avec gestion d'erreurs
            SwingUtilities.invokeLater(() -> {
                try {
                    ChessGUI gui = new ChessGUI(contreOrdinateur, niveauDifficulte);
                    gui.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Erreur lors du lancement du jeu : " + e.getMessage(),
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
                    // Réafficher l'écran d'accueil en cas d'erreur
                    new EcranAccueil().setVisible(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'initialisation du jeu : " + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
} 