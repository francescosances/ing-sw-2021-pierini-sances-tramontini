package it.polimi.ingsw.model.cards;

public abstract class Card {

    private int victoryPoints;

    Card(int victoryPoints){
        this.victoryPoints = victoryPoints;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }

    public void setVictoryPoints(int victoryPoints) {
        this.victoryPoints = victoryPoints;
    }
}
