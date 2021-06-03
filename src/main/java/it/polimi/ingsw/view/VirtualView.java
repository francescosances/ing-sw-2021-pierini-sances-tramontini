package it.polimi.ingsw.view;

import com.google.gson.Gson;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentCardSlot;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Strongbox;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.network.ClientHandler;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Triple;

import java.util.*;
import java.util.stream.Collectors;

public class VirtualView implements View {

    /**
     * The ClientHandler that manages the user which refers to this view
     */
    private final ClientHandler clientHandler;

    private String currentActiveUser;

    private final String username;

    public VirtualView(ClientHandler clientHandler, String username) {
        this.clientHandler = clientHandler;
        this.username = username;
    }

    /**
     * Sends a message to the client
     * @param message the message to be sent
     */
    private void sendMessage(Message message){
        clientHandler.sendMessage(message);
    }

    @Override
    public void showMessage(String message) {
        Message msg = new Message(Message.MessageType.GENERIC);
        msg.addData("text", message);
        sendMessage(msg);
    }

    @Override
    public void showErrorMessage(String message) {
        Message msg = new Message(Message.MessageType.ERROR);
        msg.addData("text", message);
        sendMessage(msg);
    }

    @Override
    public void listLobbies(List<Triple<String, Integer, Integer>> availableLobbies){
        Message message = new Message(Message.MessageType.LOBBY_INFO);
        message.addData("availableMatches",Serializer.serializeLobbies(availableLobbies));
        sendMessage(message);
    }

    @Override
    public void resumeMatch(PlayerBoard playerBoard) {
        Message message = new Message(Message.MessageType.RESUME_MATCH);
        message.addData("playerBoard", Serializer.serializePlayerBoard(playerBoard));
        sendMessage(message);
    }

    @Override
    public void init() {
        System.out.println("Initialized virtual view");
    }

    @Override
    public void askLogin() {
        sendMessage(new Message( Message.MessageType.LOGIN_REQUEST));
    }

    @Override
    public void waitForOtherPlayers() {

    }

    @Override
    public void userConnected(String username) {
       showMessage(username+" has joined the lobby");
    }

    @Override
    public void userDisconnected(String username) {
        showMessage(username+" has left the lobby");
    }

    @Override
    public void listLeaderCards(List<LeaderCard> leaderCardList,int cardsToChoose) {
        if(leaderCardList.isEmpty())
            throw new IllegalArgumentException("No leader cards given");
        Message message = new Message(Message.MessageType.LIST_START_LEADER_CARDS);
        message.addData("leaderCards",Serializer.serializeLeaderCardList(leaderCardList));
        message.addData("cardsToChoose",Serializer.serializeInt(cardsToChoose));
        sendMessage(message);
    }

    @Override
    public void showPlayerLeaderCards(List<LeaderCard> leaderCardList) {
        if (!currentActiveUser.equals(username)) {
            showLeaderCards(leaderCardList);
            return;
        }
        Message message = new Message(Message.MessageType.SHOW_PLAYER_LEADER_CARDS);
        message.addData("leaderCards", Serializer.serializeLeaderCardList(leaderCardList));
        sendMessage(message);
    }

    @Override
    public void showLeaderCards(List<LeaderCard> leaderCardList) {
        if (!currentActiveUser.equals(username))
            leaderCardList.stream().filter(LeaderCard::isActive).collect(Collectors.toList());
        Message message = new Message(Message.MessageType.SHOW_LEADER_CARDS);
        message.addData("leaderCards", Serializer.serializeLeaderCardList(leaderCardList));
        sendMessage(message);
    }

    @Override
    public void showDevelopmentCardSlots(DevelopmentCardSlot[] developmentCardSlots) {
        Message message = new Message(Message.MessageType.SHOW_SLOTS);
        message.addData("slots", Serializer.serializeDevelopmentCardSlots(developmentCardSlots));
        sendMessage(message);
    }

    @Override
    public void listDevelopmentCards(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard userBoard) {
        Message message = new Message(Message.MessageType.DEVELOPMENT_CARDS_TO_BUY);
        message.addData("developmentCards",Serializer.serializeDevelopmentCardsDeckList(developmentCardList));
        message.addData("cardsToChoose",Serializer.serializeInt(cardsToChoose));
        message.addData("playerBoard",Serializer.serializePlayerBoard(userBoard));
        sendMessage(message);
    }

    @Override
    public void showPlayerBoard(PlayerBoard pb){
        PlayerBoard playerBoard = pb.clone();
        if (!pb.getUsername().equals(this.username))
            playerBoard.getLeaderCards().removeIf(card -> !card.isActive());
        Message message = new Message(Message.MessageType.SHOW_PLAYER_BOARD);
        message.addData("playerBoard",Serializer.serializePlayerBoard(playerBoard));
        sendMessage(message);
    }

    @Override
    public void showFaithTrack(FaithTrack faithTrack) {
        Message message = new Message(Message.MessageType.SHOW_FAITH_TRACK);
        message.addData("faithTrack",Serializer.serializeFaithTrack(faithTrack));
        sendMessage(message);
    }

    @Override
    public void showVaticanReportTriggered(String username, int vaticanReportCount) {
        String name;
        if (username.equals(this.username))//TODO: in caso di black cross compare comunque "you"
            name = Match.YOU_STRING;
        else
            name = username;
        Message message = new Message(Message.MessageType.VATICAN_REPORT);
        message.addData("username", name);
        message.addData("vaticanReportCount", Serializer.serializeInt(vaticanReportCount));
        sendMessage(message);
    }

