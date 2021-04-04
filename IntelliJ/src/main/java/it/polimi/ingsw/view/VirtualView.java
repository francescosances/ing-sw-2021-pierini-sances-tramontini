package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.network.ClientHandler;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Triple;

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
    };

    @Override
    public void askLobby(List<Triple<String, Integer, Integer>> availableMatches){
        sendMessage(new Message(/*TODO Messaggio con serializzazione della lista delle partite disponibili */Message.MessageType.LOBBY_INFO, null));
    }

    @Override
    public void resumeMatch(Match match) {
        sendMessage(new Message(/*TODO Messaggio con serializzazione di un oggetto Match */Message.MessageType.MATCH_FULL_STATUS, null));
    }

    @Override
    public void yourTurn() {
        sendMessage(new Message(/*TODO Messaggio che indica che è il tuo turno */null, null));
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
        sendMessage(new Message(/*TODO Messaggio che notifica la connessione di un nuovo utente */null, null));
    }

    @Override
    public void userDisconnected(String username) {
        sendMessage(new Message(/*TODO Messaggio che notifica la disconnessione di un utente */null, null));
    }

}
