package it.polimi.ingsw.model.storage;

import java.util.Objects;

public class StandardDepot implements Depot {
    /**
     * The type of the resource inside the depot or null in case of an empty depot.
     */
    private ResourceType resourceType;

    /**
     * The amount of resources inside the depot.
     */
    private int occupied;

    /**
     * The size of the depot.
     */
    private final int size;

    /**
     * Creates an empty depot.
     * @param size the size of the new depot
     */
    public StandardDepot(int size){
        resourceType = null;
        this.size = size;
        this.occupied = 0;
    }

    @Override
    public ResourceType getResourceType() {
        return resourceType;
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
            this.resourceType = res;
        else{
            if (this.resourceType == null)
                throw new IllegalStateException("Depot occupied but Resource Type not defined");
            else if (this.resourceType != res)
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
            this.resourceType = null;
    }

    @Override
    public String toString() {
        return "StandardDepot{" +
                "resourceType=" + resourceType +
                ", occupied=" + occupied +
                ", size=" + size +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardDepot that = (StandardDepot) o;
        return occupied == that.occupied && size == that.size && resourceType == that.resourceType;
    }

}
