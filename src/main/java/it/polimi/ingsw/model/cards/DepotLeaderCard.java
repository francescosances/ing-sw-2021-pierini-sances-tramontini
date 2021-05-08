package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.model.storage.Depot;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;

/**
 * Leader card that extends the standard depot capacity
 */
public class DepotLeaderCard extends LeaderCard implements Depot {

    /**
     * The type of resource that can be stored in this depot
     */
    private final ResourceType resourceType;
    /**
     * The number of resources stored in this depot
     */
    private int occupied;
    /**
     * The maximum number of resources that can be stored in this depot
     */
    private final int SIZE = 2;

    /**
     * Initializes a new DepotLeaderCard
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player must satisfy so as to play the card
     * @param resourceType the resource the card can store
     */
    public DepotLeaderCard(String cardName,int victoryPoints, Requirements requirements, ResourceType resourceType) {
        super(cardName,victoryPoints, requirements);
        this.resourceType = resourceType;
        occupied = 0;
    }

    /**
     * Initializes a new DepotLeaderCard. Should be called by Serializer class only.
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player must satisfy so as to play the card
     * @param resourceType the resource the card can store
     * @param active manually sets the status of the card
     */
    public DepotLeaderCard(String cardName,int victoryPoints, Requirements requirements, ResourceType resourceType, boolean active) {
        super(cardName,victoryPoints, requirements, active);
        this.resourceType = resourceType;
        occupied = 0;
    }

    /**
     * Returns the type of resources that can be stored in this depot
     *
     * @return the type of resources that can be stored in this depot
     */
    @Override
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Returns the maximum number of resources that can be stored in this depot
     *
     * @return the maximum number of resources that can be stored in this depot
     */
    @Override
    public int getSize() {
        return SIZE;
    }

    /**
     * Returns the number of resources that are currently stored in this depot
     *
     * @return the number of resources that are currently stored in this depot
     */
    @Override
    public int getOccupied() {
        return occupied;
    }

    /**
     * Add the specified resource to the depot
     *
     * @param res The type of resource to be stored
     * @throws IncompatibleDepotException the resource is not compatible with this depot
     */
    @Override
    public void addResource(ResourceType res) throws IncompatibleDepotException {
        if (this.resourceType != res)
            throw new IncompatibleDepotException("Resource Type not compatible with depot");
        occupied++;
    }

    /**
     * Remove a resource from this depot
     *
     * @throws IllegalStateException the depot is empty
     */
    @Override
    public void removeResource() {
        if (this.occupied == 0)
            throw new IllegalStateException("Empty depot");
        occupied--;
    }


    /**
     * Activates the card if the requirements are satisfied
     * @param player the player that wants to activate the card
     * @throws NotSatisfiedRequirementsException if the player doesn't satisfy the requirements
     */
    @Override
    public void activate(PlayerBoard player) throws NotSatisfiedRequirementsException {
        super.activate(player);
        player.getWarehouse().addDepotLeaderCard(this);
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "DepotLeaderCard: " +
                (active ? "active" : "inactive") +
                ", " + occupied + "/" + SIZE + " " + resourceType + " occupied" +
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
        if (!(other instanceof DepotLeaderCard))
            return false;
        DepotLeaderCard that = (DepotLeaderCard) other;
        return this.occupied == that.occupied && this.resourceType.equals(that.resourceType) && this.requirements.equals(that.requirements)
                && this.active == that.active && this.getVictoryPoints() == that.getVictoryPoints();

    }

    /**
     * Return the hash code of the object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}