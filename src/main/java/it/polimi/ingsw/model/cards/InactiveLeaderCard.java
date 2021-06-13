package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;

public class InactiveLeaderCard extends LeaderCard{

    /**
     * Sets the attributes of the Object. Must be called from heirs only
     */
    public InactiveLeaderCard(){
        super("65", 0, new Requirements());
    }

    /**
     * Does nothing
     * @param player the player who wants to activate the card
     */
    @Override
    public void activate(PlayerBoard player){}

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Inactive LeaderCard";
    }
}
