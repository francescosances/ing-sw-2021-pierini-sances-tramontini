package it.polimi.ingsw.network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.VirtualView;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Server {

    private final Map<String,GameController> players;

    public Server() {
        players = new HashMap<>();
    }

    public GameController getGameController(String username){ return players.get(username); }

    public List<GameController> getAvailableMatches(){
        return players.values().stream()
                .filter(Objects::nonNull)
                .filter(x->!x.isFull())
                .distinct()
                .collect(Collectors.toList());
    }

    public void addPlayerToMatch(String username,GameController gameController,ClientHandler clientHandler){
        if(gameController.isFull())
            throw new IllegalStateException("Match full");
        players.put(username,gameController);
        gameController.addPlayer(username,clientHandler);
    }

    // --- communication with client ---

    public void handleReceivedMessage(Message message, ClientHandler clientHandler) {
        Gson gson = new Gson();
        switch (message.getType()){
            case LOGIN_REQUEST:
                login(message.getData("username"), clientHandler);
                break;
            case LOBBY_CHOICE:
                lobbyChoice(message.getData("matchOwner"), clientHandler);
                break;
            case START_MATCH:
                getGameController(clientHandler.getUsername()).start();
                break;
            case LEADER_CARDS_CHOICE:
                Type listType = new TypeToken<List<LeaderCard>>(){}.getType();
                List<LeaderCard> leaderCards = gson.fromJson(message.getData("leaderCards"),listType);
                System.out.println("Scelta carte leader");
                System.out.println(leaderCards);
                getGameController(clientHandler.getUsername()).getPlayerController(clientHandler.getUsername()).chooseLeaderCards(leaderCards.toArray(new LeaderCard[]{}));
                break;
            default:
                System.out.println("DEFAULT handleReceivedMessage");
                // TODO forward message to playerController / messageReader
        }
    }

    private void login(String newUsername, ClientHandler clientHandler) {
        log("Received login request from " + newUsername);
        if (isAvailableUsername(newUsername)){
            // connect
            log(newUsername + " logging in");
            clientHandler.setUsername(newUsername);
            connect(newUsername, clientHandler);
        } else if (isDisconnected(newUsername)) {
            // reconnect
            log("Reconnecting " + newUsername);
            clientHandler.setUsername(newUsername);
            reconnect(newUsername, clientHandler);
        } else {
            // login failed
            clientHandler.sendMessage(new Message(Message.MessageType.LOGIN_FAILED));
            log("Failed login attempt from new client, username " + newUsername + " already taken");
        }
    }

    private boolean isAvailableUsername(String username) {
        return !players.containsKey(username);
    }

    private boolean isDisconnected(String username) {
        return players.containsKey(username)
                && players.get(username) != null
                && !players.get(username).isConnected(username);
    }

    private void connect(String username, ClientHandler clientHandler) {
        players.put(username, null);
        listLobbies(clientHandler);
    }

    private void listLobbies(ClientHandler clientHandler) {
            View view = new VirtualView(clientHandler);
            // send the user a list of available matches (with num of joined players and size of the match)
            List<Triple<String, Integer, Integer>> availableMatches = getAvailableMatches().stream()
                    .map(match -> new Triple<>(match.getMatchName(), match.getJoinedPlayers(), match.getTotalPlayers()))
                    .collect(Collectors.toList());
            view.listLobbies(availableMatches);
    }

    private void lobbyChoice(String matchOwner, ClientHandler clientHandler) {
        if (matchOwner == null){
            // new match
            GameController match = new GameController(clientHandler.getUsername());
            addPlayerToMatch(clientHandler.getUsername(), match, clientHandler);
            System.out.println("Scelta una lobby vuota");
            new VirtualView(clientHandler).waitForStart();
        } else {
            // join existing match
            View view = new VirtualView(clientHandler);
            if (getGameController(matchOwner) == null || getGameController(matchOwner).isFull()){
                view.showMessage("Selected match is full or does not exist");
                listLobbies(clientHandler);
            } else {
                GameController gameController = getGameController(matchOwner);
                addPlayerToMatch(clientHandler.getUsername(), gameController,clientHandler);
                if(gameController.isFull())
                    gameController.start();
            }
        }
    }

    private void reconnect(String username, ClientHandler clientHandler) {
        PlayerController playerController = getGameController(username).getPlayerController(username);
        playerController.setVirtualView(new VirtualView(clientHandler));
        playerController.activate();
        playerController.getVirtualView().resumeMatch(getGameController(username).getMatch());
    }

    public void disconnect(String username){
        players.get(username).disconnect(username);
    }

    // logger
    public void log(String msg) { System.out.println(msg); }

}
