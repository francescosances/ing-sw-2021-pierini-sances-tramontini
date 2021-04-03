import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.cli.CLIController;

import java.util.Scanner;

public class ClientMain
{
    public static void main( String[] args )
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Insert server address:");
        String serverAddress = scanner.next();
        System.out.println("Insert server port:");
        int serverPort = scanner.nextInt();

        Client client = new Client(serverAddress,serverPort);

        System.out.println("Select 1 for CLI, 2 for GUI:");
        View controller;
        if(scanner.nextInt() == 1)
            controller = new CLIController(client);
        else
            controller = null;//new GUI Controller


    }
}
