package com.chess.ai;

import com.chess.model.*;
import java.util.*;

public class ChessAI {
    private final int niveauDifficulte;
    private final Random random = new Random();
    
    // Valeurs des pièces
    private static final int VALEUR_PION = 100;
    private static final int VALEUR_CAVALIER = 320;
    private static final int VALEUR_FOU = 330;
    private static final int VALEUR_TOUR = 500;
    private static final int VALEUR_REINE = 900;
    private static final int VALEUR_ROI = 20000;
    
    // Bonus/Malus positionnels
    private static final int BONUS_CENTRE = 30;
    private static final int BONUS_PROTECTION = 20;
    private static final int MALUS_PIECE_ATTAQUEE = -25;

    public ChessAI(int niveauDifficulte) {
        this.niveauDifficulte = niveauDifficulte;
    }

    public Position[] calculerMeilleurCoup(Echiquier echiquier, Piece.Couleur couleurAI) {
        List<Position[]> coupsValides = genererTousLesCoupsValides(echiquier, couleurAI);
        if (coupsValides.isEmpty()) return null;

        // Pour le niveau facile, ajouter de l'aléatoire
        if (niveauDifficulte == 0 && random.nextDouble() < 0.3) {
            return coupsValides.get(random.nextInt(coupsValides.size()));
        }

        Position[] meilleurCoup = null;
        int meilleurScore = Integer.MIN_VALUE;
        int profondeur = switch(niveauDifficulte) {
            case 0 -> 2;  // Facile
            case 1 -> 3;  // Moyen
            case 2 -> 4;  // Difficile
            default -> 3;
        };

        for (Position[] coup : coupsValides) {
            // Simuler le coup
            Piece pieceDeplacee = echiquier.getPiece(coup[0]);
            Piece piecePrise = echiquier.getPiece(coup[1]);
            echiquier.deplacerPiece(coup[0], coup[1]);
            
            // Calculer le score pour ce coup
            int score = -negamax(echiquier, profondeur - 1, -Integer.MAX_VALUE, -meilleurScore, 
                               couleurAI.opposite());
            
            // Annuler le coup
            echiquier.setPiece(coup[0], pieceDeplacee);
            echiquier.setPiece(coup[1], piecePrise);
            
            // Mettre à jour le meilleur coup
            if (score > meilleurScore) {
                meilleurScore = score;
                meilleurCoup = coup;
            }
        }
        
        return meilleurCoup;
    }
    
    private int negamax(Echiquier echiquier, int profondeur, int alpha, int beta, Piece.Couleur couleur) {
        if (profondeur == 0) {
            return evaluerPosition(echiquier, couleur);
        }
        
        List<Position[]> coups = genererTousLesCoupsValides(echiquier, couleur);
        if (coups.isEmpty()) {
            // Vérifier si le roi est en échec
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    Piece piece = echiquier.getPiece(new Position(i, j));
                    if (piece instanceof Roi && piece.getCouleur() == couleur) {
                        // Si le roi est attaqué et pas de coups possibles = échec et mat
                        if (estAttaquee(piece, echiquier)) {
                            return -20000;
                        }
                        return 0; // Pat
                    }
                }
            }
        }
        
        int meilleurScore = Integer.MIN_VALUE;
        
        for (Position[] coup : coups) {
            Piece pieceDeplacee = echiquier.getPiece(coup[0]);
            Piece piecePrise = echiquier.getPiece(coup[1]);
            echiquier.deplacerPiece(coup[0], coup[1]);
            
            int score = -negamax(echiquier, profondeur - 1, -beta, -alpha, couleur.opposite());
            
            echiquier.setPiece(coup[0], pieceDeplacee);
            echiquier.setPiece(coup[1], piecePrise);
            
            meilleurScore = Math.max(meilleurScore, score);
            alpha = Math.max(alpha, score);
            
            if (alpha >= beta) {
                break;
            }
        }
        
        return meilleurScore;
    }
    
    private int evaluerPosition(Echiquier echiquier, Piece.Couleur couleur) {
        int score = 0;
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position pos = new Position(i, j);
                Piece piece = echiquier.getPiece(pos);
                
                if (piece != null) {
                    int valeur = getValeurPiece(piece);
                    if (piece.getCouleur() == couleur) {
                        score += valeur;
                        // Bonus pour le contrôle du centre
                        if (estAuCentre(pos)) {
                            score += BONUS_CENTRE;
                        }
                    } else {
                        score -= valeur;
                    }
                }
            }
        }
        
        return score;
    }
    
    private List<Position[]> genererTousLesCoupsValides(Echiquier echiquier, Piece.Couleur couleur) {
        List<Position[]> coups = new ArrayList<>();
        
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position depart = new Position(i, j);
                Piece piece = echiquier.getPiece(depart);
                
                if (piece != null && piece.getCouleur() == couleur) {
                    // Générer tous les coups possibles pour cette pièce
                    for (int di = 0; di < 8; di++) {
                        for (int dj = 0; dj < 8; dj++) {
                            Position arrivee = new Position(di, dj);
                            if (piece.peutSeDeplacer(arrivee, echiquier)) {
                                coups.add(new Position[]{depart, arrivee});
                            }
                        }
                    }
                }
            }
        }
        
        return coups;
    }
    
    // Méthodes utilitaires
    private int getValeurPiece(Piece piece) {
        return switch (piece.getClass().getSimpleName()) {
            case "Pion" -> VALEUR_PION;
            case "Cavalier" -> VALEUR_CAVALIER;
            case "Fou" -> VALEUR_FOU;
            case "Tour" -> VALEUR_TOUR;
            case "Reine" -> VALEUR_REINE;
            case "Roi" -> VALEUR_ROI;
            default -> 0;
        };
    }
    
    private boolean estAuCentre(Position pos) {
        int ligne = pos.getLigne();
        int colonne = pos.getColonne();
        return (ligne >= 2 && ligne <= 5 && colonne >= 2 && colonne <= 5);
    }
    
    private boolean estProtegee(Piece piece, Echiquier echiquier) {
        Position pos = piece.getPosition();
        Piece.Couleur couleurPiece = piece.getCouleur();
        
        // Parcourir toutes les pièces de la même couleur
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece autrePiece = echiquier.getPiece(new Position(i, j));
                if (autrePiece != null && autrePiece != piece && 
                    autrePiece.getCouleur() == couleurPiece) {
                    // Si une pièce peut se déplacer à cette position, elle protège la pièce
                    if (autrePiece.peutSeDeplacer(pos, echiquier)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean estAttaquee(Piece piece, Echiquier echiquier) {
        return echiquier.estPositionMenacee(piece.getPosition(), piece.getCouleur().opposite());
    }
} 