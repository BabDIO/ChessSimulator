package com.chess.model;

public class Fou extends Piece {
    
    public Fou(Couleur couleur, Position position) {
        super(couleur, position);
    }
    
    @Override
    public boolean peutSeDeplacer(Position destination, Echiquier echiquier) {
        // Vérification de base (même position ou pièce de même couleur)
        Piece pieceDestination = echiquier.getPiece(destination);
        if (pieceDestination != null && pieceDestination.getCouleur() == this.getCouleur()) {
            return false;
        }

        int deltaX = destination.getLigne() - position.getLigne();
        int deltaY = destination.getColonne() - position.getColonne();

        // Vérifier si le mouvement est diagonal (|deltaX| = |deltaY|)
        if (Math.abs(deltaX) != Math.abs(deltaY)) {
            return false;
        }

        // Déterminer la direction du mouvement
        int dirX = Integer.compare(deltaX, 0);
        int dirY = Integer.compare(deltaY, 0);

        // Vérifier s'il y a des pièces sur le chemin
        int x = position.getLigne() + dirX;
        int y = position.getColonne() + dirY;

        while (x != destination.getLigne()) {
            if (echiquier.getPiece(new Position(x, y)) != null) {
                return false;
            }
            x += dirX;
            y += dirY;
        }

        return true;
    }
}
