package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.storage.ResourceType;

public abstract class LeaderCard extends Card {
    protected boolean active;
    protected final Requirements requirements;

    public LeaderCard(int victoryPoints, Requirements requirements){
        super(victoryPoints);
        this.active = false;
        this.requirements = requirements;
    }

    public boolean isActive(){
        return active;
    }

    public boolean isWhiteMarble(){
        return false;
    }

    public void activate (PlayerBoard player){
        if (requirements.satisfied(player))
            active = true;
    }

    public ResourceType getOutputResourceType() throws WrongLeaderCardException {
        throw new WrongLeaderCardException();
    }

    public Requirements recalculateRequirements(Requirements requirements){
        return (Requirements) requirements.clone();
    }

    @Override
    public String toString() {
        //TODO: fare tutti i to string delle carte leader
        return "LeaderCard{" +
                "active=" + active +
                ", requirements=" + requirements +
                '}';
    }
}
