package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.cli.CLI;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClientController {
    /**
     * The socket connection to the server
     */
    private final Client client;

    /**
     * The view used to interact with the user
     */
    private View view;

    /**
     * Initialize a new ClientController connected to the server through the specified Client object
     * @param client the socket connection reference
     */
    public ClientController(Client client) {
        this.client = client;
    }

    /**
     * Set the view to Command Line Interface and launch it
     */
    public void startCli() {
        view = new CLI(this);
        view.init();
    }

    /**
     * Sets the view to Graphical User Interface and launch it
     */
    public void startGui() {
        // TODO - GUI Controller
        // this.view = new GuiController(this);
        view.init();
    }

    /**
     * Method that map a message with the the action that must be executed
     * @param message the message received from the server via socket
     */
    public void handleReceivedMessage(Message message) {
        Gson gson = new Gson();
        switch (message.getType()) {
            case GENERIC:
                view.showMessage(message.getData("text"));
                break;
            case ERROR:
                view.showErrorMessage(message.getData("text"));
                break;
            case YOUR_TURN:
                view.yourTurn();
                break;
            case LOGIN_FAILED:
                view.showErrorMessage("Login failed, try with another username");
                view.askLogin();
                break;
            case LOBBY_INFO:
                String availableMessages = message.getData("availableMatches");
                Type listType = new TypeToken<List<Triple<String,Integer,Integer>>>(){}.getType();
                List<Triple<String, Integer, Integer>> matches = gson.fromJson(availableMessages, listType);
                view.listLobbies(matches);
                break;
            case WAIT_FOR_START:
                view.waitForStart();
                break;
            case RESUME_MATCH:
                resumeMatch(Serializer.deserializeMatchState(message.getData("match")));
                break;
            case LIST_LEADER_CARDS:
                List<LeaderCard> leaderCardList = Serializer.deserializeLeaderCards(message.getData("leaderCards"));
                view.listLeaderCards(leaderCardList,Integer.parseInt(message.getData("cardsToChoice")));
                break;
            case ASK_FOR_ACTION:
                List<Action> actions = gson.fromJson(message.getData("availableActions"),new TypeToken<List<Action>>(){}.getType());
                view.askForAction(actions.toArray(new Action[0]));
                break;
            case SHOW_WAREHOUSE_STATUS:
                Warehouse warehouse = Serializer.deserializeWarehouse(message.getData("warehouse"));
                view.showWarehouseStatus(warehouse);
                break;
            default:
                client.log("Received unexpected message");
                client.log(message.serialize());
        }
    }

    /**
     * Connects the socket to server and ask the user to login
     * @param ip the ip address of the server
     * @param port the port of the server
     * @throws IOException if the connection is interrupted
     */
    public void connect(String ip, int port) throws IOException {
        client.setupSocket(ip, port);
        new Thread(client).start();
        view.askLogin();
    }

    /**
     * Sends to the server a login request
     * @param username the username chosen to login
     */
    public void login(String username){
        Message message = new Message(Message.MessageType.LOGIN_REQUEST);
        message.addData("username", username);
        client.sendMessage(message);
    }

    /**
     * Sends to the server the lobby chosen by the user
     * @param matchName the name of the match chosen by the user
     */
    public void lobbyChoice(String matchName) {
        Message message = new Message(Message.MessageType.LOBBY_CHOICE);
        message.addData("matchOwner", matchName);
        client.sendMessage(message);
    }

    /**
     * Sends to the server the message that communicate that the match can start
     */
    public void startMatch(){
        client.sendMessage(new Message(Message.MessageType.START_MATCH));
    }

    /**
     * Sends to the server the array of leader cards chosen by the user
     * @param leaderCards the array of leader cards chosen by the user
     */
    public void leaderCardsChoice(LeaderCard ... leaderCards){
        Message message = new Message(Message.MessageType.LEADER_CARDS_CHOICE);
        message.addData("leaderCards",leaderCards,new Gson());
        client.sendMessage(message);
    }

    /**
     * Sends to the server the action chosen by the user so that it can be performed
     * @param action the action to start
     */
    public void performAction(Action action){
        Gson gson = new Gson();
        Message message = new Message(Message.MessageType.PERFORM_ACTION);
        message.addData("action",gson.toJson(action));
        client.sendMessage(message);
    }

    /**
     * Resumes a match suspended after a network disconnection
     * @param match the match to be resumed
     */
    public void resumeMatch(Match match){
        throw new IllegalStateException("Not implemented yet");
    }
}
