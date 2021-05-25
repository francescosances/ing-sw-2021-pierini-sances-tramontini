package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.model.storage.exceptions.UnswappableDepotsException;
import it.polimi.ingsw.view.VirtualView;

import java.util.*;
import java.util.stream.Collectors;

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
    private transient VirtualView virtualView;
    /**
     * Represent the current status of the player
     */
    private PlayerStatus currentStatus;

    /**
     * A list of observers of the player status
     */
    private final List<PlayerStatusListener> observers = new ArrayList<>();

    /**
     * Strategy pattern used to get the user's cards choice
     */
    private LeaderCardsChooser leaderCardsChooser;

    /**
     * Holds a resource while it is being stored
     */
    private Resource currentResourceToStore;

    /**
     * Holds a DevelopmentCard while it is being stored
     */
    private DevelopmentCard currentDevelopmentCardToStore;

    /**
     * Runnable with the action to do after a SwapDepots action
     */
    private Runnable afterDepotsSwapAction;

    /**
     * Runnable with the action that allows to skip an action
     */
    private Runnable skipAction;

    /**
     * True if the user has already choose the start resources
     */
    private boolean gotResourcesOfYourChoice;

    /**
     * The index of the player in the players array
     */
    private int playerIndex;

    /**
     * The number of resources that the user can choose before the match starts
     */
    private int resourcesToChoose;

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
        currentStatus = PlayerStatus.PERFORMING_ACTION;
        gotResourcesOfYourChoice = false;
        resetSkipAction();
        setAfterDepotsSwapAction(this::askForAction);
    }

    public PlayerController(String username,PlayerBoard playerBoard){
        this.username = username;
        this.playerBoard = playerBoard;
        this.active = false;
    }

    /**
     * Mark the user as "online" and able to play
     */
    public void activate() {
        this.active = true;
        currentStatus = PlayerStatus.TURN_ENDED;
    }

    /**
     * Mark the user as "offline" and unable to play
     */
    public void deactivate(){
        this.active = false;
        currentStatus = PlayerStatus.TURN_ENDED;

        if(currentDevelopmentCardToStore != null){
            DevelopmentCardSlot slot;
            for (int i = 0; i < playerBoard.getDevelopmentCardSlots().length; i++) {
                slot = playerBoard.getDevelopmentCardSlots()[i];
                if(slot.accepts(currentDevelopmentCardToStore)) {
                    playerBoard.addDevelopmentCardToSlot(currentDevelopmentCardToStore, i);
                    currentDevelopmentCardToStore = null;
                    break;
                }
            }
        }
        currentResourceToStore = null;
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
        currentStatus = PlayerStatus.TURN_ENDED;
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
        return currentStatus;
    }

    /**
     * Handles the player setup, giving the initial amount of resources and faith points.
     */
    public void setup(){
        int faithPoints = 0;
        switch (playerIndex){
            case 0:
                gotResourcesOfYourChoice = true;
                break;
            case 1:
                resourcesToChoose = 1;
                break;
            case 2:
                resourcesToChoose = 1;
                faithPoints = 1;
                break;
            case 3:
                resourcesToChoose = 2;
                faithPoints = 1;
                break;
        }
        getPlayerBoard().gainFaithPoints(faithPoints);
        if(!isActive()) {
            defaultSetup();
            return;
        }
        if(!gotResourcesOfYourChoice){
            askToChooseStartResources(resourcesToChoose);
        }else{
            listLeaderCards();
        }
    }

    /**
     * Method called if the user is inactive during the setup phase. He will draw two random leader cards and a random resource
     */
    public void defaultSetup(){
        List<LeaderCard> leaderCardList = playerBoard.getMatch().drawLeaderCards(4);
        LeaderCard[] automaticallyChosen = {leaderCardList.get(0),leaderCardList.get(1)};
        for (LeaderCard card : automaticallyChosen) {
            playerBoard.addLeaderCard(card);
            playerBoard.getMatch().chooseLeaderCard(card);
        }
        Resource[] randomResources = new Resource[resourcesToChoose];
        for(int i=0;i<resourcesToChoose;i++)
            randomResources[i] = ResourceType.values()[new Random().nextInt(resourcesToChoose)];
        chooseStartResources(randomResources);
    }

    /**
     * Get 4 leader cards from the match and send it to the client through the virtual view.
     * If the user is inactive, the cards are randomly chosen
     */
    public void listLeaderCards(){
        List<LeaderCard> leaderCardList = playerBoard.getMatch().drawLeaderCards(4);
        virtualView.listLeaderCards(leaderCardList,2);
        leaderCardsChooser = cards -> {
            for (LeaderCard card : cards) {
                playerBoard.addLeaderCard(card);
                playerBoard.getMatch().chooseLeaderCard(card);
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
     * Discards the LeaderCard the player has in the selected position
     * @param num the position of the LeaderCard in the PlayerBoard
     */
    public void discardLeaderCard(int num){
        playerBoard.discardLeaderCard(num);
        for (PlayerStatusListener x : this.observers) {
            x.onPlayerStatusChanged(this);
        }
        virtualView.showPlayerLeaderCards(playerBoard.getLeaderCards());
    }

    /**
     * Activates the LeaderCard the player has in the selected position
     * @param num the position of the LeaderCard in the PlayerBoard
     */
    public void activateLeaderCard(int num){
        try {
            playerBoard.activateLeaderCard(num);
        } catch (Exception e){
            virtualView.showErrorMessage(e.getMessage());
        } finally {
            for (PlayerStatusListener x : this.observers) {
                x.onPlayerStatusChanged(this);
            }
        }
        virtualView.showPlayerLeaderCards(playerBoard.getLeaderCards());
    }

    /**
     * Asks the player which ResourceType he wants as they're not starting the match
     * @param resourcesToChoose the number of resource the player is allowed to choose
     */
    public void askToChooseStartResources(int resourcesToChoose){
        virtualView.askToChooseStartResources(ResourceType.values(),resourcesToChoose);
    }

    /**
     * Stores the resources chosen into the Strongbox
     * @param resources the resources to store
     */
    public void chooseStartResources(Resource[] resources) {
        if(resources.length != resourcesToChoose)
            throw new IllegalArgumentException("Invalid number of resource of your choosing");
        try {
            if (resourcesToChoose >= 1) {
                playerBoard.getWarehouse().addResources(2, (ResourceType) resources[0], 1);
            }
            if (resourcesToChoose == 2) {
                playerBoard.getWarehouse().addResources((resources[0] == resources[1]) ? 2 : 1, (ResourceType) resources[1], 1);
            }
        }catch (IncompatibleDepotException e){
            e.printStackTrace();
        }
        gotResourcesOfYourChoice = true;
        if(isActive())
            listLeaderCards();
    }

    /**
     * Ask to the user which action want to perform
     */
    public void askForAction(){
        setAfterDepotsSwapAction(this::askForAction);
        virtualView.askForAction(
                playerBoard.getMatch().getPlayers().stream().map(PlayerBoard::getUsername).collect(Collectors.toList()),
                Arrays.stream(Action.ALL_ACTIONS)
                        .filter(x -> x != Action.CANCEL)
                        .filter(x -> !((x == Action.PLAY_LEADER) && this.playerBoard.getAvailableLeaderCards().isEmpty())) //If no leader cards are available, the options are removed from the list
                        .toArray(Action[]::new));
    }

    /**
     * Start the action chosen by the user
     * @param action the action that must be performed
     */
    public void performAction(Action action) {
        switch (action) {
            case CANCEL:
                rollback();
                break;
            case PLAY_LEADER:
                listPlayableLeaderCards();
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
                virtualView.chooseProductions(playerBoard.getAvailableProductions(),playerBoard);
                break;
            case SKIP:
                skipAction.run();
                break;
            default:
                nextStatus();
        }
    }

    /**
     * Starts a normal Action
     */
    public void startNormalAction(){
        askForNormalAction();
    }

    /**
     * Asks the player what normal action they want to perform
     */
    public void askForNormalAction(){
        setAfterDepotsSwapAction(this::askForNormalAction);
        virtualView.askForAction(
                playerBoard.getMatch().getPlayers().stream().map(PlayerBoard::getUsername).collect(Collectors.toList()),
                Arrays.stream(Action.NORMAL_ACTIONS)
                        .toArray(Action[]::new));
    }

    /**
     * Send the cards in the hand of the user to the player.
     */
    public void listPlayableLeaderCards(){
        virtualView.showPlayerLeaderCards(playerBoard.getAvailableLeaderCards());
    }

    /**
     * List the resources stored in the warehouse depots
     */
    public void showWarehouseStatus(){
        virtualView.showWarehouse(playerBoard.getWarehouse());
    }

    /**
     * Shows all DevelopmentCards at the top of their Deck.
     */
    //TODO: aggiungere controlli anche quando la carta viene selezionata
    private void listDevelopmentCardToBuy() {
        List<Deck<DevelopmentCard>> cards = getPlayerBoard().getMatch().getDevelopmentCardDecks();
        List<Deck<DevelopmentCard>> newCards = new ArrayList<>();
        for(Deck<DevelopmentCard> deck : cards){
            Deck<DevelopmentCard> newDeck = new Deck<>();
            newCards.add(newDeck);
            for(DevelopmentCard card:deck) {
                DevelopmentCard newCard = card.clone();
                Requirements requirements = newCard.getCost();
                for (LeaderCard leaderCard : getPlayerBoard().getLeaderCards())
                    requirements = leaderCard.recalculateRequirements(requirements);
                newCard.setCost(requirements);
                newDeck.add(newCard);
            }
        }virtualView.listDevelopmentCards(newCards, 1, getPlayerBoard());
    }

    /**
     * Changes the current status of the controller and notifies the change to the observers
     */
    private void nextStatus(){
        currentStatus = currentStatus.nextStatus();
        for (PlayerStatusListener x : this.observers) {
            x.onPlayerStatusChanged(this);
        }
    }

    /**
     * Makes the controller choose a new action and notifies the observers of the rollback
     */
    private void rollback(){
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

    /**
     * Returns the PlayerBoard associated with the PlayerController
     * @return the PlayerBoard associated with the PlayerController
     */
    public PlayerBoard getPlayerBoard() {
        return playerBoard;
    }

    /**
     * Organizes the endGame Phase
     */
    public void endGame(){
        virtualView.showMessage("MATCH ENDED");//TODO: gestire messaggi sul vincitore
    }

    /**
     * Swaps depotA with depotB
     * @param depotA the number of the one of the two depots to swap
     * @param depotB the number of the one of the two depots to swap
     */
    public void swapDepots(int depotA, int depotB) {
        try {
            playerBoard.getWarehouse().swapDepots(depotA, depotB);
        }catch (UnswappableDepotsException | IncompatibleDepotException e){
            virtualView.showErrorMessage(e.getMessage());
        }
        afterDepotsSwapAction.run();
    }

    /**
     * Takes the resources from the chosen row of the market and asks the player where they wants to store them
     * @param row the row from which take the resources
     */
    public void selectMarketRow(int row) {
        Resource[] resources = getPlayerBoard().getMatch().getMarket().chooseRow(row);
        askToStoreResourcesFromMarket(resources);
    }

    /**
     * Takes the resources from the chosen column of the market and asks the player where they wants to store them
     * @param column the column from which take the resources
     */
    public void selectMarketColumn(int column){
        Resource[] resources = getPlayerBoard().getMatch().getMarket().chooseColumn(column);
        askToStoreResourcesFromMarket(resources);
    }

    /**
     * Asks the player where they want to store the resources, any is takenR
     * @param resources the resources taken from the market
     */
    protected void askToStoreResourcesFromMarket(Resource[] resources){
        for (int i = 0; i < resources.length; i++) {
            if (playerBoard.getLeaderCards().stream().filter(t -> t.isWhiteMarble() && t.isActive()).count() <= 1) {
                for (LeaderCard card : playerBoard.getLeaderCards()) {
                    resources[i] = card.convertResourceType(resources[i]);
                }
                if (resources[i] == NonPhysicalResourceType.VOID) {
                    resources[i] = null;
                }
            } if (resources[i] == NonPhysicalResourceType.FAITH_POINT) {
                playerBoard.gainFaithPoints(1);
                virtualView.showMessage("You gained a faith point");
                resources[i] = null;
            }
        }

        resources = Arrays.stream(resources).filter(Objects::nonNull).toArray(Resource[]::new);
        getPlayerBoard().getWarehouse().toBeStored(resources);
        List<Resource> resourcesToStore = Arrays.asList(resources);
        Collections.reverse(resourcesToStore);
        if(resourcesToStore.isEmpty()){
            resetSkipAction();
            nextStatus();
            return;
        }
        askToStoreResource();
    }

    /**
     * Starts the "take resources from market" action by showing the player the market
     * and asking them what they want to choose
     */
    public void takeResourcesFromMarket(){
        virtualView.takeResourcesFromMarket(playerBoard.getMatch().getMarket());
    }

    /**
     * Asks the player where they want to store the resource
     */
    public void askToStoreResource(){
       currentResourceToStore = getPlayerBoard().getWarehouse().popResourceToBeStored();
       if(currentResourceToStore == NonPhysicalResourceType.VOID){
           virtualView.chooseWhiteMarbleConversion(getPlayerBoard().getLeaderCards().get(0),getPlayerBoard().getLeaderCards().get(1));
       }else
           askToConfirmDepot();
    }

    /**
     * Asks the player with 2 WhiteMarbleLeaderCards what they want to convert their VOID resource into
     * @param choice the leader card whose conversion is applied
     */
    public void chooseWhiteMarbleConversion(int choice){
        if(currentResourceToStore != NonPhysicalResourceType.VOID)
            throw new IllegalStateException("Invalid command");
        Resource resource = getPlayerBoard().getLeaderCards().get(choice).getOutputResourceType();
        getPlayerBoard().getWarehouse().pushResourceToBeStored(resource);
        askToStoreResource();
    }

    /**
     * Asks the player what depot they want to store currentResourceToStore into
     */
    protected void askToConfirmDepot(){
        setAfterDepotsSwapAction(() -> virtualView.askToStoreResource(currentResourceToStore,getPlayerBoard().getWarehouse()));
        virtualView.askToStoreResource(currentResourceToStore, getPlayerBoard().getWarehouse());
    }

    /**
     * Stores the resource into the chosen depot
     * @param depot the depot that will store the resource
     */
    protected void storeResourceToWarehouse(int depot){
        if(depot < getPlayerBoard().getWarehouse().getDepots().size())
            try {
                getPlayerBoard().getWarehouse().addResources(depot, (ResourceType) currentResourceToStore, 1);
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
            nextStatus();
        }
    }

    /**
     * shows the player their PlayerBoard
     */
    public void showPlayerBoard() {
        this.virtualView.showPlayerBoard(this.playerBoard);
    }

    /**
     * shows the player a PlayerBoard
     * @param playerBoard the PlayerBoard to be shown
     */
    public void showPlayerBoard(PlayerBoard playerBoard){
        this.virtualView.showPlayerBoard(playerBoard);
        for (PlayerStatusListener x : this.observers)
            x.onPlayerStatusChanged(this);
    }

    /**
     * Makes the player buy the selected DevelopmentCard.
     * Makes the player pay the required resources
     * @param developmentCard the DevelopmentCard the player wants to buy
     */
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

    /**
     * Stores currentDevelopmentCardToStore into the selected slot
     * @param slotIndex the slot that will store currentDevelopmentCardToStore
     */
    public void chooseDevelopmentCardSlot(int slotIndex) {
        if(!playerBoard.getDevelopmentCardSlots()[slotIndex].accepts(currentDevelopmentCardToStore)){
            virtualView.showErrorMessage("You cannot choose this slot");
            virtualView.askToChooseDevelopmentCardSlot(playerBoard.getDevelopmentCardSlots(),currentDevelopmentCardToStore);
            return;
        }
        playerBoard.addDevelopmentCardToSlot(currentDevelopmentCardToStore, slotIndex);
        currentDevelopmentCardToStore = null;
        nextStatus();
    }

    /**
     * Checks if the player can pay the resources in parameter cost and removes them from the player
     * Gives the player the resources in parameter gain
     * @param costs the Requirements the player must pay to activate the production
     * @param gains the Resources the player gain if the production is activated
     */
    public void chooseProductions(Requirements costs,Requirements gains) {
        if(!costs.satisfied(playerBoard)){
            virtualView.showErrorMessage("You cannot activate these productions");
            askForNormalAction();
            return;
        }
        playerBoard.payResources(costs);
        Map<ResourceType,Integer> newGains = new HashMap<>();
        gains.forEach(entry->{
            if(entry.getKey() == NonPhysicalResourceType.FAITH_POINT)
                playerBoard.gainFaithPoints(1);
            else if(entry.getKey() instanceof ResourceType)
                newGains.put((ResourceType) entry.getKey(),entry.getValue());
        });
        playerBoard.getStrongbox().addResources(newGains);
        nextStatus();
    }

    /**
     * Sets the afterDepotsSwapAction attribute
     * @param afterDepotsSwapAction the method the runnable must run when called
     */
    private void setAfterDepotsSwapAction(Runnable afterDepotsSwapAction) {
        this.afterDepotsSwapAction = afterDepotsSwapAction;
    }

    /**
     * Sets the skipAction attribute
     * @param skipAction the method the runnable must run when called
     */
    private void setSkipAction(Runnable skipAction){
        this.skipAction = skipAction;
    }

    /**
     * Resets the resetSkipAction attribute to its default value
     */
    private void resetSkipAction(){
        setSkipAction(this::nextStatus);
    }

    /**
     * Set the index of the player in the players array
     * @param i the index of the player
     */
    public void setPlayerIndex(int i) {
        this.playerIndex = i;
    }


    /**
     * Enumerates the status a player can be in
     */
    public enum PlayerStatus {
        PERFORMING_ACTION,
        ACTION_PERFORMED,
        TURN_ENDED;

        /**
         * Returns the PlayerStatus that is the actual one
         * @return the PlayerStatus that is the actual one
         */
        private PlayerStatus nextStatus(){
            switch (this) {
                case PERFORMING_ACTION:
                    return ACTION_PERFORMED;
                case ACTION_PERFORMED:
                    return TURN_ENDED;
                default:
                    return PERFORMING_ACTION;
            }
        }
    }

}
