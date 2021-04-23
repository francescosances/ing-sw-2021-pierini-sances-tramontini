package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.utils.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static it.polimi.ingsw.view.cli.CLI.ANSI_BLUE;
import static it.polimi.ingsw.view.cli.CLI.ANSI_RESET;

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
    public ClientSocket(){
        this.clientController = new ClientController(this);
    }

    /**
     * Set the view to Command Line Interface and launch it via the controller
     */
    public void startCli() {
        clientController.startCli();
    }

    /**
     * Set the view to Graphical User Interface and launch it via the controller
     */
    public void startGui() {
        clientController.startGui();
    }

    /**
     * Establishes the connection with the server and set the input and output streams
     * @param ip the server ip address
     * @param port the server port
     * @throws IOException if the connection is interrupted
     */
    public void setupSocket(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        log("Connection established");

        socketIn = new Scanner(socket.getInputStream());
        socketOut = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run(){
        while (!Thread.currentThread().isInterrupted()){
            String received = socketIn.nextLine();
            log("ricevo"+received);
            Message message = Message.messageFromString(received);
            clientController.handleReceivedMessage(message);
        }
        socketIn.close();
        socketOut.close();
        try {
            socket.close();
        } catch (IOException e) {
            log("Unable to close socket:");
            log(e.getMessage());
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

    /**
     * Logs messages
     * @param msg the message to be logged
     */
    public void log(String msg) {
        System.out.println(ANSI_BLUE+"Logger: "+msg+ANSI_RESET);
    }

}