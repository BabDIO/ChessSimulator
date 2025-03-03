package com.chess.model;

public class Cavalier extends Piece {
    
    public Cavalier(Couleur couleur, Position position) {
        super(couleur, position);
    }
    
    @Override
    public boolean peutSeDeplacer(Position nouvellePosition, Echiquier echiquier) {
        int diffLigne = Math.abs(nouvellePosition.getLigne() - position.getLigne());
        int diffColonne = Math.abs(nouvellePosition.getColonne() - position.getColonne());
        
        // Le cavalier se déplace en "L" : 2 cases dans une direction et 1 dans l'autre
        boolean mouvementValide = (diffLigne == 2 && diffColonne == 1) || 
                                (diffLigne == 1 && diffColonne == 2);
        
        if (!mouvementValide) return false;
        
        // Vérifier la case d'arrivée
        Piece pieceCible = echiquier.getPiece(nouvellePosition);
        return pieceCible == null || pieceCible.getCouleur() != this.couleur;
    }
}
