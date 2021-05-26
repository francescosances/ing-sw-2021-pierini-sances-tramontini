package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.model.storage.*;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.ObservableFromView;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.VirtualView;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerBoard implements Cloneable, ObservableFromView {

    /**
     * A reference to the match
     */
    private transient Match match;

    /**
     * Player's username
     */
    private final String username;

    /**
     * A reference to their faith Track
     */
    protected FaithTrack faithTrack;

    /**
     * A reference to their warehouse
     */
    protected Warehouse warehouse;

    /**
     * A reference to their strongbox
     */
    protected Strongbox strongbox;

    /**
     * An array referencing their DevelopmentCardSlots
     */
    private final DevelopmentCardSlot[] developmentCardSlots;

    /**
     * A list referencing their LeaderCards
     */
    private List<LeaderCard> leaderCards;

    /**
     * A counter that stores the number of DevelopmentCards bought
     */
    private int boughtDevelopmentCardsCounter = 0;

    /**
     * A list containing all VirtualViews to notify on update
     */
    private transient List<VirtualView> views;

    /**
     * Initializes a new PlayerBoard object
     * @param username the player's username
     * @param match a reference to the match
     */
    public PlayerBoard(String username,Match match){
        this.match = match;
        this.username = username;
        warehouse = new Warehouse();
        strongbox = new Strongbox();
        faithTrack = new FaithTrack(match, username);
        developmentCardSlots = Stream.generate(DevelopmentCardSlot::new).limit(3).toArray(DevelopmentCardSlot[]::new);

        //TODO: rimuovere bypass
        developmentCardSlots[0].addCard(match.getDevelopmentCardDecks().get(0).top());
        developmentCardSlots[0].addCard(match.getDevelopmentCardDecks().get(1).top());
        developmentCardSlots[1].addCard(match.getDevelopmentCardDecks().get(3).top());

        try {
            warehouse.addResources(0,ResourceType.SERVANT,1);
            warehouse.addResources(1,ResourceType.SHIELD,1);
        } catch (IncompatibleDepotException e) {
            e.printStackTrace();
        }


        leaderCards = new ArrayList<>();
        views = new ArrayList<>();
    }

    /**
     * Returns true if the the player has a slot where a DevelopmentCard can be stored into, false elsewhere
     * @param developmentCard the DevelopmentCard to check
     * @return true if the the player has a slot where a DevelopmentCard can be stored into, false elsewhere
     */
    public boolean acceptsDevelopmentCard(DevelopmentCard developmentCard){
        return Arrays.stream(getDevelopmentCardSlots()).anyMatch(t->t.accepts(developmentCard));
    }

    /**
     * Pays the requirements needed to buy the DevelopmentCard, applying discounts where possible
     * @param developmentCard the DevelopmentCard to buy
     */
    public void buyDevelopmentCard(DevelopmentCard developmentCard) throws EndGameException{
        Requirements requirements = developmentCard.getCost();
        for(LeaderCard leaderCard:leaderCards)
            requirements = leaderCard.recalculateRequirements(requirements);
        if(!requirements.satisfied(this)){
            throw new IllegalArgumentException("Card not purchasable");
        }
        requirements = warehouse.removeResources(requirements);
        strongbox.removeResources(requirements);
        boughtDevelopmentCardsCounter++;
        if (boughtDevelopmentCardsCounter == 7)
            throw new EndGameException();
        match.buyDevelopmentCard(developmentCard, this);
    }

    /**
     * Advances the player in their Faith Track
     * @param points the number of points the player must move
     * @throws EndGameException if last space is reached
     */
    public void gainFaithPoints(int points) throws EndGameException {
        for (int i=0;i<points;i++)
            faithTrack.moveMarker();
    }

    /**
     * Returns the number of total victory points gained by the player
     * @return the number of total victory points gained by the player
     */
    public int getTotalVictoryPoints(){
        return faithTrack.getVictoryPoints() +
                getDevelopmentCardsVictoryPoints() +
                getLeaderCardsVictoryPoints() +
                getResourcesVictoryPoints();
    }

    /**
     * Returns the number of victory points gained by the DevelopmentCards only
     * @return the number of victory points gained by the DevelopmentCards only
     */
    public int getDevelopmentCardsVictoryPoints(){
        return Arrays.stream(developmentCardSlots)
                .mapToInt(DevelopmentCardSlot::getVictoryPoints)
                .sum();
    }

    /**
     * Returns the number of victory points gained by the LeaderCards only
     * @return the number of victory points gained by the LeaderCards only
     */
    public int getLeaderCardsVictoryPoints(){
        return leaderCards.stream()
                .filter(LeaderCard::isActive)
                .mapToInt(Card::getVictoryPoints)
                .sum();
    }

    /**
     * Returns the number of victory points gained by the number of resources only
     * @return the number of victory points gained by the number of resources only
     */
    public int getResourcesVictoryPoints(){
        return Arrays.stream(ResourceType.values())
                .mapToInt(res -> getAllResources().getResources(res)).sum() / 5;
    }

    /**
     * Returns the match reference
     * @return the match reference
     */
    public Match getMatch() {
        return match;
    }

    /**
     * Returns player's username String
     * @return player's username String
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns player's FaithTrack
     * @return player's FaithTrack
     */
    public FaithTrack getFaithTrack() {
        return faithTrack;
    }

    /**
     * Returns player's DevelopmentCardSlots array
     * @return player's DevelopmentCardSlots array
     */
    public DevelopmentCardSlot[] getDevelopmentCardSlots(){
        return developmentCardSlots;
    }

    /**
     * Returns a Requirements object containing all the resources the player owns
     * @return a Requirements object containing all the resources the player owns
     */
    public Requirements getAllResources(){
        return Requirements.sum(strongbox.getAllResources(), warehouse.getAllResources());
    }

    /**
     * Adds a LeaderCard to player's hand
     * @param card to be added
     */
    public void addLeaderCard(LeaderCard card){
        this.leaderCards.add(card);
    }

    /**
     * Returns player's Warehouse
     * @return player's Warehouse
     */
    public Warehouse getWarehouse() {
        return warehouse;
    }

    /**
     * Returns player's Strongbox
     * @return player's Strongbox
     */
    public Strongbox getStrongbox() {
        return strongbox;
    }

    /**
     * Activates the LeaderCard
     * @param leaderCard the LeaderCard to be activated
     * @throws NotSatisfiedRequirementsException if the LeaderCard cannot be activated
     */
    public void activateLeaderCard(LeaderCard leaderCard) throws NotSatisfiedRequirementsException {
        for(LeaderCard x: leaderCards){
            if(x.equals(leaderCard)) {
                x.activate(this);
                updateLeaderCardsList();
            }
        }
    }


    /**
     * Activates the LeaderCard in the selected position
     * @param num the position if the LeaderCard to be activated
     * @throws NotSatisfiedRequirementsException if the LeaderCard cannot be activated
     */
    public void activateLeaderCard(int num) throws NotSatisfiedRequirementsException {
        activateLeaderCard(getAvailableLeaderCards().get(num));
    }

    /**
     * Discards the LeaderCard, makes the player gain 1 FaithPoint
     * @param leaderCard the LeaderCard to discard
     * @throws EndGameException if the player reaches the last space
     */
    public void discardLeaderCard(LeaderCard leaderCard) throws EndGameException {
        for (Iterator<LeaderCard> iterator = leaderCards.iterator(); iterator.hasNext();) {
            LeaderCard temp = iterator.next();
            if(temp.equals(leaderCard)) {
                if (temp.isActive())
                    throw new IllegalArgumentException("You cannot discard active cards");
                iterator.remove();
                updateLeaderCardsList();
                this.gainFaithPoints(1);
            }
        }
    }

    /**
     * Discards the LeaderCard, makes the player gain 1 FaithPoint
     * @param num the number of the LeaderCard's position thatdiscarded
     * @throws EndGameException if the player reaches the last space
     */
    public void discardLeaderCard(int num) throws EndGameException {
        discardLeaderCard(leaderCards.get(num));
    }

    /**
     * Returns a list with all player's LeaderCards
     * @return a list with all player's LeaderCards
     */
    public List<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public void setLeaderCards(List<LeaderCard> leaderCardList) {
        this.leaderCards = leaderCardList;
    }

    /**
     * Returns the leader cards that can be activated or discarded
     * @return a list of leader cards that can be activated or discarded
     */
    public List<LeaderCard> getAvailableLeaderCards(){
        return leaderCards.stream().filter(x->!x.isActive()).collect(Collectors.toList());
    }

    /**
     * Returns the boughtDevelopmentCardsCounter attribute
     * @return  the boughtDevelopmentCardsCounter
     */
    public int getBoughtDevelopmentCardsCounter() {
        return boughtDevelopmentCardsCounter;
    }

    /**
     * Returns a list containing all Available productions
     * @return a list containing all Available productions
     */
    public List<Producer> getAvailableProductions() {
        List<Producer> producers = new ArrayList<>();
        producers.add(DevelopmentCard.getBaseProduction());
        Arrays.stream(developmentCardSlots).forEach(slot->{
            if(slot.getTopCard() != null)
                producers.add(slot.getTopCard());
        });
        leaderCards.forEach(card -> {
            if(card.isActive() && card.isProductionLeaderCard())
                producers.add((ProductionLeaderCard) card);
        });
        return producers;
    }

    public void setMatch(Match match){
        this.match = match;
        this.faithTrack.setMatch(match);
    }

    public void addDevelopmentCardToSlot(DevelopmentCard developmentCard, int slot){
        if (!developmentCardSlots[slot].accepts(developmentCard))
            throw new NotSatisfiedRequirementsException("You cannot buy this card");
        developmentCardSlots[slot].addCard(developmentCard);
        updateSlots();
    }

    /**
     * Makes the player pay the cost
     * @param costs the cost player must pay
     */
    public void payResources(Requirements costs) {
        costs = warehouse.removeResources(costs);
        if (costs.getResourcesMap().isEmpty())
            strongbox.removeResources(costs);
    }

    /**
     * Activates the production, paying the costs and gaining the gains
     * @param costs the cost of the production
     * @param gains the gains of the production
     */
    public void produce(Requirements costs, Requirements gains){
        updateProduction();
        payResources(costs);
        Map<ResourceType,Integer> newGains = new HashMap<>();
        gains.forEach(entry->{
            if(entry.getKey() == NonPhysicalResourceType.FAITH_POINT)
                gainFaithPoints(1);
            else if(entry.getKey() instanceof ResourceType)
                newGains.put((ResourceType) entry.getKey(),entry.getValue());
        });
        strongbox.addResources(newGains);
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
        for (VirtualView view:views)
            warehouse.addView(view);
    }

    /**
     * Adds the view to the list of views
     * @param view the view that has to be added
     */
    @Override
    public void addView(VirtualView view) {
        if (views == null)
            views = new ArrayList<>();
        views.add(view);
        faithTrack.addView(view);
        strongbox.addView(view);
        warehouse.addView(view);
    }

    /**
     * Removes the view from the list of views
     * @param view the view that has to be removed
     */
    @Override
    public void removeView(VirtualView view) {
        try {
            views.remove(view);
            faithTrack.removeView(view);
            strongbox.removeView(view);
            warehouse.removeView(view);
        } catch (NullPointerException ignored){}
    }


    /**
     * Notifies all views of the change
     */
    private void updateLeaderCardsList() {
        for (VirtualView view:views)
            view.showLeaderCards(leaderCards);
    }

    /**
     * Notifies all views of the change
     */
    private void updateSlots(){
        for (VirtualView view:views)
            view.showDevelopmentCardSlots(developmentCardSlots);
    }


    /**
     * Notifies all views of the change
     */
    private void updateProduction(){
        for (VirtualView view:views)
            view.showProduction();
    }


    /**
     * Returns a String representation of the object
     * @return a String representation of the object
     */
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

    /**
     * Indicates whether some other object is equal to this one
     * @param o that is confronted
     * @return true if o is equal to the object, false elsewhere
     */

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

    /**
     * Returns the hash code of the object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * Returns a new Object which is equal to this
     * @return a new Object which is equal to this
     */
    @Override
    public PlayerBoard clone() {
        return Serializer.deserializePlayerBoard(Serializer.serializePlayerBoard(this));
    }
}

