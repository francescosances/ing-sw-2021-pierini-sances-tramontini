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
     * Returs the victory points provided by this card at the end of the game
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

    @Override
    public Object clone() throws CloneNotSupportedException {
       return super.clone();
    }
}
