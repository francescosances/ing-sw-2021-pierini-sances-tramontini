package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.network.ClientHandler;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Triple;

import java.util.Arrays;
import java.util.List;

public class VirtualView implements View {

    private final ClientHandler clientHandler;

    public VirtualView(ClientHandler clientHandler) { this.clientHandler = clientHandler; }

    private void sendMessage(Message message){ clientHandler.sendMessage(message); }

    @Override
    public void showMessage(String message) {
        Message msg = new Message(Message.MessageType.GENERIC);
        msg.addData("text", message);
        sendMessage(msg);
    }

    @Override
    public void showErrorMessage(String message) {
        Message msg = new Message(Message.MessageType.ERROR);
        msg.addData("text", message);
        sendMessage(msg);
    }

    @Override
    public void listLobbies(List<Triple<String, Integer, Integer>> availableLobbies){
        Message message = new Message(Message.MessageType.LOBBY_INFO);
        Gson gson = new Gson();
        message.addData("availableMatches",gson.toJson(availableLobbies));
        sendMessage(message);
    }

    @Override
    public void waitForStart() {
        sendMessage(new Message(Message.MessageType.WAIT_FOR_START));
    }

    @Override
    public void resumeMatch(Match match) {
        Message message = new Message(Message.MessageType.RESUME_MATCH);
        message.addData("match", Serializer.serializeMatchState(match));
        sendMessage(message);
    }

    @Override
    public void yourTurn() {
        sendMessage(new Message(Message.MessageType.YOUR_TURN));
    }

    @Override
    public void init() {
        System.out.println("Initialized virtual view");
    }

    @Override
    public void askLogin() {
        sendMessage(new Message( Message.MessageType.LOGIN_REQUEST));
    }

    @Override
    public void userConnected(String username) {
       showMessage(username+" has joined the lobby");
    }

    @Override
    public void userDisconnected(String username) {
        showMessage(username+" has left the lobby");
    }

    @Override
    public void listLeaderCards(List<LeaderCard> leaderCardList) {
        Gson gson = new Gson();
        Message message = new Message(Message.MessageType.LIST_LEADER_CARDS);
        message.addData("leaderCards",gson.toJson(leaderCardList));
        sendMessage(message);
    }

    @Override
    public void askForAction(Action... availableActions) {
        Gson gson = new Gson();
        Message message = new Message(Message.MessageType.ASK_FOR_ACTION);
        message.addData("availableActions",gson.toJson(Arrays.asList(availableActions)));
        sendMessage(message);
    }


}
