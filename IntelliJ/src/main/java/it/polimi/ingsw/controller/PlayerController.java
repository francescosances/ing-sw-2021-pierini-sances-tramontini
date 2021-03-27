package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.LeaderCard;
import it.polimi.ingsw.model.PlayerBoard;

import java.util.List;

public class PlayerController {

    protected String username;
    protected PlayerBoard playerBoard;
    protected boolean active;

    private List<LeaderCard> leaderCardList;

    public PlayerController(String username,PlayerBoard playerBoard){
        this.username = username;
        this.playerBoard = playerBoard;
        this.active = true;
    }

    public boolean isActive() {
        return active;
    }

    public String getUsername(){
        return username;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate(){
        this.active = false;
    }

    public void drawLeaderCards(){
        leaderCardList = playerBoard.getMatch().drawLeaderCards(4);
        if(isActive()){
            System.out.println(leaderCardList);
            System.out.println("qui");
            //TODO: invia messaggio scegli carta
        }else {
            chooseLeaderCards(0,1);
        }
    }

    public void chooseLeaderCards(int ... cardsIndexes){
        for (int cardsIndex : cardsIndexes) {
            playerBoard.addLeaderCard(this.leaderCardList.get(cardsIndex));
        }
    }
}
