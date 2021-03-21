package it.polimi.ingsw.Model;

import java.util.Optional;

public class StandardDepot implements Depot{
    private Optional<ResourceType> resourceType;
    private int occupied;
    private final int size;

    public StandardDepot(int size){
        resourceType = Optional.empty();
        this.size = size;
        this.occupied = 0;
    }


    @Override
    public ResourceType getResourceType() {
        return resourceType.orElse(null);
    }

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public int getOccupied() {
        return this.occupied;
    }

    @Override
    public void addResource(ResourceType res){
        if (occupied == 0)
            this.resourceType = Optional.of(res);
        else{
            if (!this.resourceType.isPresent())
                throw new IllegalStateException("Depot occupied but Resource Type not defined");
            else if (this.resourceType.get() != res)
                throw new IllegalArgumentException("Resource Type not compatible with depot");
        }
        occupied++;
    }

    @Override
    public void removeResource() {
        if (this.occupied == 0)
            throw new IndexOutOfBoundsException();
        occupied--;
        if (occupied == 0)
            this.resourceType = Optional.empty();
    }
}
