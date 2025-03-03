package com.chess.model;

public class Roi extends Piece {
    
    public Roi(Couleur couleur, Position position) {
        super(couleur, position);
    }
    
    @Override
    public boolean peutSeDeplacer(Position nouvellePosition, Echiquier echiquier) {
        int diffLigne = Math.abs(nouvellePosition.getLigne() - position.getLigne());
        int diffColonne = Math.abs(nouvellePosition.getColonne() - position.getColonne());
        
        // Le roi se déplace d'une seule case dans n'importe quelle direction
        if (diffLigne > 1 || diffColonne > 1) return false;
        
        // Vérifier la case d'arrivée
        Piece pieceCible = echiquier.getPiece(nouvellePosition);
        if (pieceCible != null && pieceCible.getCouleur() == this.couleur) return false;
        
        // Vérifier si le roi ne se met pas en échec
        return !estPositionMenacee(nouvellePosition, echiquier);
    }
    
    private boolean estPositionMenacee(Position position, Echiquier echiquier) {
        // Vérifier si une pièce adverse peut atteindre cette position
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Position pos = new Position(i, j);
                Piece piece = echiquier.getPiece(pos);
                if (piece != null && piece.getCouleur() != this.couleur) {
                    if (piece.peutSeDeplacer(position, echiquier)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
