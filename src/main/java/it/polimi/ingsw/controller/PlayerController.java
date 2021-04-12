package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.LeaderCardsChooser;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.view.VirtualView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
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
    public void yourTurn(){
        changeStatus(PlayerStatus.YOUR_TURN);
        virtualView.yourTurn();
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
        //TODO: capire perché questa azione viene eseguita prima dell'arrivo della scelta dell'utente
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
                showDepotsStatus();
                //TODO: move resources
                break;
            default:
                changeStatus(this.currentStatus.next());
                return;
        }
        this.askForAction();
    }


    /**
     * Send the cards in the hand of the user so that he can choose a card to activate
     */
    public void listLeaderCardsToPlay(){
        virtualView.listLeaderCards(this.playerBoard.getAvailableLeaderCards(), 1);
        this.leaderCardsChooser = cards -> {
            for(LeaderCard card : cards){
                this.playerBoard.activateLeaderCard(card);
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
        };
    }

    /**
     * List the resources stored in the warehouse depots
     */
    public void showDepotsStatus(){
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

    public void endGame(){
        virtualView.showMessage("MATCH ENDED");//TODO: gestire messaggi sul vincitore
    }

    public enum PlayerStatus {
        PERFORMING_ACTION,
        YOUR_TURN,
        TURN_ENDED;

        private static final PlayerStatus[] vals = {PERFORMING_ACTION,YOUR_TURN,PERFORMING_ACTION,TURN_ENDED};

        public PlayerStatus next()
        {
            return vals[(this.ordinal()+1) % vals.length];//TODO: rivedere flusso perché passi due volte da performing action
        }
    }
}
