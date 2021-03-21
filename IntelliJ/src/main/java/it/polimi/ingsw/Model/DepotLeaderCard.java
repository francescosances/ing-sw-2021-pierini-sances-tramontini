package it.polimi.ingsw.Model;

public class DepotLeaderCard extends LeaderCard implements Depot {
    private final ResourceType resourceType;
    private int occupied;
    private final int SIZE;

    public DepotLeaderCard(int victoryPoints, Requirements requirements, ResourceType resourceType){
        super(victoryPoints, requirements);
        this.resourceType = resourceType;
        SIZE = 2;
        occupied = 0;
    }

    @Override
    public ResourceType getResourceType(){
        return resourceType;
    }

    @Override
    public int getSize(){
        return SIZE;
    }

    @Override
    public int getOccupied(){
        return occupied;
    }

    @Override
    public void addResource(ResourceType res){
        if (this.resourceType != res)
            throw new IllegalArgumentException("Resource Type not compatible with depot");
        occupied++;
    }

    @Override
    public void removeResource(){
        if (this.occupied == 0)
            throw new IndexOutOfBoundsException();
        occupied--;
    }

}