package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.controller.GameController;

import java.io.PrintStream;
import java.util.Scanner;

public class CLIController {

    protected GameController gameController;
    private Scanner input;
    private PrintStream output;

    public CLIController(GameController gameController){
        this.gameController = gameController;//TODO: deve passare dal socket
        this.input = new Scanner(System.in);
        this.output = System.out;
    }

    public void start(){
        output.println("BENVENUTO");

    }


}
