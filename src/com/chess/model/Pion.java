package com.chess.model;

public class Pion extends Piece {
    private boolean premierCoup = true;
    
    public Pion(Couleur couleur, Position position) {
        super(couleur, position);
    }
    
    @Override
    public boolean peutSeDeplacer(Position nouvellePosition, Echiquier echiquier) {
        int diffLigne = nouvellePosition.getLigne() - position.getLigne();
        int diffColonne = nouvellePosition.getColonne() - position.getColonne();
        
        // Direction du mouvement selon la couleur
        int direction = (couleur == Couleur.BLANC) ? -1 : 1;
        
        // Vérifier si c'est un déplacement en avant
        if (diffColonne == 0) {
            // Déplacement simple
            if (diffLigne == direction) {
                return echiquier.getPiece(nouvellePosition) == null;
            }
            // Premier coup : peut avancer de 2 cases
            if (premierCoup && diffLigne == 2 * direction) {
                Position intermediaire = new Position(
                    position.getLigne() + direction,
                    position.getColonne()
                );
                return echiquier.getPiece(nouvellePosition) == null && 
                       echiquier.getPiece(intermediaire) == null;
            }
        }
        // Capture en diagonale
        else if (Math.abs(diffColonne) == 1 && diffLigne == direction) {
            Piece pieceCible = echiquier.getPiece(nouvellePosition);
            return pieceCible != null && pieceCible.getCouleur() != this.couleur;
        }
        
        return false;
    }
    
    public void setPremierCoup(boolean premierCoup) {
        this.premierCoup = premierCoup;
    }
}
