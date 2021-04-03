import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.cli.CLIController;

import java.util.Scanner;

public class ClientMain
{
    public static void main( String[] args )
    {
        // client, clientController and cli are created
        Client client = new Client();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select 1 for CLI, 2 for GUI:");

        // start view and show welcome screen
        if(scanner.nextInt() == 1)
            client.startCli();
        else
            client.startGui();
    }
}
