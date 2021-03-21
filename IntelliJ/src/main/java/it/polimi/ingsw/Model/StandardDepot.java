package it.polimi.ingsw.Model;

import java.util.Optional;

public class StandardDepot implements Depot{
    private Optional<ResourceType> resourceType;
    private int occupied;
    private int size;

    public StandardDepot(int size){
        resourceType = Optional.empty();
        this.size = size;
        this.occupied = 0;
    }


    @Override
    public ResourceType getResourceType() {
        return null;
    }

    @Override
    public int getSize() {
        return 0;
    }

    @Override
    public int getOccupied() {
        return 0;
    }

    @Override
    public void addResource() {
    }

    @Override
    public void removeResource() {
    }
}
