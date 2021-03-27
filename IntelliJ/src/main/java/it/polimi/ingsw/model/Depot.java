package it.polimi.ingsw.model;

public interface Depot {
    public ResourceType getResourceType();
    public int getSize();
    public int getOccupied();
    public void addResource(ResourceType resource);
    public void removeResource();
}
