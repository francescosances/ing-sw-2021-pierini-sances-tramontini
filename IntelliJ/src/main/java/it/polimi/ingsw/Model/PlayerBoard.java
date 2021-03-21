package it.polimi.ingsw.Model;


import java.util.Objects;

public class PlayerBoard {

    private Match match;
    private String username;
    private FaithTrack faithTrack;

    private DevelopmentCardSlot[] developmentCardSlots;

    public PlayerBoard(String username){
        this.username = username;
        developmentCardSlots = new DevelopmentCardSlot[3];
    }

    public void takeResourcesFromMarket(int rowOrColumn){
        Resource[] resources;
        if(rowOrColumn > Market.ROWS){
            rowOrColumn -= Market.ROWS;
            resources = match.getMarket().chooseColumn(rowOrColumn);
        }else
            resources = match.getMarket().chooseRow(rowOrColumn);
        //TODO: stoccare risorse
    }

    public void buyDevelopmentCard(DevelopmentCard developmentCard){
        if(!developmentCard.getCost().satisfied(this)){
            throw new IllegalArgumentException("Card not purchasable");
        }
        //TODO:scalare risorse per l'acquisto
    }

    public void gainFaithPoints(int points) throws EndGameException {
        for (int i=0;i<points;i++)
            faithTrack.moveMarker();
    }

    public int getVictoryPoints(){
        int res = faithTrack.getVictoryPoints();
        return res;
    }

    public Match getMatch() {
        return match;
    }

    public String getUsername() {
        return username;
    }

    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    public DevelopmentCardSlot[] getDevelopmentCardSlots(){
        return developmentCardSlots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerBoard that = (PlayerBoard) o;
        return username.equals(that.username);
    }
}
