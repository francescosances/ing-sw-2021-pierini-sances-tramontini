package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.ClientHandler;
import it.polimi.ingsw.view.VirtualView;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GameController {

    private final Match match;
    private final List<PlayerController> players;

    private int currentPlayerIndex;
    private GameStatus currentStatus;

    public GameController(String matchName){
        match = new Match(matchName);
        players = new ArrayList<>();
        currentStatus = GameStatus.ADDING_PLAYERS;
    }

    public void start(){
        currentPlayerIndex = (int) (Math.random() * players.size());
        currentStatus = currentStatus.next();
        //players.forEach(PlayerController::drawLeaderCards);
        System.out.println("PARTITA AVVIATA");
    }

    public void nextTurn(){
        players.get(currentPlayerIndex).turnEnded();
        currentPlayerIndex = (currentPlayerIndex+1)%players.size();
        if(players.stream().noneMatch(PlayerController::isActive))//No one is active
            return;
        if(!players.get(currentPlayerIndex).isActive())
            nextTurn();
        players.get(currentPlayerIndex++).yourTurn();
    }

    public PlayerController addPlayer(String username, ClientHandler clientHandler){
        PlayerController playerController = getPlayerController(username);
        if(playerController != null){
            //Reactivating existing player
            playerController.setVirtualView(new VirtualView(clientHandler));
            playerController.activate();
            return playerController;
        }else{
            playerController = new PlayerController(username, match.addPlayer(username), new VirtualView(clientHandler));
            players.add(playerController);
            return playerController;
        }
    }

    public PlayerController getPlayerController(String username){
        for(PlayerController x : players)
            if(x.getUsername().equals(username))
                return x;
        return null;
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
            user.getVirtualView().userConnected(username);
        });
    }

    public void disconnect(String username){
        players.forEach((user)->{
            if(user.getUsername().equals(username)) {
                user.deactivate();
            }
            user.getVirtualView().userDisconnected(username);
        });
    }

    public int getJoinedPlayers(){
        return players.size();
    }

    public int getTotalPlayers(){
        return Match.MAX_PLAYERS;
    }

    public boolean isFull(){
        return match.isFull();
    }

    public Match getMatch(){
        return match;
    }

    public enum GameStatus {
        ADDING_PLAYERS,SETUP;

        private static GameStatus[] vals = values();

        public GameStatus next()
        {
            return vals[(this.ordinal()+1) % vals.length];
        }
    }
}
