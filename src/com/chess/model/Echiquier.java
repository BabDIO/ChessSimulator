package com.chess.model;

public class Echiquier {
    private Piece[][] cases;
    
    public Echiquier() {
        cases = new Piece[8][8];
        initialiser();
    }
    
    // Initialise l'échiquier avec les pièces
    private void initialiser() {
        // Placer les pions
        for(int i = 0; i < 8; i++) {
            cases[1][i] = new Pion(Piece.Couleur.NOIR, new Position(1, i));
            cases[6][i] = new Pion(Piece.Couleur.BLANC, new Position(6, i));
        }
        // Placer les tours
        cases[0][0] = new Tour(Piece.Couleur.NOIR, new Position(0,0));
        cases[0][7] = new Tour(Piece.Couleur.NOIR, new Position(0,7));
        cases[7][0] = new Tour(Piece.Couleur.BLANC, new Position(7,0));
        cases[7][7] = new Tour(Piece.Couleur.BLANC, new Position(7,7));
        // Placer les cavaliers
        cases[0][1] = new Cavalier(Piece.Couleur.NOIR, new Position(0,1));
        cases[0][6] = new Cavalier(Piece.Couleur.NOIR, new Position(0,6));
        cases[7][1] = new Cavalier(Piece.Couleur.BLANC, new Position(7,1));
        cases[7][6] = new Cavalier(Piece.Couleur.BLANC, new Position(7,6));
        // Placer les fous
        cases[0][2] = new Fou(Piece.Couleur.NOIR, new Position(0, 2));
        cases[0][5] = new Fou(Piece.Couleur.NOIR, new Position(0, 5));
        cases[7][2] = new Fou(Piece.Couleur.BLANC, new Position(7, 2));
        cases[7][5] = new Fou(Piece.Couleur.BLANC, new Position(7, 5));
        // Placer les reines
        cases[0][3] = new Reine(Piece.Couleur.NOIR, new Position(0,3));
        cases[7][3] = new Reine(Piece.Couleur.BLANC, new Position(7,3));
        // Placer les rois
        cases[0][4] = new Roi(Piece.Couleur.NOIR, new Position(0,4));
        cases[7][4] = new Roi(Piece.Couleur.BLANC, new Position(7,4));
    }
    
    public Piece getPiece(Position position) {
        if(position.getLigne() < 0 || position.getLigne() >= 8 ||
           position.getColonne() < 0 || position.getColonne() >= 8)
            return null;
        return cases[position.getLigne()][position.getColonne()];
    }
    
    public void setPiece(Position position, Piece piece) {
        if (position != null) {
            cases[position.getLigne()][position.getColonne()] = piece;
            if (piece != null) {
                piece.setPosition(position);
            }
        }
    }
    
    // Méthode pour déplacer une pièce d'une case à une autre
    public boolean deplacerPiece(Position depart, Position arrivee) {
        Piece pieceADeplacer = getPiece(depart);
        if (pieceADeplacer == null) return false;
        
        // Vérifier si le déplacement est valide selon les règles de la pièce
        if (!pieceADeplacer.peutSeDeplacer(arrivee, this)) return false;
        
        // Vérifier si la case d'arrivée contient une pièce adverse
        Piece pieceCible = getPiece(arrivee);
        if (pieceCible != null) {
            // Ne pas permettre la capture d'une pièce de même couleur
            if (pieceCible.getCouleur() == pieceADeplacer.getCouleur()) {
                return false;
            }
        }
        
        // Effectuer le déplacement
        cases[depart.getLigne()][depart.getColonne()] = null;
        cases[arrivee.getLigne()][arrivee.getColonne()] = pieceADeplacer;
        pieceADeplacer.setPosition(arrivee);
        
        // Si c'est un pion, mettre à jour son état de premier coup
        if (pieceADeplacer instanceof Pion) {
            ((Pion) pieceADeplacer).setPremierCoup(false);
        }
        
        return true;
    }
    
    // Affichage en mode texte de l'échiquier
    public void afficher() {
        for (int i = 0; i < 8; i++) {
            System.out.print((8 - i) + " ");
            for (int j = 0; j < 8; j++) {
                Piece p = cases[i][j];
                if(p == null) {
                    System.out.print("- ");
                } else {
                    // On affiche par exemple "PB" pour Pion Blanc, "TN" pour Tour Noir, etc.
                    char c = p.getClass().getSimpleName().charAt(0);
                    String couleur = (p.getCouleur() == Piece.Couleur.BLANC) ? "B" : "N";
                    System.out.print(c + couleur + " ");
                }
            }
            System.out.println();
        }
        System.out.println("  a  b  c  d  e  f  g  h");
    }
    
    // Méthode utilitaire pour vérifier si une position est valide
    public boolean estPositionValide(Position position) {
        return position.getLigne() >= 0 && position.getLigne() < 8 &&
               position.getColonne() >= 0 && position.getColonne() < 8;
    }
    
    // Méthode pour vérifier si une position est menacée par une pièce adverse
    public boolean estPositionMenacee(Position position, Piece.Couleur couleurAttaquant) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Piece piece = cases[i][j];
                if (piece != null && piece.getCouleur() == couleurAttaquant) {
                    if (piece.peutSeDeplacer(position, this)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
