package it.polimi.ingsw.Model;

public abstract class LeaderCard extends Card {
    private boolean active;
    private final Requirements requirements;

    public LeaderCard(int victoryPoints, Requirements requirements){
        super(victoryPoints);
        this.active = false;
        this.requirements = requirements;
    }

    public boolean isActive(){
        return active;
    }

    public boolean isWhiteMarble(){
        return false;
    }

    public void activate (PlayerBoard player){
        if (requirements.satisfied(player))
            active = true;
    }

    public ResourceType getOutputResourceType() throws WrongLeaderCardException {
        throw new WrongLeaderCardException();
    }

    public Requirements recalculateRequirements(Requirements requirements) throws WrongLeaderCardException{
        throw new WrongLeaderCardException();
    }
}
