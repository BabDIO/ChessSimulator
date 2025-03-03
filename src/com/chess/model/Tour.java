package com.chess.model;

public class Tour extends Piece {
    
    public Tour(Couleur couleur, Position position) {
        super(couleur, position);
    }
    
    @Override
    public boolean peutSeDeplacer(Position nouvellePosition, Echiquier echiquier) {
        if (position.equals(nouvellePosition)) return false;
        
        int diffLigne = nouvellePosition.getLigne() - position.getLigne();
        int diffColonne = nouvellePosition.getColonne() - position.getColonne();
        
        // Vérifier si le mouvement est horizontal ou vertical
        if (diffLigne != 0 && diffColonne != 0) return false;
        
        // Vérifier le chemin
        int pasLigne = (diffLigne == 0) ? 0 : diffLigne / Math.abs(diffLigne);
        int pasColonne = (diffColonne == 0) ? 0 : diffColonne / Math.abs(diffColonne);
        
        Position pos = new Position(
            position.getLigne() + pasLigne,
            position.getColonne() + pasColonne
        );
        
        while (!pos.equals(nouvellePosition)) {
            if (echiquier.getPiece(pos) != null) return false;
            pos = new Position(pos.getLigne() + pasLigne, pos.getColonne() + pasColonne);
        }
        
        // Vérifier la case d'arrivée
        Piece pieceCible = echiquier.getPiece(nouvellePosition);
        return pieceCible == null || pieceCible.getCouleur() != this.couleur;
    }
}
