package it.polimi.ingsw;

import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.SocketServer;

public class ServerMain
{
    public static void main( String[] args ) {
        int serverPort = 8000;

        for (int i = 0; i < args.length - 1; i++)
            if (args[i].equals("-p") || args[i].equals("--port"))
                try {
                    serverPort = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    Server.log("Invalid port, using default");
                }

        Server server = Server.loadServer();
        SocketServer socketServer = new SocketServer(server, serverPort);

        socketServer.start();
    }
}
