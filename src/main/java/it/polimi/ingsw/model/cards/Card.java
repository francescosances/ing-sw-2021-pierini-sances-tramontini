package it.polimi.ingsw.model.cards;

public abstract class Card {

    /**
     * The victory points provided by this card at the end of the game
     */
    private int victoryPoints;

    /**
     * The name associated to the card
     */
    private String cardName;

    Card(int victoryPoints,String cardName){
        this.victoryPoints = victoryPoints;
        this.cardName = cardName;
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
     * Returns the name associated to the card
     * @return the name associated to the card
     */
    public String getCardName() {
        return cardName;
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
