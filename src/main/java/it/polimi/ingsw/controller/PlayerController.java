package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.view.VirtualView;

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
     * Rapresent the current status of the player
     */
    private PlayerStatus currentStatus;

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
        this.currentStatus = PlayerStatus.WAITING;
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
     * Get 4 leader cards from the match and send it to the client through the virtual view.
     * If the user is inactive, the cards are randomly chosen
     */
    public void listLeaderCards(){
        List<LeaderCard> leaderCardList = playerBoard.getMatch().drawLeaderCards(4);
        if(isActive()){
            virtualView.listLeaderCards(leaderCardList);
        }else {
            chooseLeaderCards(leaderCardList.get(0),leaderCardList.get(1));
            //TODO: ricontrollare riconnessione utente
        }
    }

    /**
     * Puts the given leader cards into the player's hand
     * @param cards the cards to keep in the hand
     */
    public void chooseLeaderCards(LeaderCard ... cards){
        for (LeaderCard card : cards) {
            playerBoard.addLeaderCard(card);
        }
    }

    /**
     * Mark the player as currently playing and notify the status update to the virtual view
     */
    public void yourTurn(){
        this.currentStatus = PlayerStatus.YOUR_TURN;
        virtualView.yourTurn();
    }

    /**
     * Mark the player as waiting for others and notify the status update to the virtual view
     */
    public void turnEnded(){
        this.currentStatus = PlayerStatus.WAITING;
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


    public enum PlayerStatus {
        YOUR_TURN,
        WAITING;

        private static final PlayerStatus[] vals = values();

        public PlayerStatus next()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }
}
