package com.chess.model;

public class Position {
    private int ligne;
    private int colonne;
    
    public Position(int ligne, int colonne) {
        this.ligne = ligne;
        this.colonne = colonne;
    }
    
    public int getLigne() { 
        return ligne; 
    }
    
    public int getColonne() { 
        return colonne; 
    }
    
    public void setLigne(int ligne) { 
        this.ligne = ligne; 
    }
    
    public void setColonne(int colonne) { 
        this.colonne = colonne; 
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Position)) return false;
        Position p = (Position) obj;
        return ligne == p.ligne && colonne == p.colonne;
    }
    
    @Override
    public int hashCode() {
        return 31 * ligne + colonne;
    }
    
    @Override
    public String toString() {
        return "(" + ligne + ", " + colonne + ")";
    }
}
