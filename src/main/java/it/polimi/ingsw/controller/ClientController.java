package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Producer;
import it.polimi.ingsw.model.cards.DevelopmentCardSlot;
import it.polimi.ingsw.model.Market;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.network.ClientSocket;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.cli.CLI;
import it.polimi.ingsw.view.gui.GUI;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ClientController {
    /**
     * The socket connection to the server
     */
    private final ClientSocket clientSocket;

    /**
     * The view used to interact with the user
     */
    private View view;

    /**
     * Initialize a new ClientController connected to the server through the specified Client object
     * @param clientSocket the socket connection reference
     */
    public ClientController(ClientSocket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Set the view to Command Line Interface and launch it
     */
    public void startCli() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select 1 if your default view is light, 2 if dark:");
        //select the correct view
        view = new CLI(this,scanner.nextInt() == 1);
        view.init();
    }

    /**
     * Sets the view to Graphical User Interface and launch it
     */
    public void startGui() {
        view = new GUI(this);
        view.init();
    }

    /**
     * Method that map a message with the the action that must be executed
     * @param message the message received from the server via socket
     */
    public void handleReceivedMessage(Message message) {
        Gson gson = new Gson();
        switch (message.getType()) {
            case GENERIC:
                view.showMessage(message.getData("text"));
                break;
            case ERROR:
                view.showErrorMessage(message.getData("text"));
                break;
            case CURRENT_ACTIVE_USER:
                view.showCurrentActiveUser(message.getData("username"));
                break;
            case LOGIN_FAILED:
                view.showErrorMessage("Login failed, try with another username");
                view.askLogin();
                break;
            case LOBBY_INFO:
                String availableMessages = message.getData("availableMatches");
                Type listType = new TypeToken<List<Triple<String,Integer,Integer>>>(){}.getType();
                List<Triple<String, Integer, Integer>> matches = gson.fromJson(availableMessages, listType);
                view.listLobbies(matches);
                break;
            case RESUME_MATCH:
                resumeMatch(Serializer.deserializeMatchState(message.getData("match")));
                break;
            case LIST_LEADER_CARDS:
                List<LeaderCard> leaderCardList = Serializer.deserializeLeaderCardDeck(message.getData("leaderCards"));
                view.listLeaderCards(leaderCardList,Integer.parseInt(message.getData("cardsToChoose")));
                break;
            case START_RESOURCES:
                view.askToChooseStartResources(Serializer.deserializeResources(message.getData("resources")),Integer.parseInt(message.getData("resourcesToChoose")));
                break;
            case SHOW_PLAYER_BOARD:
                view.showPlayerBoard(Serializer.deserializePlayerBoard(message.getData("playerBoard")));
                break;
            case ASK_FOR_ACTION:
                List<Action> actions = gson.fromJson(message.getData("availableActions"),new TypeToken<List<Action>>(){}.getType());
                view.askForAction(actions.toArray(new Action[0]));
                break;
            case SWAP_DEPOTS:
                view.askToSwapDepots(Serializer.deserializeWarehouse(message.getData("warehouse")));
                break;
            case SHOW_WAREHOUSE_STATUS:
                view.showWarehouse(Serializer.deserializeWarehouse(message.getData("warehouse")));
                break;
            case TAKE_RESOURCES_FROM_MARKET:
                Market market = Serializer.deserializeMarket(message.getData("market"));
                view.takeResourcesFromMarket(market);
                break;
            case SHOW_MARKET:
                market = Serializer.deserializeMarket(message.getData("market"));
                view.showMarket(market);
            case SHOW_RESOURCES:
                view.showResourcesGainedFromMarket(Serializer.deserializeResources(message.getData("resources")));
                break;
            case WHITE_MARBLE_CONVERSION:
                view.chooseWhiteMarbleConversion(Serializer.deserializeLeaderCard(message.getData("card1")),Serializer.deserializeLeaderCard(message.getData("card2")));
                break;
            case RESOURCE_TO_STORE:
                view.askToStoreResource(Serializer.deserializeResource(message.getData("resource")),Serializer.deserializeWarehouse(message.getData("warehouse")));
                break;
            case DEVELOPMENT_CARDS_TO_BUY:
                view.listDevelopmentCards(Serializer.deserializeDevelopmentCardsDeckList(message.getData("developmentCards")),Integer.parseInt(message.getData("cardsToChoose")),Serializer.deserializePlayerBoard(message.getData("playerBoard")));
                break;
            case CHOOSE_DEVELOPMENT_CARD_SLOT:
                view.askToChooseDevelopmentCardSlot(Serializer.deserializeDevelopmentCardsSlots(message.getData("slots")).toArray(new DevelopmentCardSlot[0]),Serializer.deserializeDevelopmentCard(message.getData("developmentCard")));
                break;
            case PRODUCTION:
                List<Producer> producers = Serializer.deserializeProducerList(message.getData("productions"));
                view.chooseProductions(producers,Serializer.deserializePlayerBoard(message.getData("playerboard")));
                break;
            default:
                clientSocket.log("Received unexpected message");
                clientSocket.log(message.serialize());
        }
    }

    /**
     * Connects the socket to server and ask the user to login
     * @param ip the ip address of the server
     * @param port the port of the server
     * @throws IOException if the connection is interrupted
     */
    public void connect(String ip, int port) throws IOException {
        clientSocket.setupSocket(ip, port);
        new Thread(clientSocket).start();
        view.askLogin();
    }

    /**
     * Sends to the server a login request
     * @param username the username chosen to login
     */
    public void login(String username){
        Message message = new Message(Message.MessageType.LOGIN_REQUEST);
        message.addData("username", username);
        clientSocket.sendMessage(message);
    }

    /**
     * Sends to the server the lobby chosen by the user
     * @param matchName the name of the match chosen by the user
     */
    public void lobbyChoice(String matchName) {
        lobbyChoice(matchName,-1);
    }

    public void createNewLobby(int playersNumber){
        lobbyChoice(null,playersNumber);
    }

    public void lobbyChoice(String matchName,int playersNumber){
        Message message = new Message(Message.MessageType.LOBBY_CHOICE);
        message.addData("matchOwner", matchName);
        if(playersNumber > 0)
            message.addData("playersNumber", String.valueOf(playersNumber));
        clientSocket.sendMessage(message);
    }

    /**
     * Sends to the server the array of leader cards chosen by the user
     * @param leaderCards the array of leader cards chosen by the user
     */
    public void leaderCardsChoice(LeaderCard ... leaderCards){
        Message message = new Message(Message.MessageType.LEADER_CARDS_CHOICE);
        message.addData("leaderCards", Serializer.serializeLeaderCardDeck(leaderCards));
        clientSocket.sendMessage(message);
    }

    public void chooseDevelopmentCards(DevelopmentCard ... cardsChosen) {
        Message message = new Message(Message.MessageType.DEVELOPMENT_CARDS_TO_BUY);
        message.addData("developmentCards",Serializer.serializeDevelopmentCardsList(Arrays.asList(cardsChosen)));
        clientSocket.sendMessage(message);
    }

    public void chooseDevelopmentCardsSlot(int choice) {
        Message message = new Message(Message.MessageType.CHOOSE_DEVELOPMENT_CARD_SLOT);
        message.addData("slotIndex",String.valueOf(choice));
        clientSocket.sendMessage(message);
    }

    /**
     * Sends to the server the action chosen by the user so that it can be performed
     * @param action the action to start
     */
    public void performAction(Action action){
        Gson gson = new Gson();
        Message message = new Message(Message.MessageType.PERFORM_ACTION);
        message.addData("action",gson.toJson(action));
        clientSocket.sendMessage(message);
    }

    public void swapDepots(int depotA, int depotB) {
        Message message = new Message(Message.MessageType.SWAP_DEPOTS);
        message.addData("depotA",String.valueOf(depotA));
        message.addData("depotB",String.valueOf(depotB));
        clientSocket.sendMessage(message);
    }

    public void chooseMarketRow(int row){
        Message message = new Message(Message.MessageType.SELECT_MARKET_ROW);
        message.addData("row",String.valueOf(row));
        clientSocket.sendMessage(message);
    }

    public void chooseMarketColumn(int column){
        Message message = new Message(Message.MessageType.SELECT_MARKET_COLUMN);
        message.addData("column",String.valueOf(column));
        clientSocket.sendMessage(message);
    }

    public void chooseWhiteMarbleConversion(int choice) {
        Message message = new Message(Message.MessageType.WHITE_MARBLE_CONVERSION);
        message.addData("choice",String.valueOf(choice));
        clientSocket.sendMessage(message);
    }

    public void chooseDepot(int choice) {
        Message message = new Message(Message.MessageType.RESOURCE_TO_STORE);
        message.addData("choice",String.valueOf(choice));
        clientSocket.sendMessage(message);
    }

    public void chooseProductions(Requirements costs,Requirements gains) {
        Message message = new Message(Message.MessageType.PRODUCTION);
        message.addData("costs",Serializer.serializeRequirements(costs));
        message.addData("gains",Serializer.serializeRequirements(gains));
        clientSocket.sendMessage(message);
    }

    public void chooseStartResources(Resource[] resourcesChosen) {
        Message message = new Message(Message.MessageType.START_RESOURCES);
        message.addData("resources",Serializer.serializeResources(resourcesChosen));
        clientSocket.sendMessage(message);
    }

    public void rollback() {
        Message message = new Message(Message.MessageType.ROLLBACK);
        clientSocket.sendMessage(message);
    }

    /**
     * Resumes a match suspended after a network disconnection
     * @param match the match to be resumed
     */
    public void resumeMatch(Match match){
        view.resumeMatch(match);
    }

}
