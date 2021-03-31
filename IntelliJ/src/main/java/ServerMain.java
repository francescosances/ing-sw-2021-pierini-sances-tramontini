import it.polimi.ingsw.network.Client;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.SocketServer;
import it.polimi.ingsw.view.cli.CLIController;

import java.io.IOException;
import java.util.Scanner;

public class ServerMain
{
    public static void main( String[] args ) throws IOException {
        int serverPort = 8000;

        SocketServer socketServer = new SocketServer(serverPort);
        socketServer.startServer();

    }
}
