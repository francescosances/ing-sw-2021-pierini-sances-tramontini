package it.polimi.ingsw.network;

import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.view.VirtualView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    /**
     * The socket connection reference
     */
    private final Socket socket;
    /**
     * The server object reference
     */
    private final Server server;

    /**
     * A scanner on the socket input stream
     */
    private final Scanner socketIn;
    /**
     * A printWriter used to write on the socket output stream
     */
    private final PrintWriter socketOut;

    /**
     * The username associated to this ClientHandler
     */
    private String username = null;

    public ClientHandler(Socket socket,Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        socketIn = new Scanner(socket.getInputStream());
        socketOut = new PrintWriter(socket.getOutputStream());
    }

    /**
     * Returns the username associated to this ClientHandler
     * @return the username associated to this ClientHandler
     */
    public String getUsername() { return this.username; }

    /**
     * Set the username associated to this ClientHandler
     * @param username the username to associate to this ClientHandler
     */
    public void setUsername(String username) { this.username = username; }

    @Override
    public void run() {
        try {
            // wait for messages from client
            while(!Thread.currentThread().isInterrupted()){
                String fromClient = socketIn.nextLine();
                if(fromClient == null) {
                    throw new IllegalStateException("Inactive client " + username);
                }
                Message message = Message.messageFromString(fromClient); //Read the new message
                Server.log("Message received from " + username + ":\n" + message.serialize());
                try {
                    server.handleReceivedMessage(message, this); // Forwards the message to the server
                }catch (IllegalStateException | IllegalArgumentException e){
                    new VirtualView(this, null).showErrorMessage(e.getMessage());
                }
            }
            //close connections
            Server.log("Closing connection");
            socketIn.close();
            socketOut.close();
            socket.close();
        } catch (IOException e) {
            Server.log("Received invalid message");
            Server.log(e.getMessage());
        }catch (NoSuchElementException ignored){
        }catch (Exception e){
            e.printStackTrace();
        }finally {
           //Client disconnected
            if (username != null) {
                server.disconnect(username);
                Server.log(username + " disconnected");
            }
        }
        Server.log("ClientHandler closed");
    }

    /**
     * Send the specified message to the client
     * @param message the message to send
     */
    public void sendMessage(Message message) {
        socketOut.println(message.serialize());
        socketOut.flush();
        Server.log("Message sent to client " + username + ":\n" + message.serialize());
    }

}
