package it.polimi.ingsw.model.cards;

public abstract class Card {

    /**
     * The victory points provided by this card at the end of the game
     */
    private int victoryPoints;

    Card(int victoryPoints){
        this.victoryPoints = victoryPoints;
    }

    /**
     * Returns the victory points provided by this card at the end of the game
     * @return the victory points provided by this card at the end of the game
     */
    public int getVictoryPoints() {
        return victoryPoints;
    }

    /**
     * Set the victory points provided by this card at the end of the game
     * @param victoryPoints the victory points to provide by this card at the end of the game
     */
    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }

    /**
     * Return a clone of the object
     * @return a clone of the object
     * @throws CloneNotSupportedException if the object isn't cloneable
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
       return super.clone();
    }
}
