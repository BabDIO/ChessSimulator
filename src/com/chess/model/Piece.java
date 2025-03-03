package com.chess.model;

public abstract class Piece {
    public enum Couleur { 
        BLANC, NOIR;  // Notez le point-virgule ici
        
        // Méthode pour obtenir la couleur opposée
        public Couleur opposite() {
            return this == BLANC ? NOIR : BLANC;
        }
    }
    
    protected Position position;
    protected Couleur couleur;
    
    public Piece(Couleur couleur, Position position) {
        this.couleur = couleur;
        this.position = position;
    }
    
    public Couleur getCouleur() {
        return couleur;
    }
    
    public Position getPosition() {
        return position;
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }
    
    // La méthode abstraite qui sera implémentée selon le type de pièce
    public abstract boolean peutSeDeplacer(Position destination, Echiquier echiquier);
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " " + couleur + " " + position;
    }
}