    @Override
    public void showWarehouse(Warehouse warehouse) {
        Message message = new Message(Message.MessageType.SHOW_WAREHOUSE_STATUS);
        message.addData("warehouse",Serializer.serializeWarehouse(warehouse));
        sendMessage(message);
    }

    @Override
    public void showStrongbox(Strongbox strongbox) {
        Message message = new Message(Message.MessageType.SHOW_STRONGBOX_STATUS);
        message.addData("strongbox", Serializer.serializeStrongbox(strongbox));
        sendMessage(message);
    }

    @Override
    public void askToSwapDepots(Warehouse warehouse) {
        Message message = new Message(Message.MessageType.SWAP_DEPOTS);
        message.addData("warehouse",Serializer.serializeWarehouse(warehouse));
        sendMessage(message);
    }

    @Override
    public void takeResourcesFromMarket(Market market) {
        if (!currentActiveUser.equals(username)){
            showMarket(market);
            return;
        }
        Message message = new Message(Message.MessageType.TAKE_RESOURCES_FROM_MARKET);
        message.addData("market",Serializer.serializeMarket(market));
        sendMessage(message);

    }

    @Override
    public void showMarket(Market market) {
        if (currentActiveUser.equals(username))
            return;
        Message message = new Message(Message.MessageType.SHOW_MARKET);
        message.addData("market",Serializer.serializeMarket(market));
        sendMessage(message);
    }

    @Override
    public void showResourcesGainedFromMarket(Resource[] resources) {
        Message message = new Message(Message.MessageType.ASK_TO_STORE_RESOURCES);
        message.addData("resources",Serializer.serializeResources(resources));
        sendMessage(message);
    }


    @Override
    public void askToStoreResource(Resource resource, Warehouse warehouse) {
        Message message = new Message(Message.MessageType.RESOURCE_TO_STORE);
        message.addData("warehouse",Serializer.serializeWarehouse(warehouse));
        message.addData("resource",Serializer.serializeResource(resource));
        sendMessage(message);
    }

    @Override
    public void chooseWhiteMarbleConversion(LeaderCard card1, LeaderCard card2) {
        Message message = new Message(Message.MessageType.WHITE_MARBLE_CONVERSION);
        message.addData("card1",Serializer.serializeLeaderCard(card1));
        message.addData("card2",Serializer.serializeLeaderCard(card2));
        sendMessage(message);
    }

    @Override
    public void askToChooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard) {
        Message message = new Message(Message.MessageType.CHOOSE_DEVELOPMENT_CARD_SLOT);
        message.addData("slots",Serializer.serializeDevelopmentCardSlots(slots));
        message.addData("developmentCard",Serializer.serializeDevelopmentCard(developmentCard));
        sendMessage(message);
    }

    @Override
    public void showCurrentActiveUser(String username) {
        currentActiveUser = new String(username);
        String name;
        if (this.username.equals(username))
            name = Match.YOU_STRING;
        else
            name = new String(username);
        Message message = new Message(Message.MessageType.CURRENT_ACTIVE_USER);
        message.addData("username",name);
        sendMessage(message);
    }

    @Override
    public void askToChooseStartResources(Resource[] values,int resourcesToChoose) {
        Message message = new Message(Message.MessageType.START_RESOURCES);
        message.addData("resources",Serializer.serializeResources(values));
        message.addData("resourcesToChoose",Serializer.serializeInt(resourcesToChoose));
        sendMessage(message);
    }

    @Override
    public void showPlayers(Map<String, Boolean> players) {
        players.remove(username);
        players.put(Match.YOU_STRING, true);
        Message message = new Message(Message.MessageType.SHOW_PLAYERS);
        message.addData("players",new Gson().toJson(players));
        sendMessage(message);
    }

    @Override
    public void showActionToken(ActionToken actionToken) {
        Message message = new Message(Message.MessageType.ACTION_TOKEN);
        message.addData("actionToken", Serializer.serializeActionToken(actionToken));
        sendMessage(message);
    }

    @Override
    public void showProducerUser() {
        Message message = new Message(Message.MessageType.PRODUCTION_PERFORMED);
        sendMessage(message);
    }

    @Override
    public void actionPerformed() {
        Message message = new Message(Message.MessageType.ACTION_PERFORMED);
        sendMessage(message);
    }

    @Override
    public void askForAction(List<String> usernames, Action... availableActions) {
        Gson gson = new Gson();
        List<String> list = usernames.stream().map(t-> t.equals(this.username) ? t = Match.YOU_STRING : t).collect(Collectors.toList());
        Message message = new Message(Message.MessageType.ASK_FOR_ACTION);
        message.addData("availableActions",gson.toJson(Arrays.asList(availableActions)));
        message.addData("usernames", gson.toJson(list));
        sendMessage(message);
    }

    @Override
    public void chooseProductions(List<Producer> availableProductions,PlayerBoard playerBoard) {
        Message message = new Message(Message.MessageType.PRODUCTION);
        message.addData("productions",Serializer.serializeProducerList(availableProductions));
        message.addData("playerBoard",Serializer.serializePlayerBoard(playerBoard));
        sendMessage(message);
    }

    public String getCurrentActiveUser(){
        return currentActiveUser;
    }

    public String getUsername() {
        return username;
    }
}
