package it.polimi.ingsw.Model;

public abstract class Card {

    private final int victoryPoints;

    public Card(int victoryPoints){
        this.victoryPoints = victoryPoints;
    }

    public int getVictoryPoints() {
        return victoryPoints;
    }
}
