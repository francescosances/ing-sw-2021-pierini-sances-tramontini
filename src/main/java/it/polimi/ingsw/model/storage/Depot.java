package it.polimi.ingsw.model.storage;

public interface Depot {
    /**
     * Returns the resource type of the depot.
     * @return the resource type of the depot or null in case of a depot that can accept all types of resources
     */
    public ResourceType getResourceType();

    /**
     * Returns the size of the depot.
     * @return the size of the depot
     */
    public int getSize();

    /**
     * Returns the amount of resources inside the depot.
     * @return the amount of resources inside the depot
     */
    public int getOccupied();

    /**
     * Adds a single resource to the depot of the specified type, that must be compatible with the resource type of the depot.
     * @param resource the type of the resource to be added to the depot
     */
    public void addResource(ResourceType resource);

    /**
     * Removes a single resource from the depot.
     */
    public void removeResource();
}
