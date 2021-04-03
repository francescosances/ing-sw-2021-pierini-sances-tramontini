package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.view.View;

import java.io.PrintStream;
import java.util.Scanner;

public class CLIController implements View {

    protected Client client;
    private Scanner input;
    private PrintStream output;

    public CLIController(Client client){
        this.client = client;//TODO: deve passare dal socket
        this.input = new Scanner(System.in);
        this.output = System.out;
    }

    @Override
    public void resumeMatch(Match match) {

    }

    @Override
    public void yourTurn() {
        System.out.println("It's your turn");
    }

    @Override
    public void userConnected(String username) {
        System.out.println(username+" has joined the match");
    }

    @Override
    public void userDisconnected(String username) {
        System.err.println(username+" has been disconnected");
    }

    @Override
    public void askUsername() {
        System.out.println("Insert username");
        String username = input.next();
        //TODO: codificare messaggio di risposta al server con l'username
        output.println("messaggio con username"+username);
    }
}
