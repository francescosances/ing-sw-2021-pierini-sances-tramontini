package it.polimi.ingsw.View.CLI;

import it.polimi.ingsw.Controller.GameController;

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
