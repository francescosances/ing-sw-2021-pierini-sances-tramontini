package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.model.cards.exceptions.WrongLeaderCardException;
import it.polimi.ingsw.model.storage.Resource;
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

    public LeaderCard(int victoryPoints, Requirements requirements, boolean active){
        super(victoryPoints);
        this.active = false;
        this.requirements = requirements;
        this.active = active;
    }

    public boolean isActive(){
        return active;
    }

    public boolean isWhiteMarble(){
        return false;
    }

    public void activate (PlayerBoard player) throws NotSatisfiedRequirementsException{
        if (requirements.satisfied(player))
            this.active = true;
        else
            throw new NotSatisfiedRequirementsException("You cannot activate this card");
    }

    public ResourceType getOutputResourceType() throws WrongLeaderCardException {
        throw new WrongLeaderCardException();
    }

    public Resource convertResourceType(Resource resourceType){
        return resourceType;
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
        return requirements.equals(that.requirements) && active == that.active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(requirements);
    }

}
