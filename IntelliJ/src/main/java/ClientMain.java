import it.polimi.ingsw.network.Client;
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

        CLIController cliController = new CLIController(new Client(serverAddress,serverPort));

    }
}
