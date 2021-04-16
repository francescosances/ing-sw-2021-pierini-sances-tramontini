package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.LeaderCardsChooser;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.model.storage.exceptions.UnswappableDepotsException;
import it.polimi.ingsw.view.VirtualView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        this.currentStatus = PlayerStatus.TURN_ENDED;
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
        askForAction();
    }

    /**
     * Mark the player as waiting for others and notify the status update to the virtual view
     */
    public void turnEnded(){
        changeStatus(PlayerStatus.TURN_ENDED);
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
            this.changeStatus(PlayerStatus.TURN_ENDED);
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
        virtualView.askForAction(
                Arrays.stream(Action.values())
                        .filter(x -> x != Action.CANCEL)
                        .filter(x -> !((x == Action.PLAY_LEADER || x == Action.DISCARD_LEADER) && this.playerBoard.getAvailableLeaderCards().isEmpty())) //If no leader cards are available, the options are removed from the list
                        .toArray(Action[]::new));
        changeStatus(PlayerStatus.PERFORMING_ACTION);
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
                showWarehouseStatus();
                break;
                //TODO: move resources
            default:
                changeStatus(this.currentStatus.next());
        }
    }

    public void startNormalAction(){
        virtualView.yourTurn();
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
        virtualView.showWarehouseStatus(playerBoard.getWarehouse());
    }

    /**
     * Change the current status of the controller and notify the change to the observers
     * @param newStatus the new status of the player controller
     */
    protected void changeStatus(PlayerStatus newStatus){
        this.currentStatus = newStatus;
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
        }catch (UnswappableDepotsException e){
            virtualView.showErrorMessage(e.getMessage());
        }
        askForAction();
    }

    public enum PlayerStatus {
        CHOOSING_LEADER_CARDS,
        PERFORMING_ACTION,
        NORMAL_ACTION,
        TURN_ENDED;

        private static final PlayerStatus[] vals = {CHOOSING_LEADER_CARDS,PERFORMING_ACTION, NORMAL_ACTION,PERFORMING_ACTION,TURN_ENDED};

        public PlayerStatus next()
        {
            return vals[(this.ordinal()+1) % vals.length];//TODO: rivedere flusso perché passi due volte da performing action
        }
    }

    public class PlayerStatusIndex{

        private int currentIndex = 0;
        private final PlayerStatus[] vals = {PlayerStatus.CHOOSING_LEADER_CARDS,PlayerStatus.PERFORMING_ACTION, PlayerStatus.NORMAL_ACTION,PlayerStatus.PERFORMING_ACTION,PlayerStatus.TURN_ENDED};

        public PlayerStatus nextStatus(){
            currentIndex = (currentIndex+1)%vals.length;
            return vals[currentIndex];
        }

        public PlayerStatus getCurrentStatus(){
            return vals[currentIndex];
        }

    }
}
