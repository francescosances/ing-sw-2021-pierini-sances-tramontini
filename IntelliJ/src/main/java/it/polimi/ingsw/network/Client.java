package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.utils.Message;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable{
    private Socket socket;
    private Scanner socketIn;
    private PrintWriter socketOut;
    private final ClientController clientController;

    public Client(){
        this.clientController = new ClientController(this);
    }


    public void startCli() {
        clientController.startCli();
    }
    public void startGui() {
        clientController.startGui();
    }

    public void setupSocket(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        log("Connection established");

        socketIn = new Scanner(socket.getInputStream());
        socketOut = new PrintWriter(socket.getOutputStream());
    }

    @Override
    public void run(){
        while (!Thread.currentThread().isInterrupted()){
            Message message = Message.messageFromString(socketIn.next());
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

    public void sendMessage(Message message) {
        socketOut.println(message.serialize());
        socketOut.flush();
        log("Message sent to server");
    }

    // logger
    public void log(String msg) { System.out.println(msg); }

}