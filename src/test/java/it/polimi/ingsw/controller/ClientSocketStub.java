package it.polimi.ingsw.controller;

import it.polimi.ingsw.network.ClientSocket;
import it.polimi.ingsw.utils.Message;

import java.util.ArrayList;
import java.util.List;

public class ClientSocketStub extends ClientSocket {
    private final List<Message> messages;

    public ClientSocketStub(ClientController clientController) {
        super(clientController);
        messages = new ArrayList<>();
    }
    public boolean isEmpty(){
        return messages.isEmpty();
    }

    @Override
    public void sendMessage(Message message) {
        this.messages.add(message);
    }

    public Message popMessage() {
        return messages.remove(0);
    }
}
