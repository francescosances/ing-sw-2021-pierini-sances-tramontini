package it.polimi.ingsw;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.view.gui.JavaFXGui;
import javafx.application.Application;

import java.util.Arrays;

public class ClientMain
{
    public static void main( String[] args )
    {
        if (Arrays.asList(args).contains("-c") || Arrays.asList(args).contains("--cli"))
            new ClientController().startCli();
        else
            Application.launch(JavaFXGui.class);
    }
}
