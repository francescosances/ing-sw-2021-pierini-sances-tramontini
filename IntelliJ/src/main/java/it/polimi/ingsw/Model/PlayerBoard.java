package it.polimi.ingsw.Model;

public class PlayerBoard {

    private Match match;
    protected String username;
    protected FaithTrack faithTrack;

    protected Warehouse warehouse;
    protected Strongbox strongbox;

    private DevelopmentCardSlot[] developmentCardSlots;

    public PlayerBoard(String username,Match match){
        this.match = match;
        this.username = username;
        warehouse = new Warehouse();
        strongbox = new Strongbox();
        faithTrack = new FaithTrack(match);
        developmentCardSlots = new DevelopmentCardSlot[3];
    }

    public void takeResourcesFromMarket(int rowOrColumn){
        Resource[] resources;
        if(rowOrColumn > Market.ROWS){
            rowOrColumn -= Market.ROWS;
            resources = match.getMarket().chooseColumn(rowOrColumn);
        }else
            resources = match.getMarket().chooseRow(rowOrColumn);
        warehouse.toBeStored(resources);
    }

    public void buyDevelopmentCard(DevelopmentCard developmentCard){
        if(!developmentCard.getCost().satisfied(this)){
            throw new IllegalArgumentException("Card not purchasable");
        }
        Requirements requirements = developmentCard.getCost();
        requirements = warehouse.removeResources(requirements);
        strongbox.removeResources(requirements);
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

    public Requirements getAllResources(){
        return Requirements.sum(strongbox.getAllResources(),warehouse.getAllResources());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerBoard that = (PlayerBoard) o;
        return username.equals(that.username);
    }
}
