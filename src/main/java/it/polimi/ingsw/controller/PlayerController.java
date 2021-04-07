package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.view.VirtualView;

import java.util.List;

public class PlayerController {

    protected String username;
    protected PlayerBoard playerBoard;
    protected boolean active;
    private VirtualView virtualView;

    private PlayerStatus currentStatus;

    public PlayerController(String username,PlayerBoard playerBoard,VirtualView virtualView){
        this.username = username;
        this.playerBoard = playerBoard;
        this.active = true;
        this.virtualView = virtualView;
        this.currentStatus = PlayerStatus.WAITING;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate(){
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public String getUsername(){
        return username;
    }

    public void drawLeaderCards(){
        List<LeaderCard> leaderCardList = playerBoard.getMatch().drawLeaderCards(4);
        if(isActive()){
            virtualView.listLeaderCards(leaderCardList);
        }else {
            chooseLeaderCards(leaderCardList.get(0),leaderCardList.get(1));
            //TODO: ricontrollare riconnessione utente
        }
    }

    public void chooseLeaderCards(LeaderCard ... cards){
        for (LeaderCard card : cards) {
            playerBoard.addLeaderCard(card);
        }
    }

    public void yourTurn(){
        this.currentStatus = PlayerStatus.YOUR_TURN;
        virtualView.yourTurn();
    }

    public void turnEnded(){
        this.currentStatus = PlayerStatus.WAITING;
    }

    public VirtualView getVirtualView(){
        return virtualView;
    }

    public void setVirtualView(VirtualView virtualView){
        this.virtualView = virtualView;
    }

    public PlayerStatus getCurrentStatus() {
        return currentStatus;
    }

    public enum PlayerStatus {
        YOUR_TURN,WAITING;

        private static final PlayerStatus[] vals = values();

        public PlayerStatus next()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }
}
