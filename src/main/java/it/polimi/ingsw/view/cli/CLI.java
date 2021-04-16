package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.Depot;
import it.polimi.ingsw.model.storage.Storage;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

//TODO: lo scanner deve essere eseguito in un thread diverso dal printstream
public class CLI implements View {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * The controller related to this view
     */
    private final ClientController clientController;

    /**
     * The scanner that reads the user inputs
     */
    private final Scanner input;
    /**
     * The default output stream to communicate with the user
     */
    private final PrintStream output;
    /**
     * The output stream used to communicate the errors to the user
     */
    private final PrintStream errorOutput;

    public CLI(ClientController clientController){
        this.clientController = clientController;
        this.input = new Scanner(System.in);
        this.output = System.out;
        this.errorOutput = System.out;
    }

    @Override
    public void showMessage(String message) {
        output.println(message);
    }

    @Override
    public void showErrorMessage(String message) {
        errorOutput.println(ANSI_RED+message+ANSI_RESET);
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
    public void listLobbies(List<Triple<String, Integer, Integer>> availableLobbies) {
        // TODO possibilità di aggiornare la lista di lobby disponibili
        output.println("Create a new match or join one:");
        output.println("[0] New match");
        int choice;
        if(availableLobbies != null) {
            for (int i = 1; i <= availableLobbies.size(); i++) {
                output.print("[" + i + "] ");
                output.print(availableLobbies.get(i - 1).getFirst() + "'s match ");
                output.println("(" + availableLobbies.get(i - 1).getSecond() + "/" + availableLobbies.get(i - 1).getThird() + ")");
            }
            choice = input.nextInt();
        }else
            choice = 0;
        if (choice == 0)
            clientController.lobbyChoice(null);
        else if (choice > 0 && choice <= availableLobbies.size())
            clientController.lobbyChoice(availableLobbies.get(choice - 1).getFirst());
        else{
            output.println("Invalid choice");
            listLobbies(availableLobbies);
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
    public void listLeaderCards(List<LeaderCard> leaderCardList,int cardsToChoose) {
        output.println("Choose "+cardsToChoose+" leader cards:");
        for (int i = 0; i < leaderCardList.size(); i++) {
            output.print("[" + i + "] ");
            LeaderCard temp = leaderCardList.get(i);
            output.println(temp.toString());
        }
        int[] choices = new int[cardsToChoose];
        LeaderCard[] cardsChosen = new LeaderCard[cardsToChoose];
        for(int i=0;i<cardsToChoose;i++) {
            choices[i] = input.nextInt();
            if(choices[i] <0 || choices[i] >= leaderCardList.size()){
                showErrorMessage("Invalid choice");
                listLeaderCards(leaderCardList,cardsToChoose);
                return;
            }
            cardsChosen[i] = leaderCardList.get(choices[i]);
        }
        clientController.leaderCardsChoice(cardsChosen);
    }

    @Override
    public void showWarehouseStatus(Warehouse warehouse){
        System.out.println("Depots status:");
        for(int i=0;i<warehouse.getDepots().size();i++){
            Depot depot = warehouse.getDepots().get(i);
            System.out.printf("[%d] ",i);
            for(int j=0;j<depot.getSize();j++)
                System.out.printf("["+((depot.getOccupied()>j)?"*":" ")+"]");
            for(int j=10-depot.getSize()*3;j>0;j--)
                System.out.printf(" ");
            System.out.printf(depot.getResourceType() == null?"Empty":depot.getResourceType().toString());
            System.out.println();
        }
        System.out.println(warehouse);
    }

    @Override
    public void askToSwapDepots(Warehouse warehouse) {
        this.showWarehouseStatus(warehouse);
        System.out.println("Select 2 depots to swap:");
        int depotA = input.nextInt();
        int depotB = input.nextInt();
        if(depotA < 0 || depotA >= warehouse.getDepots().size() || depotB < 0 || depotB >= warehouse.getDepots().size()){
            showErrorMessage("Invalid choice");
            askToSwapDepots(warehouse);
        }else{
            clientController.swapDepots(depotA,depotB);
        }
    }

    @Override
    public void askForAction(Action ... availableActions) {
        output.println("Which action do you want to perform?");
        for (int i = 0; i < availableActions.length; i++) {
            output.print("[" + (i) + "] ");
            output.println(availableActions[i].toString());
        }
        int choice = input.nextInt();
        if (choice >= 0 && choice <= availableActions.length)
            clientController.performAction(availableActions[choice] );
        else{
            showErrorMessage("Invalid choice");
            askForAction(availableActions);
        }
    }

}
