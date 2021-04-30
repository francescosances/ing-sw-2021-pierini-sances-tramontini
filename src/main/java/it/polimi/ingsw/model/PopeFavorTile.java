package it.polimi.ingsw.model;

public class PopeFavorTile {

    /**
     * Stores the status of the tile. Yet if it hasn't been uncovered yet, true elsewhere
     */
    private boolean uncovered = false;
    /**
     * the vicotry points associated with the tile
     */
    private final int victoryPoints;

    /**
     * Initializes a new PopeFavorTile object
     * @param victoryPoints the victory points associated with the tile
     */
    public PopeFavorTile(int victoryPoints){
        this.victoryPoints = victoryPoints;
    }

    /**
     * Sets the uncovered boolean to true
     */
    public void uncover(){
        uncovered = true;
    }

    /**
     * Returns the value of uncovered
     * @return the value of uncovered
     */
    public boolean isUncovered() {
        return uncovered;
    }

    /**
     * Returns the victory points associated with the tile, 0 if uncovered
     * @return the victory points associated with the tile, 0 if uncovered
     */
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

    /**
     * Returns the hash code of the object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
