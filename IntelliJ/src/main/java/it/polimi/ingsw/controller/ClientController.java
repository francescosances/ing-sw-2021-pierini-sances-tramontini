package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.cli.CLI;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ClientController {
    private final Client client;
    private View view;

    public ClientController(Client client) {
        this.client = client;
    }

    // --- FROM SEVER TO CLIENT (invoked by Client class during initialization or after message from server) ---

    public void startCli() {
        view = new CLI(this);
        view.init();
    }

    public void startGui() {
        // TODO - GUI Controller
        // this.view = new GuiController(this);
        view.init();
    }

    public void handleReceivedMessage(Message message) {
        Gson gson = new Gson();
        switch (message.getType()) {
            case GENERIC:
                view.showMessage(message.getData("text"));
                break;
            case YOUR_TURN:
                view.yourTurn();
                break;
            case LOGIN_FAILED:
                view.showMessage("Login failed, try with another username");
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
                view.listLeaderCards(leaderCardList);
                break;
            default:
                client.log("Received unexpected message");
                client.log(message.serialize());
        }
    }


    // --- FROM CLIENT TO SERVER --- (invoked by View)

    public void connect(String ip, int port) throws IOException {
        client.setupSocket(ip, port);
        new Thread(client).start();
        view.askLogin();
    }

    public void login(String username){
        Message message = new Message(Message.MessageType.LOGIN_REQUEST);
        message.addData("username", username);
        client.sendMessage(message);
    }

    public void lobbyChoice(String matchOwner) {
        Message message = new Message(Message.MessageType.LOBBY_CHOICE);
        message.addData("matchOwner", matchOwner);
        client.sendMessage(message);
    }

    public void leaderCardsChoice(LeaderCard ... leaderCards){
        Message message = new Message(Message.MessageType.LEADER_CARDS_CHOICE);
        message.addData("leaderCards",leaderCards,new Gson());
        client.sendMessage(message);
    }

    public void startMatch(){
        client.sendMessage(new Message(Message.MessageType.START_MATCH));
    }

    public void resumeMatch(Match match){
        throw new IllegalStateException("Not implemented yet");
    }
}
