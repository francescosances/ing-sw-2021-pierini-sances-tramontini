package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.cli.CLI;

import java.io.IOException;

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
        switch (message.getType()) {
            case GENERIC:
                view.showMessage(message.getData("text"));
            case LOGIN_FAILED:
                view.showMessage("Login failed, try with another username");
                view.askLogin();
            case LOBBY_INFO:
                // TODO
                //  deserialize content of message into List<Triple<String, Integer, Integer>> availableMatches
                //  view.askLobby(availableMatches);
            default:
                client.log("Received unexpected message");
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
}
