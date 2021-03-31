package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.view.View;

import java.io.PrintStream;
import java.util.Scanner;

public class CLIController implements View {

    protected GameController gameController;
    private Scanner input;
    private PrintStream output;

    public CLIController(GameController gameController){
        this.gameController = gameController;//TODO: deve passare dal socket
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

    }
}
