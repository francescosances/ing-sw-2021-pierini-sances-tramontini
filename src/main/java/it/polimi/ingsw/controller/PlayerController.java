package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.model.storage.exceptions.UnswappableDepotsException;
import it.polimi.ingsw.view.VirtualView;

import java.util.*;

public class PlayerController {

    /**
     * The username associated with this player controller
     */
    protected String username;
    /**
     * The playerboard of to the player
     */
    protected PlayerBoard playerBoard;
    /**
     * True if the user is able to play. If false, the user will skip his turn
     */
    protected boolean active;
    /**
     * The virtual view connected to the client via socket
     */
    private VirtualView virtualView;
    /**
     * Represent the current status of the player
     */
    private final PlayerStatusIndex currentStatus;

    /**
     * A list of observers of the player status
     */
    private final List<PlayerStatusListener> observers = new ArrayList<>();

    /**
     * Strategy pattern used to get the user's cards choice
     */
    private LeaderCardsChooser leaderCardsChooser;

    private Resource currentResourceToStore;

    private DevelopmentCard currentDevelopmentCardToStore;

    private Runnable afterDepotsSwapAction;

    private Runnable skipAction;

    /**
     * Initialize a new player controller active and waiting for his turn.
     * @param username the username of the player associated with the player controller
     * @param playerBoard the playerboard of the user
     * @param virtualView the virtual view containing the reference to the socket connection
     */
    public PlayerController(String username,PlayerBoard playerBoard,VirtualView virtualView){
        this.username = username;
        this.playerBoard = playerBoard;
        this.active = true;
        this.virtualView = virtualView;
        currentStatus = new PlayerStatusIndex();
        resetSkipAction();
        resetAfterDepotsSwapAction();
    }

    /**
     * Mark the user as "online" and able to play
     */
    public void activate() {
        this.active = true;
    }

    /**
     * Mark the user as "offline" and unable to play
     */
    public void deactivate(){
        this.active = false;
    }

    /**
     * Returns true if the user is online and able to play, otherwise false
     * @return true if the user is online and able to play, otherwise false
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the username of the player associated to this controller
     * @return the username of the player associated to this controller
     */
    public String getUsername(){
        return username;
    }

    /**
     * Mark the player as currently playing and notify the status update to the virtual view
     */
    public void startTurn(){
         nextStatus();
    }

    /**
     * Mark the player as waiting for others and notify the status update to the virtual view
     */
    public void turnEnded(){
        this.currentStatus.setEndTurnStatus();
        for (PlayerStatusListener x : this.observers) {
            x.onPlayerStatusChanged(this);
        }
    }

    /**
     * Returns the virtual view containing the reference to the socket connection
     * @return the virtual view containing the reference to the socket connection
     */
    public VirtualView getVirtualView(){
        return virtualView;
    }

    /**
     * Sets the virtual view containing the reference to the socket connection
     * @param virtualView the virtual view containing the reference to the socket connection
     */
    public void setVirtualView(VirtualView virtualView){
        this.virtualView = virtualView;
    }

    /**
     * Returns the current status of the player
     * @return the current status of the player
     */
    public PlayerStatus getCurrentStatus() {
        return currentStatus.getCurrentStatus();
    }

    /**
     * Get 4 leader cards from the match and send it to the client through the virtual view.
     * If the user is inactive, the cards are randomly chosen
     */
    public void listLeaderCards(){
        List<LeaderCard> leaderCardList = playerBoard.getMatch().drawLeaderCards(4);
        if(isActive()){
            virtualView.listLeaderCards(leaderCardList,2);
        }else {
            chooseLeaderCards(leaderCardList.get(0),leaderCardList.get(1));
            //TODO: ricontrollare riconnessione utente
        }
        leaderCardsChooser = cards -> {
            for (LeaderCard card : cards) {
                playerBoard.addLeaderCard(card);
            }
            turnEnded();
        };
    }

    /**
     * Receive the leader cards chosen by the user and execute the action indicated by the leaderCardsChooser
     * @param cards the cards chosen by the user
     */
    public void chooseLeaderCards(LeaderCard ... cards){
        this.leaderCardsChooser.chooseLeaderCards(cards);
    }

