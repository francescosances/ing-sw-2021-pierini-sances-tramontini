package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Match;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private Match match;
    private List<PlayerController> players;

    private int currentPlayerIndex;

    public GameController(String matchName){
        match = new Match(matchName);
        players = new ArrayList<>();
    }

    public void start(){
        currentPlayerIndex = (int) (Math.random() * players.size());
        //players.forEach(PlayerController::drawLeaderCards);
    }

    public void nextTurn(){
        currentPlayerIndex = (currentPlayerIndex+1)%players.size();
        if(!players.get(currentPlayerIndex).isActive()) //TODO: controllare che non siano tutti disconnessi
            nextTurn();

        //TODO: manda messaggio "è il tuo turno" al giocatore attivo
    }

    public PlayerController addPlayer(String username){
        PlayerController playerController = new PlayerController(username,match.addPlayer(username));
        players.add(playerController);
        return playerController;
    }

    public boolean containsUsername(String username){
        return match.containsUsername(username);
    }

    public boolean isConnected(String username){
        return players.stream().filter(x->x.getUsername().equals(username)).anyMatch(PlayerController::isActive);
    }

    public String getMatchName(){
        return match.getMatchName();
    }

    public void connect(String username){
        players.forEach((user)->{
            if(user.getUsername().equals(username)) {
                user.activate();
            }
        });
    }

    public void disconnect(String username){
        players.forEach((user)->{
            if(user.getUsername().equals(username)) {
                user.deactivate();
            }
        });
    }

    public boolean isFull(){
        return match.isFull();
    }
}
