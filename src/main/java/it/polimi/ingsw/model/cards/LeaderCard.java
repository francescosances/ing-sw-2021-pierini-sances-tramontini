package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.model.cards.exceptions.WrongLeaderCardException;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;

import java.util.Objects;

public abstract class LeaderCard extends Card {
    /**
     * Stores the status of the card
     */
    protected boolean active;
    /**
     * The requirements the player must satisfy so as to activate the card
     */
    protected final Requirements requirements;

    /**
     * Sets the attributes of the Object. Must be called from heirs only
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player must satisfy so as to play the card
     */
    public LeaderCard(String cardName,int victoryPoints, Requirements requirements){
        super(victoryPoints,cardName);
        this.active = false;
        this.requirements = requirements;
    }

    /**
     * Sets the attributes of the Object. Must be called from heirs only
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player must satisfy so as to play the card
     * @param active the status of the card
     */
    public LeaderCard(String cardName,int victoryPoints, Requirements requirements, boolean active){
        super(victoryPoints,cardName);
        this.active = false;
        this.requirements = requirements;
        this.active = active;
    }

    /**
     * Returns true if the card is active, false elsewhere
     * @return true if the card is active, false elsewhere
     */
    public boolean isActive(){
        return active;
    }

    /**
     * Returns true if the card is a WhiteMarbleLeaderCard, false elsewhere.
     * Is overwritten by WhiteMarbleLeaderCard class only
     * @return true if the card is a WhiteMarbleLeaderCard, false elsewhere.
     */
    public boolean isWhiteMarble(){
        return false;
    }

    /**
     * Turns active to true if the player satisfies the requirements of the card
     * @param player the player who wants to activate the card
     * @throws NotSatisfiedRequirementsException if card requirements aren't satisfied
     */
    public void activate (PlayerBoard player) throws NotSatisfiedRequirementsException{
        if (requirements.satisfied(player))
            this.active = true;
        else
            throw new NotSatisfiedRequirementsException("You cannot activate this card");
    }

    /**
     * Returns the output resource of the card conversion.
     * Is overwritten by DevelopmentLeaderCard only.
     * @return the ResourceType the DevelopmentLeaderCard converts into
     * @throws WrongLeaderCardException if the card isn't a DevelopmentLeaderCard
     */
    public ResourceType getOutputResourceType() throws WrongLeaderCardException {
        throw new WrongLeaderCardException();
    }

    /**
     * Converts a Void resource into a ResourceType.
     * Returns the same resource if the conversion isn't applicable or the Leader card isn't a WhiteMarbleLeaderCard
     * @param resourceType the resource to convert
     * @return teh converted resource
     */
    public Resource convertResourceType(Resource resourceType){
        return resourceType;
    }

    /**
     * Applies a discount to the card where applicable, otherwise returns the same Requirements
     * @param requirements the requirements on top of which the discount will be applied
     * @return the discounted Requirments
     */
    public Requirements recalculateRequirements(Requirements requirements){
        return requirements.clone();
    }

    /**
     * Returns true if the LeaderCard is a ProductionLeaderCard (in overriden method), false elsewhere
     * @return true if the LeaderCard is a ProductionLeaderCard, false elsewhere
     */
    public boolean isProductionLeaderCard(){
        return false;
    }

    /**
     * Returns true if the LeaderCard is a DiscountLeaderCard (in overriden method), false elsewhere
     * @return true if the LeaderCard is a DiscountLeaderCard, false elsewhere
     */
    public boolean isDiscountLeaderCard(){ return false; }

    /**
     * Returns true if the LeaderCard is a DepotLeaderCard (in overriden method), false elsewhere
     * @return true if the LeaderCard is a DepotLeaderCard, false elsewhere
     */
   public boolean isDepotLeaderCard(){
        return false;
   }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "LeaderCard: " +
                (active ? "active" : "inactive") +
                ", requirements = " + requirements;
    }

    /**
     * Indicates whether some other object is equal to this one
     * @param o that is confronted
     * @return true if o is equal to the object, false elsewhere
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LeaderCard)) return false;
        LeaderCard that = (LeaderCard) o;
        return requirements.equals(that.requirements) && active == that.active && super.getCardName().equals(that.getCardName());
    }

    /**
     * Returns the hash code of the object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return Objects.hash(requirements);
    }

}