    /**
     * Ask to the user which action want to perform
     */
    public void askForAction(){
        resetAfterDepotsSwapAction();
        virtualView.askForAction(
                Arrays.stream(Action.EXTRA_ACTIONS)
                        .filter(x -> x != Action.CANCEL)
                        .filter(x -> !((x == Action.PLAY_LEADER || x == Action.DISCARD_LEADER) && this.playerBoard.getAvailableLeaderCards().isEmpty())) //If no leader cards are available, the options are removed from the list
                        .toArray(Action[]::new));
    }

    /**
     * Start the action chosen by the user
     * @param action the action that must be performed
     */
    public void performAction(Action action) {
        switch (action) {
            case CANCEL:
                break;
            case PLAY_LEADER:
                listLeaderCardsToPlay();
                break;
            case DISCARD_LEADER:
                listLeaderCardsToDiscard();
                break;
            case MOVE_RESOURCES:
                virtualView.askToSwapDepots(getPlayerBoard().getWarehouse());
                break;
            case TAKE_RESOURCES_FROM_MARKET:
                takeResourcesFromMarket();
                break;
            case BUY_DEVELOPMENT_CARD:
                listDevelopmentCardToBuy();
                break;
            case ACTIVATE_PRODUCTION:
                break;
            case SKIP:
                skipAction.run();
                break;
            default:
                nextStatus();
        }
    }


    public void startNormalAction(){
        askForNormalAction();
    }

    public void askForNormalAction(){
        virtualView.askForAction(
                Arrays.stream(Action.NORMAL_ACTIONS)
                        .toArray(Action[]::new));
    }

    /**
     * Send the cards in the hand of the user so that he can choose a card to activate
     */
    public void listLeaderCardsToPlay(){
        virtualView.listLeaderCards(this.playerBoard.getAvailableLeaderCards(), 1);
        this.leaderCardsChooser = cards -> {
            try {
                for (LeaderCard card : cards) {
                    this.playerBoard.activateLeaderCard(card);
                }
            }catch (NotSatisfiedRequirementsException e){
                virtualView.showErrorMessage(e.getMessage());
            } finally {
                askForAction();
            }
        };
    }

    /**
     * Send the inactive cards in the hand of the user so that he can choose a card to discard
     */
    public void listLeaderCardsToDiscard(){
        virtualView.listLeaderCards(this.playerBoard.getAvailableLeaderCards(), 1);
        this.leaderCardsChooser = cards -> {
            for(LeaderCard card : cards){
                this.playerBoard.discardLeaderCard(card);
            }
            askForAction();
        };
    }

    /**
     * List the resources stored in the warehouse depots
     */
    public void showWarehouseStatus(){
        virtualView.showWarehouse(playerBoard.getWarehouse());
    }

    //TODO: aggiungere controlli anche quando la carta viene selezionata
    private void listDevelopmentCardToBuy() {
        if(getPlayerBoard().getMatch().getDevelopmentCardDecks().stream().noneMatch(t->{
                DevelopmentCard d = t.get(0);
                Requirements requirements = d.getCost();
                for(LeaderCard card : getPlayerBoard().getLeaderCards())
                    requirements = card.recalculateRequirements(requirements);
                return requirements.satisfied(getPlayerBoard()) && getPlayerBoard().acceptsDevelopmentCard(d);
        })) {
            virtualView.showErrorMessage("You can not buy any card");
            askForNormalAction();
        }else {
            List<Deck<DevelopmentCard>> cards = getPlayerBoard().getMatch().getDevelopmentCardDecks();
            List<Deck<DevelopmentCard>> newCards = new ArrayList<>();
            for(Deck<DevelopmentCard> deck : cards){
                Deck<DevelopmentCard> newDeck = new Deck<>();
                newCards.add(newDeck);
                DevelopmentCard newCard = deck.get(0).clone();
                Requirements requirements = newCard.getCost();
                for(LeaderCard leaderCard : getPlayerBoard().getLeaderCards())
                    requirements = leaderCard.recalculateRequirements(requirements);
                newCard.setCost(requirements);
                newDeck.add(newCard);
            }
            virtualView.listDevelopmentCards(newCards, 1, getPlayerBoard());
        }
    }

