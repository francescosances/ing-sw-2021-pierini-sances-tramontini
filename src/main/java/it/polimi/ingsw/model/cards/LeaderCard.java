package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.storage.ResourceType;

import java.util.Objects;

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

    public boolean activate (PlayerBoard player){
        if (requirements.satisfied(player)) {//TODO: satisfied da null pointer exception
            this.active = true;
            return true;
        }
        return false;
    }

    public ResourceType getOutputResourceType() throws WrongLeaderCardException {
        throw new WrongLeaderCardException();
    }

    public Requirements recalculateRequirements(Requirements requirements){
        return (Requirements) requirements.clone();
    }

    @Override
    public String toString() {
        return "LeaderCard{" +
                "active=" + active +
                ", requirements=" + requirements +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaderCard)) return false;
        LeaderCard that = (LeaderCard) o;
        return requirements.equals(that.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requirements);
    }
}
