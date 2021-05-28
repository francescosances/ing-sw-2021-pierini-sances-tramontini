package it.polimi.ingsw.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    /**
     * The object containing the socket reference
     */
    private final Server server;

    /**
     * The server port
     */
    private final int port;

    /**
     * Initialize a new server listening on the specified port
     * @param port the port used by the server
     */
    public SocketServer(Server server, int port){
        this.server = server;
        this.port = port;
    }

    /**
     * ClientSocket starts to wait for messages and to assign a ClientHandler
     * to each client joining
     */
    public void start(){
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);
        }catch (IOException e){
            System.err.println(e.getMessage()); //port not available
            return;
        }
        Server.log("Server ready on port " + port);
        while (true){
            try{
                Socket socket = serverSocket.accept();
                Server.log("User connected");
                new Thread(new ClientHandler(socket,server)).start();
            }catch(IOException e){
                e.printStackTrace();
                break; //In case the serverSocket gets closed
            }
        }
        try {
            serverSocket.close();
        }catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
}