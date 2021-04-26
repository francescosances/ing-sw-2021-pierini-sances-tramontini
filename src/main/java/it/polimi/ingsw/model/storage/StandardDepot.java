package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;

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
     *
     * @param size the size of the new depot
     */
    public StandardDepot(int size) {
        resourceType = null;
        this.size = size;
        this.occupied = 0;
    }

    /**
     * Returns the ResourceType that can be stored in that depot, if any is stored
     * @return the ResourceType that can be stored in that depot, if any is stored
     */
    @Override
    public ResourceType getResourceType() {
        return resourceType;
    }

    /**
     * Returns the size of the depot
     * @return the size of the depot
     */
    @Override
    public int getSize() {
        return this.size;
    }

    /**
     * Returns how many resources are in the depot
     * @return how many resources are in teh depot
     */
    @Override
    public int getOccupied() {
        return this.occupied;
    }

    /**
     * Adds the resource res to the depot
     * @param res resource to be added to the depot
     * @throws IncompatibleDepotException if the depot is full or the resource res is not compatible with the depot
     */
    @Override
    public void addResource(ResourceType res) throws IncompatibleDepotException {
        if (res == null)
            throw new NullPointerException();
        if (occupied == size)
            throw new IncompatibleDepotException("Depot is full");
        if (occupied == 0)
            this.resourceType = res;
        else {
            if (this.resourceType == null)
                throw new IncompatibleDepotException("Depot occupied but Resource Type not defined");
            else if (this.resourceType != res)
                throw new IncompatibleDepotException("Resource Type not compatible with depot");
        }
        occupied++;
    }

    /**
     * Removes one resource from the depot
     */
    @Override
    public void removeResource() {
        if (this.occupied == 0)
            throw new IndexOutOfBoundsException();
        occupied--;
        if (occupied == 0)
            this.resourceType = null;
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "StandardDepot{" +
                "resourceType=" + resourceType +
                ", occupied=" + occupied +
                ", size=" + size +
                '}';
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StandardDepot that = (StandardDepot) o;
        return occupied == that.occupied && size == that.size && resourceType == that.resourceType;
    }

}
