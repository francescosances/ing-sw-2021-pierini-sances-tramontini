package it.polimi.ingsw.model;

public class PopeFavorTile {

    private boolean uncovered = false;
    private int victoryPoints;

    public PopeFavorTile(int victoryPoints){
        this.victoryPoints = victoryPoints;
    }
    public void uncover(){
        uncovered = true;
    }

    public boolean isUncovered() {
        return uncovered;
    }

    public int getVictoryPoints() {
        if(uncovered)
            return victoryPoints;
        else return 0;
    }

    @Override
    public String toString() {
        return "PopeFavorTile{" +
                "uncovered=" + uncovered +
                ", victoryPoints=" + victoryPoints +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopeFavorTile that = (PopeFavorTile) o;
        return uncovered == that.uncovered && victoryPoints == that.victoryPoints;
    }
}
