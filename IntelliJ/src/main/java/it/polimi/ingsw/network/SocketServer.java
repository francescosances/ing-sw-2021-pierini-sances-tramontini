package it.polimi.ingsw.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private int port;

    protected Server server;

    public SocketServer(int port){
        this.port = port;
        this.server = new Server();
    }

    public void startServer() throws IOException {
        //It creates threads when necessary, otherwise it re-uses existing one when possible
        ExecutorService executor = Executors.newCachedThreadPool();
        ServerSocket serverSocket;
        try{
            serverSocket = new ServerSocket(port);
        }catch (IOException e){
            System.err.println(e.getMessage()); //port not available
            return;
        }
        System.out.println("Server ready on port "+port);
        while (true){
            try{
                Socket socket = serverSocket.accept();
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