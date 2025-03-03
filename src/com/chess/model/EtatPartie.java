package com.chess.model;

public class EtatPartie {
    private boolean estEnEchec;
    private boolean estEnEchecEtMat;
    private boolean estPat;
    private Piece.Couleur joueurEnEchec;
    
    public void verifierEtat(Echiquier echiquier, Piece.Couleur joueurActuel) {
        // Vérifier si le roi du joueur actuel est en échec
        Position positionRoi = trouverRoi(echiquier, joueurActuel);
        estEnEchec = estPositionAttaquee(echiquier, positionRoi, joueurActuel.opposite());
        
        if (estEnEchec) {
            joueurEnEchec = joueurActuel;
            // Vérifier si c'est un échec et mat
            estEnEchecEtMat = !peutEviterEchec(echiquier, joueurActuel);
        } else {
            // Vérifier si c'est pat (aucun coup légal possible mais pas en échec)
            estPat = !existeCoupLegal(echiquier, joueurActuel);
        }
    }
    
    private Position trouverRoi(Echiquier echiquier, Piece.Couleur couleur) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position pos = new Position(i, j);
                Piece piece = echiquier.getPiece(pos);
                if (piece instanceof Roi && piece.getCouleur() == couleur) {
                    return pos;
                }
            }
        }
        return null;
    }
    
    private boolean estPositionAttaquee(Echiquier echiquier, Position position, Piece.Couleur couleurAttaquant) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position posDepart = new Position(i, j);
                Piece piece = echiquier.getPiece(posDepart);
                if (piece != null && piece.getCouleur() == couleurAttaquant) {
                    if (piece.peutSeDeplacer(position, echiquier)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean peutEviterEchec(Echiquier echiquier, Piece.Couleur joueur) {
        // Essayer tous les coups possibles pour voir si l'un d'eux peut éviter l'échec
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position posDepart = new Position(i, j);
                Piece piece = echiquier.getPiece(posDepart);
                if (piece != null && piece.getCouleur() == joueur) {
                    for (int di = 0; di < 8; di++) {
                        for (int dj = 0; dj < 8; dj++) {
                            Position posArrivee = new Position(di, dj);
                            if (piece.peutSeDeplacer(posArrivee, echiquier)) {
                                // Simuler le coup
                                Piece piecePrise = echiquier.getPiece(posArrivee);
                                echiquier.deplacerPiece(posDepart, posArrivee);
                                
                                // Vérifier si le roi est toujours en échec
                                Position posRoi = trouverRoi(echiquier, joueur);
                                boolean toujoursEnEchec = estPositionAttaquee(echiquier, posRoi, joueur.opposite());
                                
                                // Annuler le coup
                                echiquier.setPiece(posDepart, piece);
                                echiquier.setPiece(posArrivee, piecePrise);
                                
                                if (!toujoursEnEchec) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    private boolean existeCoupLegal(Echiquier echiquier, Piece.Couleur joueur) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position posDepart = new Position(i, j);
                Piece piece = echiquier.getPiece(posDepart);
                if (piece != null && piece.getCouleur() == joueur) {
                    for (int di = 0; di < 8; di++) {
                        for (int dj = 0; dj < 8; dj++) {
                            Position posArrivee = new Position(di, dj);
                            if (piece.peutSeDeplacer(posArrivee, echiquier)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
    
    public boolean estEnEchec() { return estEnEchec; }
    public boolean estEnEchecEtMat() { return estEnEchecEtMat; }
    public boolean estPat() { return estPat; }
    public Piece.Couleur getJoueurEnEchec() { return joueurEnEchec; }
} 