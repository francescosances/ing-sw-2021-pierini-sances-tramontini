package it.polimi.ingsw.Model;

public class PopeFavorTile {

    private boolean uncovered = false;
    private int victoryPoints;

    public void uncover(){
        uncovered = true;
    }

    public boolean getStatus() {
        return uncovered;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }
}
