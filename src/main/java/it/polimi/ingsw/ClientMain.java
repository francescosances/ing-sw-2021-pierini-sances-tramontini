package it.polimi.ingsw;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.view.gui.JavaFXGui;
import javafx.application.Application;

import java.util.Scanner;

public class ClientMain
{
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select 1 for CLI, 2 for GUI:");
        // start view and show welcome screen
        if(scanner.nextInt() == 1)
            new ClientController().startCli();
        else
            Application.launch(JavaFXGui.class);
    }
}
