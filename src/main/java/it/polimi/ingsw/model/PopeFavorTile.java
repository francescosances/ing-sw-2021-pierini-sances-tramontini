package it.polimi.ingsw.model;

import java.util.Objects;

public class PopeFavorTile {

    private boolean uncovered = false;
    private final int victoryPoints;

    public PopeFavorTile(int victoryPoints){
        this.victoryPoints = victoryPoints;
    }
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

    @Override
    public String toString() {
        return "PopeFavorTile: " +
                (uncovered ? "uncovered" : "covered") +
                ", " + victoryPoints + " victory points";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopeFavorTile that = (PopeFavorTile) o;
        return uncovered == that.uncovered && victoryPoints == that.victoryPoints;
    }
}
