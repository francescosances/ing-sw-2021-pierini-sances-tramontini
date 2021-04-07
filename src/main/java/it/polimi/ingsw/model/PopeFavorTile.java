package it.polimi.ingsw.model;

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
        if(uncovered)
            return victoryPoints;
        else return 0;
    }
}
