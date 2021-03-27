package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Server {

    protected Map<String,GameController> players;

    public GameController getGameController(String username){
        return players.get(username);
    }

    public boolean isValidUsername(String username){
        return listMatches().stream().noneMatch(match-> match.containsUsername(username));
    }

    public void disconnect(String username){
        players.get(username).disconnect(username);
    }

    public List<GameController> listMatches(){
        return players.values().stream().distinct().filter(x->!x.isFull()).collect(Collectors.toList());
    }

    public void addPlayer(String username,GameController match){
        if(match.isFull())
            throw new IllegalStateException("Match full");
        players.put(username,match);
    }

}
