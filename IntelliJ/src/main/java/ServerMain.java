import it.polimi.ingsw.network.SocketServer;

import java.io.IOException;

public class ServerMain
{
    public static void main( String[] args ) throws IOException {
        int serverPort = 8000;

        SocketServer socketServer = new SocketServer(serverPort);
        socketServer.startServer();

    }
}
