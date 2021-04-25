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
                Message message = Message.messageFromString(socketIn.nextLine()); //Read the new message
                server.log("Message received from " + username);
                server.log(message.serialize());
                try {
                    server.handleReceivedMessage(message, this); // Forward the message to the server
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
        }catch (NoSuchElementException ignored){
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

    /**
     * Send the specified message to the client
     * @param message the message to send
     */
    public void sendMessage(Message message) {
        socketOut.println(message.serialize());
        socketOut.flush();
        server.log("Message sent to client:\n"+message.serialize());
    }

}
