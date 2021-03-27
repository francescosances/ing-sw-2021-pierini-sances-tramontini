package it.polimi.ingsw.model;

public class WhiteMarbleLeaderCard extends LeaderCard {

    private final ResourceType outputResourceType;

    public WhiteMarbleLeaderCard (int victoryPoints, Requirements requirements, ResourceType outputResourceType){
        super(victoryPoints, requirements);
        this.outputResourceType = outputResourceType;
    }

    @Override
    public boolean isWhiteMarble(){
        return true;
    }

    @Override
    public ResourceType getOutputResourceType() {
        return outputResourceType;
    }

}