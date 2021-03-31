package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Match;

import java.io.PrintWriter;

public class VirtualView implements View{

    protected PrintWriter out;

    public VirtualView(PrintWriter out){
        this.out = out;
    }

    @Override
    public void resumeMatch(Match match) {
        out.println("Messaggio con serializzazione di un oggetto Match");
    }

    @Override
    public void yourTurn() {
        out.println("Messaggio che indica che è il tuo turno");
    }

    @Override
    public void userConnected(String username) {
        out.println("Messaggio che notifica la connessione di un nuovo utente");
    }

    @Override
    public void userDisconnected(String username) {

    }

    @Override
    public void askUsername() {

    }
}
