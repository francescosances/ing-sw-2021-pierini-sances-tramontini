package it.polimi.ingsw.network;

import it.polimi.ingsw.controller.GameController;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable {
    private Socket socket;
    protected Server server;

    protected Scanner in;
    protected PrintWriter out;

    protected String username;

    public ClientHandler(Socket socket,Server server) throws IOException {
        this.socket = socket;
        this.server = server;
        in = new Scanner(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream());
    }

    public void sendError(String error){
        out.println(error);
    }

    @Override
    public void run() {
        try {
            System.out.println("Avviato");
            String username;
            do {
                username = askUsername();
            }while (!server.isValidUsername(username));
            this.username = username;

            chooseMatch();
            //TODO: scegliere il numero di persone per partita oppure permettere all'host di avviarla
            while(!Thread.currentThread().isInterrupted()){
               String msg = in.next();
             //  server.getGameController(username).messageReceived(msg);
            }

            //close connections
            in.close();
            out.close();
            socket.close();
        } catch (IOException e){
            System.out.println("PROVA");
            System.err.println(e.getMessage());
        }finally {
           //Client disconnected
            server.disconnect(username);
        }
        System.out.println("Terminato");
    }

    protected String askUsername(){
        out.println("askUsername");
        return in.nextLine();
    }

    /**
     * List all matches and get an integer to choose the match. If the integer is out of the bound, a new match is created
     */
    protected void chooseMatch() {
        try {
            out.println(server.listMatches().stream().map(GameController::getMatchName));
            int index = in.nextInt();
            if (index < 0 || index >= server.listMatches().size())
                server.addPlayer(username, new GameController(username));
            else
                server.addPlayer(username, server.listMatches().get(index));
        }catch (Exception e){
            sendError(e.getMessage());
            chooseMatch();
        }
    }

}
