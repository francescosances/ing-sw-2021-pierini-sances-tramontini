package it.polimi.ingsw.network;

import com.google.gson.Gson;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.StatusObserver;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.VirtualView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static it.polimi.ingsw.view.cli.CLI.ANSI_BLUE;
import static it.polimi.ingsw.view.cli.CLI.ANSI_RESET;

public class Server implements StatusObserver {

    /**
     * The players connected to the server.
     * The map associate each username to the relative GameController.
     * If a user has not joined yet a match, his GameController is null
     */
    private final Map<String,GameController> players;

    /**
     * Empty constructor that initializes an empty list of players
     */
    public Server() {
        players = new HashMap<>();
    }

    /**
     * Returns the GameController associated to the specified user
     * @param username the username of the user
     * @return the GameController associated to the user
     */
    public GameController getGameController(String username){ return players.get(username); }

    /**
     * List the matches that the user can join
     * @return a list of game controllers relating to matches that the user can join
     */
    public synchronized List<GameController> getAvailableMatches(){
        return players.values().stream()
                .filter(Objects::nonNull)
                .filter(x->!x.isFull())
                .filter(x->!x.isStarted())
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void onStatusChanged(GameController gameController) {

        System.out.println("Serializzo ");
        try {
            System.out.println(Serializer.serializeMatchState(gameController.getMatch()));
        }catch (Exception e){
            e.printStackTrace();
        }
       /* Message message = new Message(Message.MessageType.RESUME_MATCH);
        message.addData("match",Serializer.serializeMatchState(gameController.getMatch()));
        message.addData("matchStarted",String.valueOf(gameController.isMatchStarted()));
        message.addData("currentPlayerIndex",String.valueOf(gameController.getCurrentPlayerIndex()));
        message.addData("gamePhase", String.valueOf(gameController.getCurrentPhase()));
        System.out.println(message.serialize());*/
    }

    /**
     * Add a new player to the specified match
     * @param username the username of the user to be added
     * @param gameController the game controller of the match chosen
     * @param clientHandler the clientHandler that manages the socket connection with the client
     * @throws IllegalStateException if the chosen match is full
     */
    public synchronized void addPlayerToMatch(String username,GameController gameController,ClientHandler clientHandler){
        if(gameController.isFull())
            throw new IllegalStateException("Match full");
        players.put(username,gameController);
        gameController.addPlayer(username,clientHandler);
        if(gameController.isFull())
            gameController.start();
    }

    /**
     * Method that map a message and a ClientHandler with the the action that must be executed
     * @param message the message received from the client
     * @param clientHandler the ClientHandler that manages the socket connection with the client
     */
    public void handleReceivedMessage(Message message, ClientHandler clientHandler) {
        switch (message.getType()){
            case LOGIN_REQUEST:
                login(message.getData("username"), clientHandler);
                break;
            case LOBBY_CHOICE:
                if(message.getData("matchOwner") == null)
                    createNewLobby(Integer.parseInt(message.getData("playersNumber")),clientHandler);
                else
                    joinLobby(message.getData("matchOwner"), clientHandler);
                getGameController(clientHandler.getUsername()).connect(clientHandler.getUsername());
                break;
            default:
                getGameController(clientHandler.getUsername()).handleReceivedGameMessage(message,clientHandler.getUsername());
        }
    }

    /**
     * Accept a new user to the server.
     * If the username is already taken by another user, the login will fail.
     * If the user was already known, he will be reconnected.
     * @param newUsername the username of the new user
     * @param clientHandler the ClientHandler that manages the socket connection with the client
     */
    private synchronized void login(String newUsername, ClientHandler clientHandler) {
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

    /**
     * Returns true if the given username is available, false if the chosen username has already been taken by another user
     * @param username the username to check
     * @return true if the given username is available, false if the chosen username has already been taken by another user
     */
    private synchronized boolean isAvailableUsername(String username) {
        return !players.containsKey(username);
    }

    /**
     * Send a list of lobbies that a new user can join to the ClientHandler through a new virtualView specially created
     * @param clientHandler the ClientHandler that manages the socket connection with the client
     */
    private synchronized void listLobbies(ClientHandler clientHandler) {
            View view = new VirtualView(clientHandler);
            // send the user a list of available matches (with num of joined players and size of the match)
            List<Triple<String, Integer, Integer>> availableMatches = getAvailableMatches().stream()
                    .map(match -> new Triple<>(match.getMatchName(), match.getJoinedPlayers(), match.getTotalPlayers()))
                    .collect(Collectors.toList());
            view.listLobbies(availableMatches);
    }

    /**
     * Associate the user to the chosen lobby. It also connects the user to the realative game controller or crete a new one.
     * The name of the new match is the username of the current user
     * @param playersNumber the number of players that can join the match
     * @param clientHandler the ClientHandler that manages the socket connection with the client
     */
    private synchronized void createNewLobby(int playersNumber, ClientHandler clientHandler) {
        if(playersNumber > Match.MAX_PLAYERS || playersNumber <= 0)
            new VirtualView(clientHandler).showErrorMessage("Invalid choice");
        else {
            GameController match = new GameController(clientHandler.getUsername(), playersNumber,this);
            addPlayerToMatch(clientHandler.getUsername(), match, clientHandler);
        }
    }

    private synchronized void joinLobby(String matchName, ClientHandler clientHandler){
        View view = new VirtualView(clientHandler);
        if (getGameController(matchName) == null || getGameController(matchName).isFull()){
            view.showMessage("Selected match is full or does not exist");
            listLobbies(clientHandler);
        } else {
            GameController gameController = getGameController(matchName);
            addPlayerToMatch(clientHandler.getUsername(), gameController,clientHandler);
        }
    }

    /**
     * Returns true if the user has been disconnected, false if the user is online
     * @param username the username of the user to check
     * @return true if the user has been disconnected, false if the user is online
     */
    private synchronized boolean isDisconnected(String username) {
        return players.containsKey(username)
                && players.get(username) != null
                && !players.get(username).isConnected(username);
    }

    /**
     * Marks the username as chosen and sends the list of available lobbies to the ClientHandler.
     * The username is added to the players list with a null GameController
     * @param username the username to add
     * @param clientHandler the ClientHandler that manages the socket connection with the client
     */
    private synchronized void connect(String username, ClientHandler clientHandler) {
        players.put(username, null);
        listLobbies(clientHandler);
    }

    /**
     * Marks as active an user that was disconnected.
     * Associates a new virtual view to his PlayerController and send him the current state of the match to resume it.
     * @param username the username of the reconnected user
     * @param clientHandler the ClientHandler that manages the socket connection with the client
     */
    private synchronized void reconnect(String username, ClientHandler clientHandler) {
        PlayerController playerController = getGameController(username).getPlayerController(username);
        playerController.setVirtualView(new VirtualView(clientHandler)); //Set the virtual view with the new clientHandler reference
        playerController.activate();
        playerController.getVirtualView().resumeMatch(getGameController(username).getMatch());
    }

    /**
     * Mark the user as inactive
     * @param username the username of the disconnected user
     */
    public synchronized void disconnect(String username){
        players.get(username).disconnect(username);
    }

    /**
     * Logs messages
     * @param msg the message to log
     */
    public void log(String msg) {
        System.out.println(ANSI_BLUE+"Logger: "+msg+ANSI_RESET);
    }

}