    /**
     * Change the current status of the controller and notify the change to the observers
     */
    private void nextStatus(){
        currentStatus.nextStatus();
        for (PlayerStatusListener x : this.observers) {
            x.onPlayerStatusChanged(this);
        }
    }

    /**
     * Add the specified observer to the list of observers
     * @param playerStatusListener the observer to add
     */
    public void addObserver(PlayerStatusListener playerStatusListener){
        observers.add(playerStatusListener);
    }

    /**
     * Remove the specified observer from the list of observers
     * @param playerStatusListener the observer to remove
     */
    public void removeObserver(PlayerStatusListener playerStatusListener){
        observers.remove(playerStatusListener);
    }

    public PlayerBoard getPlayerBoard() {
        return playerBoard;
    }

    public void endGame(){
        virtualView.showMessage("MATCH ENDED");//TODO: gestire messaggi sul vincitore
    }

    public void swapDepots(int depotA, int depotB) {
        try {
            playerBoard.getWarehouse().swapDepots(depotA, depotB);
        }catch (UnswappableDepotsException | IncompatibleDepotException e){
            virtualView.showErrorMessage(e.getMessage());
        }
        afterDepotsSwapAction.run();
    }

    public void selectMarketRow(int row) {
        Resource[] resources = getPlayerBoard().getMatch().getMarket().chooseRow(row);
        askToStoreResourcesFromMarket(resources);
    }

    public void selectMarketColumn(int column){
        Resource[] resources = getPlayerBoard().getMatch().getMarket().chooseColumn(column);
        askToStoreResourcesFromMarket(resources);
    }

    protected void askToStoreResourcesFromMarket(Resource[] resources){
        if(playerBoard.getLeaderCards().stream().filter(t->t.isWhiteMarble() && t.isActive()).count() <= 1){
            for(int i=0;i<resources.length;i++){
                for(LeaderCard card : playerBoard.getLeaderCards()){
                    resources[i] = card.convertResourceType(resources[i]);
                }
                if(resources[i] == NonPhysicalResourceType.VOID) {
                    resources[i] = null;
                }else if(resources[i] == NonPhysicalResourceType.FAITH_POINT){
                    playerBoard.gainFaithPoints(1);
                    virtualView.showMessage("You gained a faith point");
                    resources[i] = null;
                }
            }
        }
        resources = Arrays.stream(resources).filter(Objects::nonNull).toArray(Resource[]::new);
        getPlayerBoard().getWarehouse().toBeStored(resources);
        List<Resource> resourcesToStore = Arrays.asList(resources);
        Collections.reverse(resourcesToStore);
        virtualView.showResources(resourcesToStore.toArray(new Resource[0]));
        askToStoreResource();
    }

    /**
     * Show the market tray to the user
     */
    public void takeResourcesFromMarket(){
        virtualView.takeResourcesFromMarket(playerBoard.getMatch().getMarket());
    }

    public void askToStoreResource(){
        if(!getPlayerBoard().getWarehouse().hasResourcesToStore()){
            resetSkipAction();
            nextStatus();
            askForAction();
            return;
        }
       currentResourceToStore = getPlayerBoard().getWarehouse().popResourceToBeStored();
       if(currentResourceToStore == NonPhysicalResourceType.VOID){
           virtualView.chooseWhiteMarbleConversion(getPlayerBoard().getLeaderCards().get(0),getPlayerBoard().getLeaderCards().get(1));
       }else{
           askToConfirmDepotsStatus();
       }
    }

    public void chooseWhiteMarbleConversion(int choice){
        if(currentResourceToStore != NonPhysicalResourceType.VOID)
            throw new IllegalStateException("Invalid command");
        Resource resource = getPlayerBoard().getLeaderCards().get(choice).getOutputResourceType();
        getPlayerBoard().getWarehouse().pushResourceToBeStored(resource);
        askToStoreResource();
    }

