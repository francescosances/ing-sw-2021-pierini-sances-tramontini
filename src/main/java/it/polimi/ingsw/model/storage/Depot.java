package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;

public interface Depot {
    /**
     * Returns the resource type of the depot.
     * @return the resource type of the depot or null in case of a depot that can accept all types of resources
     */
     ResourceType getResourceType();

    /**
     * Returns the size of the depot.
     * @return the size of the depot
     */
     int getSize();

    /**
     * Returns the amount of resources inside the depot.
     * @return the amount of resources inside the depot
     */
     int getOccupied();

    /**
     * Adds a single resource to the depot of the specified type, that must be compatible with the resource type of the depot.
     * @param resource the type of the resource to be added to the depot
     */
     void addResource(ResourceType resource) throws IncompatibleDepotException;

    /**
     * Removes a single resource from the depot.
     */
    void removeResource();

}
