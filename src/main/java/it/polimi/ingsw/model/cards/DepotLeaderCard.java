package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.model.storage.Depot;
import it.polimi.ingsw.model.storage.ResourceType;

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

    public DepotLeaderCard(int victoryPoints, Requirements requirements, ResourceType resourceType) {
        super(victoryPoints, requirements);
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
     * @throws IllegalArgumentException the resource is not compatible with this depot
     */
    @Override
    public void addResource(ResourceType res) {
        if (this.resourceType != res)
            throw new IllegalArgumentException("Resource Type not compatible with depot");
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

    @Override
    public String toString() {
        return "DepotLeaderCard{" +
                "resourceType=" + resourceType +
                ", occupied=" + occupied +
                ", SIZE=" + SIZE +
                ", active=" + active +
                ", requirements=" + requirements +
                '}';
    }

    @Override
    public void activate(PlayerBoard player) throws NotSatisfiedRequirementsException {
        super.activate(player);
        player.getWarehouse().addDepotLeaderCard(this);
    }

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
}