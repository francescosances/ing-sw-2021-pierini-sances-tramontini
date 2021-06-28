package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.view.gui.GUI;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientSocket implements Runnable{
    /**
     * The socket connection to the server
     */
    private Socket socket;
    /**
     * A scanner on the socket input stream
     */
    private Scanner socketIn;
    /**
     * A printWriter used to write on the socket output stream
     */
    private PrintWriter socketOut;
    /**
     * The client controller associated
     */
    private final ClientController clientController;

    /**
     * Initialize a new Client with and empty ClientController
     */
    public ClientSocket(ClientController clientController){
        this.clientController = clientController;
    }

    /**
     * Returns the current client controller associated to this socket
     * @return the current client controller associated to this socket
     */
    public ClientController getClientController(){
        return clientController;
    }

    /**
     * Establishes the connection with the server and set the input and output streams
     * @param ip the server ip address
     * @param port the server port
     * @throws IOException if the connection is interrupted
     */
    public void setupSocket(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        System.out.println("Connection established");

        socketIn = new Scanner(socket.getInputStream());
        socketOut = new PrintWriter(socket.getOutputStream());
    }

    /**
     * The method that will be run by a Thread
     * Catches all incoming messages and sends them to the ClientController creating a new thread)
     * so that it can be handled
     */
    @Override
    public void run(){
        while (!Thread.currentThread().isInterrupted()){
            try {
                String received = socketIn.nextLine();
                Message message = Message.messageFromString(received);

                Thread t = new Thread(()-> clientController.handleReceivedMessage(message));
                t.setDaemon(true);
                t.start();
            } catch (Exception e){
                clientController.getView().showErrorMessage("Server closed connection");
                break;
            }
        }
        socketIn.close();
        socketOut.close();
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Unable to close socket:");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Sends a message to the server
     * @param message the message to be sent to the server
     */
    public void sendMessage(Message message) {
        socketOut.println(message.serialize());
        socketOut.flush();
    }
}