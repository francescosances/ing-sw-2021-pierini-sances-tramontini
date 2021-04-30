package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;

public class WhiteMarbleLeaderCard extends LeaderCard {

    /**
     * The ResourceType the card converts the White Marbles into
     */
    private final ResourceType outputResourceType;

    /**
     * Initializes a new WhiteMarbleLeaderCard object
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player has to satisfy so as to play the card
     * @param outputResourceType the ResourceType the card converts the White Marbles into
     */
    public WhiteMarbleLeaderCard (int victoryPoints, Requirements requirements, ResourceType outputResourceType){
        super(victoryPoints, requirements);
        this.outputResourceType = outputResourceType;
    }

    /**
     * Initializes a new WhiteMarbleLeaderCard object. Manually sets the active status
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player has to satisfy so as to play the card
     * @param outputResourceType the ResourceType the card converts the White Marbles into
     * @param active the status of the card
     */
    public WhiteMarbleLeaderCard (int victoryPoints, Requirements requirements, ResourceType outputResourceType, boolean active){
        super(victoryPoints, requirements, active);
        this.outputResourceType = outputResourceType;
    }

    /**
     * Overrides the isWhiteMarble method set in LeaderCard class
     * @return true
     */
    @Override
    public boolean isWhiteMarble(){
        return true;
    }

    /**
     * Returns the ResourceType the card converts the White Marbles into
     * @return the ResourceType the card converts the White Marbles into
     */
    @Override
    public ResourceType getOutputResourceType() {
        return outputResourceType;
    }

    /**
     * Converts NonPhysicalResourceType.VOID resources into the one set in output resource type.
     * Returns the parameter elsewhere
     * @param resourceType the resource to convert
     * @return the converted resource if convertable
     */
    @Override
    public Resource convertResourceType(Resource resourceType){
        if(isActive() && resourceType == NonPhysicalResourceType.VOID)
            return outputResourceType;
        else
            return resourceType;
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "WhiteMarbleLeaderCard: " +
                (active ? "active" : "inactive") +
                ", White marble to " + outputResourceType +
                ", requirements=" + requirements;
    }
    /**
     * Indicates whether some other object is equal to this one
     * @param other that is confronted
     * @return true if o is equal to the object, false elsewhere
     */
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