    protected void askToConfirmDepotsStatus(){
        setAfterDepotsSwapAction(() -> virtualView.askToStoreResource(currentResourceToStore,getPlayerBoard().getWarehouse()));
        setSkipAction(() -> virtualView.askToStoreResource(currentResourceToStore,getPlayerBoard().getWarehouse()));
        Action[] actions = {Action.SKIP,Action.MOVE_RESOURCES};
        virtualView.showMessage("You have to store a "+currentResourceToStore);
        showWarehouseStatus();
        virtualView.askForAction(actions);
    }

    protected void storeResourceToWarehouse(int depot){
        if(depot < getPlayerBoard().getWarehouse().getDepots().size())
            try {
                getPlayerBoard().getWarehouse().addResource(depot, (ResourceType) currentResourceToStore, 1);
            }catch (IncompatibleDepotException e){
                virtualView.showErrorMessage(e.getMessage());
                getPlayerBoard().getWarehouse().pushResourceToBeStored(currentResourceToStore);
                askToStoreResource();
                return;
            }
        else
            getPlayerBoard().getMatch().discardResource(getPlayerBoard());

        currentResourceToStore = null;
        if(getPlayerBoard().getWarehouse().hasResourcesToStore())
           askToStoreResource();
        else {
            resetSkipAction();
            showWarehouseStatus();
            nextStatus();
        }
    }

    public void showPlayerBoard() {
        this.virtualView.showPlayerBoard(this.playerBoard);
    }

    public void buyDevelopmentCard(DevelopmentCard developmentCard) {
        if(!developmentCard.getCost().satisfied(getPlayerBoard())){
            virtualView.showErrorMessage("You cannot buy this card");
            listDevelopmentCardToBuy();
            return;
        }
        playerBoard.buyDevelopmentCard(developmentCard);
        this.currentDevelopmentCardToStore = developmentCard;
        virtualView.askToChooseDevelopmentCardSlot(playerBoard.getDevelopmentCardSlots(),developmentCard);
    }

    public void chooseDevelopmentCardSlot(int slotIndex) {
        if(!playerBoard.getDevelopmentCardSlots()[slotIndex].accepts(currentDevelopmentCardToStore)){
            virtualView.showErrorMessage("You cannot choose this slot");
            virtualView.askToChooseDevelopmentCardSlot(playerBoard.getDevelopmentCardSlots(),currentDevelopmentCardToStore);
            return;
        }
        playerBoard.getDevelopmentCardSlots()[slotIndex].addCard(currentDevelopmentCardToStore);
        currentDevelopmentCardToStore = null;
        nextStatus();
    }

    private void setAfterDepotsSwapAction(Runnable afterDepotsSwapAction) {
        this.afterDepotsSwapAction = afterDepotsSwapAction;
    }

    private void resetAfterDepotsSwapAction(){
        setAfterDepotsSwapAction(()->{
                showWarehouseStatus();
                askForAction();
        });
    }

    private void setSkipAction(Runnable skipAction){
        this.skipAction = skipAction;
    }

    private void resetSkipAction(){
        setSkipAction(this::nextStatus);
    }

    public enum PlayerStatus {
        PERFORMING_ACTION,
        NORMAL_ACTION,
        TURN_ENDED
    }

    public class PlayerStatusIndex{

        private int currentIndex;
        private final PlayerStatus[] vals = {PlayerStatus.PERFORMING_ACTION, PlayerStatus.NORMAL_ACTION,PlayerStatus.PERFORMING_ACTION,PlayerStatus.TURN_ENDED};

        public PlayerStatus nextStatus(){
            currentIndex = (currentIndex+1)%vals.length;
            return vals[currentIndex];
        }

        public void setEndTurnStatus(){
            this.currentIndex = Arrays.asList(vals).indexOf(PlayerStatus.TURN_ENDED);
        }

        public PlayerStatus getCurrentStatus(){
            return vals[currentIndex];
        }

    }
}
