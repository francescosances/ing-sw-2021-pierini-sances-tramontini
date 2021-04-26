package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;

public class WhiteMarbleLeaderCard extends LeaderCard {

    private final ResourceType outputResourceType;

    public WhiteMarbleLeaderCard (int victoryPoints, Requirements requirements, ResourceType outputResourceType){
        super(victoryPoints, requirements);
        this.outputResourceType = outputResourceType;
    }

    public WhiteMarbleLeaderCard (int victoryPoints, Requirements requirements, ResourceType outputResourceType, boolean active){
        super(victoryPoints, requirements, active);
        this.outputResourceType = outputResourceType;
    }

    @Override
    public boolean isWhiteMarble(){
        return true;
    }

    @Override
    public ResourceType getOutputResourceType() {
        return outputResourceType;
    }

    @Override
    public Resource convertResourceType(Resource resourceType){
        if(isActive() && resourceType == NonPhysicalResourceType.VOID)
            return outputResourceType;
        else
            return resourceType;
    }

    @Override
    public String toString() {
        return "WhiteMarbleLeaderCard: " +
                (active ? "active" : "inactive") +
                ", White marble to " + outputResourceType +
                ", requirements=" + requirements;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof WhiteMarbleLeaderCard))
            return false;
        WhiteMarbleLeaderCard that = (WhiteMarbleLeaderCard) other;
        return this.outputResourceType.equals(that.outputResourceType) && this.requirements.equals(that.requirements)
                && this.active == that.active && this.getVictoryPoints() == that.getVictoryPoints();

    }
}