package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.controller.PlayerController;
import it.polimi.ingsw.controller.StatusObserver;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.utils.FileManager;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.util.*;
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
    private Server() {
        players = new HashMap<>();
    }

    /**
     * Creates a new Server Object and loads the games suspended and stored
     * @return an instance of the server
     */
    public static Server loadServer(){
        Server ret = new Server();
        try {
            Map<String,List<String>> matchesList = FileManager.getInstance().readMatchesList();
            for(Map.Entry<String,List<String>> entry : matchesList.entrySet()){
                try {
                    Match match = FileManager.getInstance().readMatchStatus(entry.getKey());
                    GameController gameController = GameController.regenerateController(match,ret,entry.getValue());
                    for (String username : entry.getValue()) {
                        gameController.getPlayerController(username).deactivate();
                        ret.players.put(username, gameController);
                    }
                }catch (Exception ignored){
                    ignored.printStackTrace();
                    log("Unable to load match: "+entry.getKey()+" from local disk");
                }
            }
        }catch (Exception ignored){
            log("Unable to load matches from local disk");
        }
        return ret;
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

    /**
     * updates the matches list both locally and on disk
     */
    public void updateMatchesList(){
        try {
            Map<String,List<String>> newMap = new HashMap<>();
            players.forEach((key, value) -> {
                if(value == null)return;
                newMap.putIfAbsent(value.getMatchName(),new ArrayList<>());
                newMap.get(value.getMatchName()).add(key);
            });
            FileManager.getInstance().writeMatchesList(newMap);
        } catch (IOException e) {
            log("Unable to locally save the matches list");
        }
    }

    /**
     * Updates GameController's match's save or deletes it in case it ended
     * @param gameController the match that needs to be updated or deleted
     */
    @Override
    public void onStatusChanged(GameController gameController) {
        if(gameController.getMatch().getCurrentPhase() == Match.GamePhase.END_GAME){
            for(PlayerController playerController: gameController.getPlayers())
                players.remove(playerController.getUsername());
            FileManager.getInstance().deleteMatch(gameController.getMatch().getMatchName());
        }else {
            try {
                FileManager.getInstance().writeMatchStatus(gameController.getMatch());
            } catch (IOException e) {
                log("Unable to locally save the match status of " + gameController.getMatchName());
            }
        }
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
        updateMatchesList();
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
            case LOBBY_INFO:
                listLobbies(clientHandler);
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

    /**
     * Makes the player join the lobby if possible
     * @param matchName the match the player wants to join in
     * @param clientHandler the ClientHandler of the player who wants to join in the match
     */
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
        playerController.setVirtualView(new VirtualView(clientHandler)); //Sets the virtual view with the new clientHandler reference
        playerController.activate();
        getGameController(username).resumeMatch(username);
        if(getGameController(username).isSuspended()) {
            if (getGameController(username).getPlayers().stream().filter(PlayerController::isActive).count() == getGameController(username).getTotalPlayers()) {
                getGameController(username).start();
            }
        }
    }

    /**
     * Mark the user as inactive
     * @param username the username of the disconnected user
     */
    public synchronized void disconnect(String username){
        if (players.get(username) != null)
            players.get(username).disconnect(username);
        else //player didn't even joined a lobby
            players.remove(username);
    }

    /**
     * Logs messages
     * @param msg the message to log
     */
    public static void log(String msg) {
        System.out.println(ANSI_BLUE+"Logger: "+msg+ANSI_RESET);
    }

}
