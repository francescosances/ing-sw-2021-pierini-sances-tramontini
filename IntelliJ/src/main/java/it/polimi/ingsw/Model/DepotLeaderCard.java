package it.polimi.ingsw.Model;

public class DepotLeaderCard extends LeaderCard implements Depot {
    private ResourceType resourceType;
    private int occupied;
    private final int SIZE;

    private DepotLeaderCard(int victoryPoints, Requirements requirements, ResourceType resourceType){
        super(victoryPoints, requirements);
        this.resourceType = resourceType;
        SIZE = 2;
        occupied = 0;
    }

    public ResourceType getResourceType(){
        return resourceType;
    }

    public int getSize(){
        return SIZE;
    }

    public int getOccupied(){
        return occupied;
    }

    public void addResource(){
        occupied++;
    }

    public void removeResource(){
        occupied--;
    }

}