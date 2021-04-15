import it.polimi.ingsw.network.ClientSocket;

import java.util.Scanner;

public class ClientMain
{
    public static void main( String[] args )
    {
        // client, clientController and cli are created
        ClientSocket clientSocket = new ClientSocket();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Select 1 for CLI, 2 for GUI:");

        // start view and show welcome screen
        if(scanner.nextInt() == 1)
            clientSocket.startCli();
        else
            clientSocket.startGui();
    }
}
