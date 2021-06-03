package it.polimi.ingsw;

import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.SocketServer;

import java.io.IOException;

public class ServerMain
{
    public static void main( String[] args ) throws IOException {
        int serverPort = 8000;

        Server server = Server.loadServer();
        SocketServer socketServer = new SocketServer(server, serverPort);

        socketServer.start();
    }
}
