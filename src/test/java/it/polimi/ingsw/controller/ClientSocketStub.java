package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.ClientSocket;
import it.polimi.ingsw.utils.Message;

public class ClientSocketStub extends ClientSocket {
    private Message message;

    public ClientSocketStub(ClientController clientController) {
        super(clientController);
    }

    @Override
    public void sendMessage(Message message) {
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }
}
