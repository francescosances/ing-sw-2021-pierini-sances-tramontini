package it.polimi.ingsw.network;

import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final Server server;

    private final Scanner socketIn;
    private final PrintWriter socketOut;

    private String username = null;

    public ClientHandler(Socket socket,Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        socketIn = new Scanner(socket.getInputStream());
        socketOut = new PrintWriter(socket.getOutputStream());
    }

    public String getUsername() { return this.username; }

    public void setUsername(String username) { this.username = username; }

    @Override
    public void run() {
        try {
            // wait for messages from client
            while(!Thread.currentThread().isInterrupted()){
                Message message = Message.messageFromString(socketIn.nextLine());
                server.log("Message received from " + username);
                server.log(message.serialize());
                try {
                    server.handleReceivedMessage(message, this);
                }catch (IllegalStateException | IllegalArgumentException e){
                    new VirtualView(this).showErrorMessage(e.getMessage());
                }
            }
            //close connections
            server.log("Closing connection");
            socketIn.close();
            socketOut.close();
            socket.close();
        } catch (IOException e) {
            server.log("Received invalid message");
            server.log(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }finally {
           //Client disconnected
            if (username != null) {
                server.disconnect(username);
                server.log(username + " disconnected");
            }
        }
        server.log("ClientHandler closed");
    }

    public void sendMessage(Message message) {
        socketOut.println(message.serialize());
        socketOut.flush();
        server.log("Message sent to client:\n"+message.serialize());
    }

}
