package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.*;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    private final boolean lightMode;

    public CLI(ClientController clientController, boolean lightMode){
        this.clientController = clientController;
        this.input = new Scanner(System.in);
        this.output = System.out;
        this.errorOutput = System.out;
        this.lightMode = lightMode;
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
        output.println("  [0] New match");
        int choice;
        if(availableLobbies != null) {
            for (int i = 1; i <= availableLobbies.size(); i++) {
                output.print("  [" + i + "] ");
                output.print(availableLobbies.get(i - 1).getFirst() + "'s match ");
                output.println("(" + availableLobbies.get(i - 1).getSecond() + "/" + availableLobbies.get(i - 1).getThird() + ")");
            }
            choice = input.nextInt();
        }else
            choice = 0;
        if (choice == 0) {
            int playersNumber;
            do {
                output.println("Insert number of players:");
                playersNumber = input.nextInt();
            }while(playersNumber <= 0 || playersNumber > Match.MAX_PLAYERS);
            clientController.createNewLobby(playersNumber);
        } else if (choice > 0 && choice <= availableLobbies.size())
            clientController.lobbyChoice(availableLobbies.get(choice - 1).getFirst());
        else{
            output.println("Invalid choice");
            listLobbies(availableLobbies);
        }
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
        showLeaderCards(leaderCardList);
        output.println("Choose " + cardsToChoose + " leader cards:");
        int[] choices = new int[cardsToChoose];
        LeaderCard[] cardsChosen = new LeaderCard[cardsToChoose];
        for(int i=0;i<cardsToChoose;i++) {
            choices[i] = input.nextInt();
            for (int j = 0; j < i; j++) {
                if (choices[i] == choices[j]){
                    showErrorMessage("You chose the same card twice");
                    listLeaderCards(leaderCardList,cardsToChoose);
                    return;
                }
            }
            if(choices[i] <0 || choices[i] >= leaderCardList.size()){
                showErrorMessage("Invalid choice");
                listLeaderCards(leaderCardList,cardsToChoose);
                return;
            }
            cardsChosen[i] = leaderCardList.get(choices[i]);
        }
        clientController.leaderCardsChoice(cardsChosen);
    }

    private String developmentCardColor(DevelopmentCard card){
        switch (card.getColor()){
            case BLUE:
                return ANSI_BLUE;
            case YELLOW:
                return ANSI_YELLOW;
            case PURPLE:
                return ANSI_PURPLE;
            case GREEN:
                return ANSI_GREEN;
        }
        return "";
    }

    private void showDevelopmentCardDecks(List<Deck<DevelopmentCard>> developmentCardList,PlayerBoard playerBoard){
        int index=0;
        for(Deck<DevelopmentCard> deck : developmentCardList){
            output.print(developmentCardColor(deck.get(0)));
            output.printf("%s level %d\n",deck.get(0).getColor(),deck.get(0).getLevel());
            for(DevelopmentCard developmentCard:deck){
                if(!developmentCard.getCost().satisfied(playerBoard))
                    output.print(ANSI_BLACK + "[X]");
                else output.printf(developmentCardColor(developmentCard) + "[%d]",index++);
                output.println(developmentCard);
            }
            output.print(ANSI_RESET);
        }
    }

    @Override
    public void listDevelopmentCards(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard userBoard) {
        showDevelopmentCardDecks(developmentCardList,userBoard);
        output.println("Choose " + cardsToChoose + " development cards:");
        int[] choices = new int[cardsToChoose];
        DevelopmentCard[] cardsChosen = new DevelopmentCard[cardsToChoose];
        for(int i=0;i<cardsToChoose;i++) {
            choices[i] = input.nextInt();
            for (int j = 0; j < i; j++) {
                if (choices[i] == choices[j]){
                    showErrorMessage("You chose the same card twice");
                    listDevelopmentCards(developmentCardList,cardsToChoose,userBoard);
                    return;
                }
            }
            if(choices[i] <0 || choices[i] >= developmentCardList.size()){
                showErrorMessage("Invalid choice");
                listDevelopmentCards(developmentCardList,cardsToChoose,userBoard);
                return;
            }
            cardsChosen[i] = developmentCardList.get(choices[i]/4).get(choices[i]%4);
        }
       // clientController.leaderCardsChoice(cardsChosen);
    }

    @Override
    public void showPlayerBoard(PlayerBoard playerBoard){
        showFaithTrack(playerBoard.getFaithTrack());
        showStorage(playerBoard);
        showDevelopmentCards(playerBoard.getDevelopmentCardSlots());
        showLeaderCards(playerBoard.getLeaderCards());
    }

    private void showFaithTrack(FaithTrack faithTrack){
        output.println("Faith track: " + faithTrack.getTrackVictoryPoints() + " victory points");

        output.print(" ");
        for (int i = 0; i < faithTrack.getFaithMarker(); i++){
            output.print(" ");
        }
        output.println(ANSI_RED + "†" + ANSI_RESET);

        output.print("  ");
        int vaticanReportCount = 0;
        for (int pos = 1; pos <= FaithTrack.SIZE; pos++){
            if (pos == FaithTrack.POPE_SPACES[vaticanReportCount]){
                output.print(ANSI_RED + "▢" + ANSI_RESET);
                vaticanReportCount++;
            }else if ((pos >= FaithTrack.POPE_SPACES[vaticanReportCount] - 3 - vaticanReportCount) && (pos <= FaithTrack.POPE_SPACES[vaticanReportCount] - 1))
                output.print(ANSI_YELLOW + "▢" + ANSI_RESET);
            else
                output.print("▢");
        }
        output.println();

        showPopeFavorTiles(faithTrack);
    }

    private void showPopeFavorTiles(FaithTrack faithTrack){
        output.println("Pope favor tiles: " + faithTrack.getPopeFavorTilesVictoryPoints() + " victory points");
        Arrays.stream(faithTrack.getPopeFavorTiles()).forEach(tile -> output.println("  " + tile));
    }

    private void showStorage(PlayerBoard playerBoard){
        output.println("Storage: " + playerBoard.getResourcesVictoryPoints() + " victory points");
        showWarehouse(playerBoard.getWarehouse());
        showStrongbox(playerBoard.getStrongbox());
    }

    @Override
    public void showWarehouse(Warehouse warehouse){
        output.println("Depots:");
        for(int i=0;i<warehouse.getDepots().size();i++){
            Depot depot = warehouse.getDepots().get(i);
            output.printf("  [%d] ",i);
            for(int j=0;j<depot.getSize();j++)
                output.print("["+((depot.getOccupied()>j)?showResource(depot.getResourceType()):" ")+"]");
            for(int j=10-depot.getSize()*3;j>0;j--)
                output.print(" ");
            output.print(depot.getResourceType() == null?"Empty":depot.getResourceType().toString());
            output.println();
        }
    }

    private String showResource(ResourceType resource){
        String res;
        switch (resource){
            case SHIELD:
                res = ANSI_BLUE + "*";
                break;
            case STONE:
                res = (lightMode) ? ANSI_BLACK + "*" : ANSI_WHITE + "*";
                break;
            case COIN:
                res = ANSI_YELLOW + "*";
                break;
            case SERVANT:
                res = ANSI_PURPLE + "*";
                break;
            default:
                res = " ";
        }
        return res + ANSI_RESET;
    }

    private void showStrongbox(Strongbox strongbox){
        output.println("Strongbox:");
        for(Map.Entry<Resource, Integer> res : strongbox.getAllResources())
            output.println("  " + res.getKey() + ": " + res.getValue());
    }

    private void showDevelopmentCards(DevelopmentCardSlot[] developmentCardSlots){
        int victoryPoints = Arrays.stream(developmentCardSlots)
                .mapToInt(DevelopmentCardSlot::getVictoryPoints)
                .sum();
        output.println("Development cards: " + victoryPoints + " victory points");
        for (int i = 0; i< developmentCardSlots.length; i++){
            final DevelopmentCardSlot developmentCardSlot = developmentCardSlots[i];
            if (developmentCardSlot.getSize() > 0){
                output.println("  Slot [" + i + "]:");
                developmentCardSlot.iterator().forEachRemaining(developmentCard -> {
                    if (developmentCard.equals(developmentCardSlot.getTopCard()))
                        output.println("    Active: " + developmentCard);
                    else
                        output.println("    " + developmentCard);
                });
            }
        }
    }

    private void showLeaderCards(List<LeaderCard> leaderCardList){
        int victoryPoints = leaderCardList.stream()
                .filter(LeaderCard::isActive)
                .mapToInt(LeaderCard::getVictoryPoints)
                .sum();
        output.println("Leader cards: " + victoryPoints + " victory points");
        for (int i = 0; i < leaderCardList.size(); i++) {
            output.print("  [" + i + "] ");
            output.println(leaderCardList.get(i).toString());
        }
    }


    @Override
    public void askToSwapDepots(Warehouse warehouse) {
        this.showWarehouse(warehouse);
        output.println("Select 2 depots to swap:");
        int depotA = input.nextInt();
        int depotB = input.nextInt();
        if(depotA < 0 || depotA >= warehouse.getDepots().size() || depotB < 0 || depotB >= warehouse.getDepots().size()){
            showErrorMessage("Invalid choice");
            askToSwapDepots(warehouse);
        }else{
            clientController.swapDepots(depotA,depotB);
        }
    }

    /**
     * Show a single marble whose color depends on the type of marble
     * @param marbleType the type of marble
     * @return a string containing the colored circle inside two brackets
     */
    private String showMarble(MarbleType marbleType){
        String marble = "[";
        switch (marbleType){
            case RED:
                marble += ANSI_RED + "●";
                break;
            case BLUE:
                marble += ANSI_BLUE + "●";
                break;
            case GREY:
                marble += (lightMode) ? ANSI_BLACK + "●" : ANSI_WHITE + "◯";
            break;
            case YELLOW:
                marble += ANSI_YELLOW + "●";
                break;
            case PURPLE:
                marble += ANSI_PURPLE + "●";
                break;
            case WHITE:
                marble += (lightMode) ? ANSI_WHITE + "◯" : "●";
                break;
            default:
                marble = " ";
        }
        return marble + ANSI_RESET+"]";
    }

    @Override
    public void showMarket(Market market) {
        for(int i=0;i<Market.COLUMNS*3;i++)
            output.print(" ");
        output.println(showMarble(market.getSlideMarble()));
        for(int r=0;r<Market.ROWS;r++){
            for(int c=0;c<Market.COLUMNS;c++){
                output.print(showMarble(market.getMarble(r,c)));
            }
            output.println();
        }
    }

    @Override
    public void showResources(Resource[] resources) {
        output.println("you got these resources from the market:");
        for(Resource resource : resources){
            output.println("  " + resource);
        }
    }

    @Override
    public void askToStoreResource(Resource resource,Warehouse warehouse) {
        output.println("Where do you want to store this "+resource+" resource?");
        showWarehouse(warehouse);
        output.printf("  [%d] Discard\n",warehouse.getDepots().size());
        int choice = input.nextInt();
        if(choice < 0 || choice > warehouse.getDepots().size()) {
            showErrorMessage("Invalid choice");
            askToStoreResource(resource, warehouse);
            return;
        }
        clientController.chooseDepot(choice);
    }

    @Override
    public void chooseWhiteMarbleConversion(LeaderCard card1, LeaderCard card2) {
        output.println("You have 2 active white marble leader cards. Choose conversion output:");
        output.println("  [0] "+card1.getOutputResourceType());
        output.println("  [1] "+card2.getOutputResourceType());
        int choice = input.nextInt();
        if(choice < 0 || choice >= 2) {
            showErrorMessage("Invalid choice");
            chooseWhiteMarbleConversion(card1, card2);
            return;
        }
        clientController.chooseWhiteMarbleConversion(choice);
    }

    @Override
    public void askToChooseMarketRowOrColumn(Market market){
        showMarket(market);
        int choice;
        do {
            output.println("Do you want to choose a row or a column?");
            output.println("  [0] Row");
            output.println("  [1] Column");
            choice = input.nextInt();
            if (choice !=0 && choice != 1)
                showErrorMessage("Invalid choice");
        }while (choice !=0 && choice != 1);
        if(choice == 0){//rows
            output.print("Which row would you like to choose?");
            output.println(" [0-"+(Market.ROWS-1)+"]");
            int row = input.nextInt();
            if(row < 0 || row >= Market.ROWS){
                showErrorMessage("Invalid choice");
                askToChooseMarketRowOrColumn(market);
                return;
            }
            clientController.chooseMarketRow(row);
        }else{//columns
            output.print("Which column would you like to choose?");
            output.println(" [0-"+(Market.COLUMNS-1)+"]");
            int column = input.nextInt();
            if(column < 0 || column >= Market.COLUMNS){
                showErrorMessage("Invalid choice");
                askToChooseMarketRowOrColumn(market);
                return;
            }
            clientController.chooseMarketColumn(column);
        }
    }

    @Override
    public void askForAction(Action... availableActions) {
        output.println("Which action do you want to perform?");
        for (int i = 0; i < availableActions.length; i++) {
            output.print("  [" + (i) + "] ");
            output.println(availableActions[i].toString());
        }
        int choice = input.nextInt();
        if (choice >= 0 && choice < availableActions.length)
            clientController.performAction(availableActions[choice] );
        else{
            showErrorMessage("Invalid choice");
            askForAction(availableActions);
        }
    }

    @Override
    public void takeResourcesFromMarket(Market market) {
        showMarket(market);
    }
}
