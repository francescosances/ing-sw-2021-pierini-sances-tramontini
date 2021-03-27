package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Match;

public class GameController {

    private Match match;

    public GameController(){
        match = new Match();
    }

    public void addPlayer(String username){
        match.addPlayer(username);
    }

    public void start(){

    }

}
