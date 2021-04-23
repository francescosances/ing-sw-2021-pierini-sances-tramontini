package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Strongbox;
import it.polimi.ingsw.model.storage.Warehouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerBoard {

    private transient final Match match;
    protected String username;
    protected FaithTrack faithTrack;

    protected Warehouse warehouse;
    protected Strongbox strongbox;

    private final DevelopmentCardSlot[] developmentCardSlots;
    private final List<LeaderCard> leaderCards;

    public PlayerBoard(String username,Match match){
        this.match = match;
        this.username = username;
        warehouse = new Warehouse();
        strongbox = new Strongbox();
        faithTrack = new FaithTrack(match);
        developmentCardSlots = Stream.generate(DevelopmentCardSlot::new).limit(3).toArray(DevelopmentCardSlot[]::new);
        leaderCards = new ArrayList<>();
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
        return faithTrack.getVictoryPoints();
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
        return Requirements.sum(strongbox.getAllResources(), warehouse.getAllResources());
    }

    public void addLeaderCard(LeaderCard card){
        this.leaderCards.add(card);
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public Strongbox getStrongbox() {
        return strongbox;
    }

    public void activateLeaderCard(LeaderCard leaderCard) throws NotSatisfiedRequirementsException {
        for(LeaderCard x: leaderCards){
            if(x.equals(leaderCard))
                x.activate(this);
        }
    }

    public void discardLeaderCard(LeaderCard leaderCard) throws EndGameException {
        for (Iterator<LeaderCard> iterator = leaderCards.iterator(); iterator.hasNext();) {
            LeaderCard temp = iterator.next();
            if(temp.equals(leaderCard)) {
                if (temp.isActive())
                    throw new IllegalArgumentException("You cannot discard active cards");
                iterator.remove();
                this.gainFaithPoints(1);
            }
        }
    }

    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    /**
     * Returns the leader cards that can be activated or discarded
     * @return a list of leader cards that can be activated or discarded
     */
    public List<LeaderCard> getAvailableLeaderCards(){
        return leaderCards.stream().filter(x->!x.isActive()).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerBoard that = (PlayerBoard) o;
        return  username.equals(that.username) &&
                this.getFaithTrack().equals(that.getFaithTrack()) &&
                Arrays.equals(this.getDevelopmentCardSlots(), that.getDevelopmentCardSlots()) &&
                this.getStrongbox().equals(that.getStrongbox()) &&
                this.getWarehouse().equals(that.getWarehouse()) &&
                this.getLeaderCards().equals(that.getLeaderCards());
    }

    @Override
    public String toString() {
        return "PlayerBoard{" +
                "username='" + username + '\'' +
                ", faithTrack=" + faithTrack +
                ", warehouse=" + warehouse +
                ", strongbox=" + strongbox +
                ", developmentCardSlots=" + Arrays.toString(developmentCardSlots) +
                ", leaderCards=" + leaderCards +
                '}';
    }
}
