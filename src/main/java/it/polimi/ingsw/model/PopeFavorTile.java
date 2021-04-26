package it.polimi.ingsw.model;

public class PopeFavorTile {

    private boolean uncovered = false;
    private final int victoryPoints;

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

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "PopeFavorTile: " +
                (uncovered ? "uncovered" : "covered") +
                ", " + victoryPoints + " victory points";
    }


    /**
     * Indicates whether some other object is equal to this one
     * @param o that is confronted
     * @return true if o is equal to the object, false elsewhere
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PopeFavorTile that = (PopeFavorTile) o;
        return uncovered == that.uncovered && victoryPoints == that.victoryPoints;
    }
}
