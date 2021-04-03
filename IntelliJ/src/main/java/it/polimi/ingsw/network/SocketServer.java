package it.polimi.ingsw.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private final int port;
    private final Server server;

    public SocketServer(int port){
        this.server = new Server();
        this.port = port;
    }

    public void start() throws IOException {
        //It creates threads when necessary, otherwise it re-uses existing one when possible
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);
        }catch (IOException e){
            System.err.println(e.getMessage()); //port not available
            return;
        }
        server.log("Server ready on port " + port);
        while (true){
            try{
                Socket socket = serverSocket.accept();
                server.log("User connected");
                executor.submit(new ClientHandler(socket,server));
            }catch(IOException e){
                e.printStackTrace();
                break; //In case the serverSocket gets closed
            }
        }
        executor.shutdown();
        serverSocket.close();
    }

}