package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public class CLI implements View {

    private final ClientController clientController;

    private final Scanner input;
    private final PrintStream output;
    private final PrintStream errorOutput;

    public CLI(ClientController clientController){
        this.clientController = clientController;
        this.input = new Scanner(System.in);
        this.output = System.out;
        this.errorOutput = System.err;
    }

    @Override
    public void showMessage(String message) {
        output.println(message);
    }

    @Override
    public void showErrorMessage(String message) {
        errorOutput.println(message);
    }

    @Override
    public void init() {
        output.println("Welcome");
        askConnection();
    }

    public void askConnection(){
        output.print("Insert server address: ");
        String serverAddress = input.next();
        output.print("Insert server port: ");
        int serverPort = input.nextInt();
        try {
            clientController.connect(serverAddress, serverPort);
        } catch (IOException e){
            output.println("Unable to connect");
            askConnection();
        }
    }

    @Override
    public void askLogin(){
        output.print("Insert desired username: ");
        String username = input.next();
        clientController.login(username);
    }

    @Override
    public void listLobbies(List<Triple<String, Integer, Integer>> availableMatches) {
        // TODO possibilità di aggiornare la lista di lobby disponibili
        output.println("Create a new match or join one:");
        output.println("[0] New match");
        if(availableMatches != null) {
            for (int i = 1; i <= availableMatches.size(); i++) {
                output.print("[" + i + "] ");
                output.print(availableMatches.get(i - 1).getFirst() + "'s match ");
                output.println("(" + availableMatches.get(i - 1).getSecond() + "/" + availableMatches.get(i - 1).getThird() + ")");
            }
        }
        int choice = input.nextInt();
        if (choice == 0)
            clientController.lobbyChoice(null);
        else if (choice > 0 && choice <= availableMatches.size())
            clientController.lobbyChoice(availableMatches.get(choice - 1).getFirst());
        else{
            output.println("Invalid choice");
            listLobbies(availableMatches);
        }
    }

    @Override
    public void waitForStart() {
        do {
            System.out.println("Type \"START\" to start the match");
        }while(!input.nextLine().equals("START"));
        clientController.startMatch();
    }


    @Override
    public void resumeMatch(Match match) {
        // TODO
    }

    @Override
    public void yourTurn() {
        output.println("It's your turn");
    }

    @Override
    public void userConnected(String username) {
        output.println(username + " has joined the match");
    }

    @Override
    public void userDisconnected(String username) {
        output.println(username + " has been disconnected");
    }

    @Override
    public void listLeaderCards(List<LeaderCard> leaderCardList) {
        System.out.println("Choose two leader cards:");
        for (int i = 1; i < leaderCardList.size(); i++) {
            output.print("[" + i + "] ");
            LeaderCard temp = leaderCardList.get(i);
            output.println(temp.toString());
        }
        int choiceA = input.nextInt();
        int choiceB = input.nextInt();
        if (choiceA > 0 && choiceA <= leaderCardList.size() && choiceB > 0 && choiceB < leaderCardList.size())
            clientController.leaderCardsChoice(leaderCardList.get(choiceA),leaderCardList.get(choiceB));
        else{
            output.println("Invalid choice");
            listLeaderCards(leaderCardList);
        }
    }

}
