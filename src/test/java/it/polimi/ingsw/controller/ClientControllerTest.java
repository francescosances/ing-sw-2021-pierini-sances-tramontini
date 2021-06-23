package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.*;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ClientControllerTest {
    private ClientController clientController;
    private ClientSocketStub clientSocketStub; // for testing ClientController -> server
    private ViewStub viewStub; // for testing ClientController -> View

    @BeforeEach
    void setUp() {
        viewStub = new ViewStub();
        clientController = new ClientController();
        clientSocketStub = new ClientSocketStub(clientController);
        clientController.setClientSocket(clientSocketStub);
        clientController.setView(viewStub);
    }

    @AfterEach
    void tearDown() {
        clientController = null;
        assertTrue(clientSocketStub.isEmpty());
        clientSocketStub = null;
        assertTrue(viewStub.isEmpty());
        viewStub = null;
    }

    @Test
    void handleReceivedMessage() {
        // setup test variables
        Message message;

        List<LeaderCard> leaderCardList= new ArrayList<>();
        leaderCardList.add(new DiscountLeaderCard("",2, new Requirements(new Triple<>(DevelopmentColorType.YELLOW, 1, 0)), ResourceType.SERVANT));
        leaderCardList.add(new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 0)),ResourceType.SHIELD));
        leaderCardList.add(new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 1)),ResourceType.SHIELD));
        Market market = new Market();
        Resource resource = ResourceType.SERVANT;
        Resource[] resources = new Resource[] {ResourceType.COIN, ResourceType.SHIELD, ResourceType.SERVANT};
        Warehouse warehouse = new Warehouse();
        try {
            warehouse.addResources(1, ResourceType.COIN, 2);
            warehouse.addResources(2, ResourceType.SHIELD, 1);
        } catch (IncompatibleDepotException e) {
            fail();
        }
        Strongbox strongbox = new Strongbox();
        strongbox.addResource(ResourceType.SERVANT);
        DevelopmentCard developmentCard1 = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 1)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1));
        DevelopmentCard developmentCard2 = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 0)), 1, DevelopmentColorType.BLUE, new Requirements(new Pair<>(ResourceType.SERVANT, 1)), new Pair<>(ResourceType.SHIELD, 1), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1));
        DevelopmentCard developmentCard3 = new DevelopmentCard("",2, new Requirements(new Pair<>(ResourceType.SHIELD, 0)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1));

        DevelopmentCardSlot[] slots = new DevelopmentCardSlot[] {
                new DevelopmentCardSlot(),
                new DevelopmentCardSlot()
        };

        // test cases
        message = new Message(Message.MessageType.GENERIC);
        message.addData("text", "msg");
        clientController.handleReceivedMessage(message);
        assertEquals("msg", viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.ERROR);
        message.addData("text", "err");
        clientController.handleReceivedMessage(message);
        assertEquals("err", viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.LOGIN_FAILED);
        clientController.handleReceivedMessage(message);
        assertEquals("Login failed, try with another username", viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        login();
        message = new Message(Message.MessageType.CURRENT_ACTIVE_USER);
        message.addData("username", "TestUsername");
        clientController.handleReceivedMessage(message);
        assertEquals("TestUsername", viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.LOBBY_INFO);
        List<Triple<String, Integer, Integer>> availableLobbies = new ArrayList<>();
        availableLobbies.add(new Triple<>("TestLobby0", 1, 4));
        availableLobbies.add(new Triple<>("TestLobby1", 2, 3));

        message.addData("availableMatches", Serializer.serializeLobbies(availableLobbies));
        clientController.handleReceivedMessage(message);
        assertEquals(availableLobbies, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());


        message = new Message(Message.MessageType.RESUME_MATCH);
        PlayerBoard PlayerBoard = new PlayerBoard("TestPlayer", null);
        message.addData("playerBoard", Serializer.serializePlayerBoard(PlayerBoard));
        clientController.handleReceivedMessage(message);
        assertEquals(PlayerBoard, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.LIST_START_LEADER_CARDS);
        message.addData("leaderCards", Serializer.serializeLeaderCardList(leaderCardList));
        message.addData("cardsToChoose", Serializer.serializeInt(2));
        clientController.handleReceivedMessage(message);
        assertEquals(leaderCardList, viewStub.popMessage());
        assertEquals(2, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.START_RESOURCES);
        message.addData("resources", Serializer.serializeResources(resources));
        message.addData("resourcesToChoose", Serializer.serializeInt(2));
        clientController.handleReceivedMessage(message);
        assertEquals(Arrays.asList(resources), Arrays.asList((Resource[])viewStub.popMessage()));
        assertEquals(2, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.SHOW_PLAYER_BOARD);
        PlayerBoard playerBoard = new PlayerBoard("TestPlayer", null);
        message.addData("playerBoard", Serializer.serializePlayerBoard(playerBoard));
        clientController.handleReceivedMessage(message);
        assertEquals(playerBoard, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.SHOW_FAITH_TRACK);
        FaithTrack faithTrack = new FaithTrack(null, "TestPlayer");
        message.addData("faithTrack", Serializer.serializeFaithTrack(faithTrack));
        clientController.handleReceivedMessage(message);
        assertEquals(faithTrack, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.VATICAN_REPORT);
        message.addData("username", "TestUsername");
        message.addData("vaticanReportCount", Serializer.serializeInt(2));
        clientController.handleReceivedMessage(message);
        assertEquals("TestUsername", viewStub.popMessage());
        assertEquals(2, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.ASK_FOR_ACTION);
        List<String> usernames = new ArrayList<>();
        usernames.add("TestUsername0");
        usernames.add("TestUsername1");
        List<Action> availableActions = new ArrayList<>();
        availableActions.add(Action.SKIP);
        availableActions.add(Action.ACTIVATE_PRODUCTION);
        message.addData("usernames", (new Gson()).toJson(usernames));
        message.addData("availableActions", (new Gson()).toJson(availableActions));
        clientController.handleReceivedMessage(message);
        assertEquals(usernames, viewStub.popMessage());
        assertEquals(availableActions, Arrays.asList((Action[])viewStub.popMessage()));
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.SWAP_DEPOTS);
        message.addData("warehouse", Serializer.serializeWarehouse(warehouse));
        clientController.handleReceivedMessage(message);
        assertEquals(warehouse, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.SHOW_WAREHOUSE_STATUS);
        message.addData("warehouse", Serializer.serializeWarehouse(warehouse));
        clientController.handleReceivedMessage(message);
        assertEquals(warehouse, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.SHOW_STRONGBOX_STATUS);
        message.addData("strongbox", Serializer.serializeStrongbox(strongbox));
        clientController.handleReceivedMessage(message);
        assertEquals(strongbox, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.TAKE_RESOURCES_FROM_MARKET);
        message.addData("market", Serializer.serializeMarket(market));
        clientController.handleReceivedMessage(message);
        assertEquals(market, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.SHOW_MARKET);
        message.addData("market", Serializer.serializeMarket(market));
        clientController.handleReceivedMessage(message);
        assertEquals(market, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.ASK_TO_STORE_RESOURCES);
        message.addData("resources", Serializer.serializeResources(resources));
        clientController.handleReceivedMessage(message);
        assertEquals(Arrays.asList(resources), Arrays.asList((Resource[])viewStub.popMessage()));
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.WHITE_MARBLE_CONVERSION);
        LeaderCard leaderCard1 = new WhiteMarbleLeaderCard("",5, new Requirements(new Triple<>(DevelopmentColorType.GREEN, 1, 0)), ResourceType.SHIELD, true);
        LeaderCard leaderCard2 = new WhiteMarbleLeaderCard("",5, new Requirements(new Triple<>(DevelopmentColorType.BLUE, 1, 0)), ResourceType.COIN, true);
        message.addData("card1", Serializer.serializeLeaderCard(leaderCard1));
        message.addData("card2", Serializer.serializeLeaderCard(leaderCard2));
        clientController.handleReceivedMessage(message);
        assertEquals(leaderCard1, viewStub.popMessage());
        assertEquals(leaderCard2, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.RESOURCE_TO_STORE);
        message.addData("resource", Serializer.serializeResource(resource));
        message.addData("warehouse", Serializer.serializeWarehouse(warehouse));
        clientController.handleReceivedMessage(message);
        assertEquals(resource, viewStub.popMessage());
        assertEquals(warehouse, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.DEVELOPMENT_CARDS_TO_BUY);
        List<Deck<DevelopmentCard>> developmentCards = new ArrayList<>();
        developmentCards.add(new Deck<>(new ArrayList<>(){{
            add(developmentCard1);
        }}));
        developmentCards.add(new Deck<>(new ArrayList<>(){{
            add(developmentCard2);
            add(developmentCard3);
        }}));
        message.addData("developmentCards", Serializer.serializeDevelopmentCardsDeckList(developmentCards));
        message.addData("cardsToChoose", Serializer.serializeInt(2));
        message.addData("playerBoard", Serializer.serializePlayerBoard(playerBoard));
        clientController.handleReceivedMessage(message);
        assertEquals(developmentCards, viewStub.popMessage());
        assertEquals(2, viewStub.popMessage());
        assertEquals(playerBoard, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.CHOOSE_DEVELOPMENT_CARD_SLOT);
        message.addData("slots", Serializer.serializeDevelopmentCardSlots(slots));
        message.addData("developmentCard", Serializer.serializeDevelopmentCard(developmentCard2));
        clientController.handleReceivedMessage(message);
        assertEquals(Arrays.asList(slots), Arrays.asList((DevelopmentCardSlot[])viewStub.popMessage()));
        assertEquals(developmentCard2, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.PRODUCTION);
        List<Producer> producers = new ArrayList<>();
        producers.add(developmentCard2);
        producers.add(developmentCard3);
        producers.add(new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 1)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        message.addData("productions", Serializer.serializeProducerList(producers));
        message.addData("playerBoard", Serializer.serializePlayerBoard(playerBoard));
        clientController.handleReceivedMessage(message);
        assertEquals(producers, viewStub.popMessage());
        assertEquals(playerBoard, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.ACTION_TOKEN);
        ActionToken actionToken = new ActionToken(DevelopmentColorType.GREEN);
        message.addData("actionToken", Serializer.serializeActionToken(actionToken));
        clientController.handleReceivedMessage(message);
        assertEquals(actionToken, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.PRODUCTION_PERFORMED);
        message.addData("playerBoard", Serializer.serializePlayerBoard(playerBoard));
        clientController.handleReceivedMessage(message);
        assertEquals(playerBoard, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.ACTION_PERFORMED);
        clientController.handleReceivedMessage(message);
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.SHOW_LEADER_CARDS);
        message.addData("leaderCards", Serializer.serializeLeaderCardList(leaderCardList));
        clientController.handleReceivedMessage(message);
        assertEquals(leaderCardList, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.SHOW_PLAYERS);
        Map<String, Boolean> players = new HashMap<>();
        players.put("TestPlayer0", true);
        players.put("TestPlayer1", false);
        message.addData("players", (new Gson()).toJson(players));
        clientController.handleReceivedMessage(message);
        assertEquals(players, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.SHOW_SLOTS);
        message.addData("slots", Serializer.serializeDevelopmentCardSlots(slots));
        clientController.handleReceivedMessage(message);
        assertEquals(Arrays.asList(slots), Arrays.asList((DevelopmentCardSlot[])viewStub.popMessage()));
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.END_GAME);
        clientController.handleReceivedMessage(message);
        assertTrue(viewStub.isEmpty());

        message = new Message(Message.MessageType.CHARTS);
        List<PlayerBoard> playerBoardList = new ArrayList<>();
        playerBoardList.add(new PlayerBoard("TestPlayer0", null));
        playerBoardList.add(new PlayerBoard("TestPlayer1", null));
        message.addData("charts",Serializer.serializePlayerBoardList(playerBoardList));
        clientController.handleReceivedMessage(message);
        assertEquals(playerBoardList, viewStub.popMessage());
        assertTrue(viewStub.isEmpty());
    }

    @Test
    void connect() {
        boolean exception = false;
        try {
            clientController.connect("invalid ip", 8000);
        } catch (IOException e) {
            exception = true;
        } finally {
            assertTrue(exception);
        }
    }

    @Test
    void login() {
        clientController.login("TestUsername");
        assertEquals("TestUsername", clientController.getUsername());
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.LOGIN_REQUEST, message.getType());
        assertEquals("TestUsername", message.getData("username"));
    }

    @Test
    void createNewLobby() {
        clientController.createNewLobby(1);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.LOBBY_CHOICE, message.getType());
        assertNull(message.getData("matchOwner"));
        assertEquals("1", message.getData("playersNumber"));
        clientController.createNewLobby(2);
        message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.LOBBY_CHOICE, message.getType());
        assertNull(message.getData("matchOwner"));
        assertEquals("2", message.getData("playersNumber"));
    }

    @Test
    void lobbyChoice() {
        clientController.lobbyChoice("TestMatch", 2);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.LOBBY_CHOICE, message.getType());
        assertEquals("TestMatch", message.getData("matchOwner"));
    }

    @Test
    void leaderCardsChoice() {
        List<Integer> choices = new ArrayList<>();
        choices.add(1);
        choices.add(3);
        clientController.leaderCardsChoice(choices);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.LEADER_CARDS_CHOICE, message.getType());
        assertEquals(choices, Serializer.deserializeIntList(message.getData("leaderCards")));
    }

    @Test
    void chooseDevelopmentCards() {
        DevelopmentCard[] developmentCards = new DevelopmentCard[]{
                new DevelopmentCard("TestDevCard1", 2, new Requirements(), 1, DevelopmentColorType.GREEN, new Requirements(), new Requirements()),
                new DevelopmentCard("TestDevCard1", 5, new Requirements(), 2, DevelopmentColorType.BLUE, new Requirements(), new Requirements())
        };
        clientController.chooseDevelopmentCards(developmentCards);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.DEVELOPMENT_CARDS_TO_BUY, message.getType());
        assertEquals(Arrays.asList(developmentCards), Serializer.deserializeDevelopmentCardsList(message.getData("developmentCards")));
    }

    @Test
    void chooseDevelopmentCardsSlot() {
        clientController.chooseDevelopmentCardsSlot(2);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.CHOOSE_DEVELOPMENT_CARD_SLOT, message.getType());
        assertEquals("2", message.getData("slotIndex"));
    }

    @Test
    void performAction() {
        clientController.performAction(Action.TAKE_RESOURCES_FROM_MARKET);
        Message message = clientSocketStub.popMessage();
        Gson gson = new Gson();
        assertEquals(Message.MessageType.PERFORM_ACTION, message.getType());
        assertEquals(Action.TAKE_RESOURCES_FROM_MARKET, gson.fromJson(message.getData("action"), Action.class));
    }

    @Test
    void swapDepots() {
        clientController.swapDepots(2, 3);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.SWAP_DEPOTS, message.getType());
        assertEquals("2", message.getData("depotA"));
        assertEquals("3", message.getData("depotB"));
    }

    @Test
    void chooseMarketRow() {
        clientController.chooseMarketRow(2);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.SELECT_MARKET_ROW, message.getType());
        assertEquals("2", message.getData("row"));
    }

    @Test
    void chooseMarketColumn() {
        clientController.chooseMarketColumn(2);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.SELECT_MARKET_COLUMN, message.getType());
        assertEquals("2", message.getData("column"));
    }

    @Test
    void chooseWhiteMarbleConversion() {
        clientController.chooseWhiteMarbleConversion(1);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.WHITE_MARBLE_CONVERSION, message.getType());
        assertEquals("1", message.getData("choice"));
    }

    @Test
    void chooseDepot() {
        clientController.chooseDepot(2);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.RESOURCE_TO_STORE, message.getType());
        assertEquals("2", message.getData("choice"));
    }

    @Test
    void chooseProductions() {
        Requirements costs = new Requirements(new HashMap<>(){{
            put(ResourceType.SHIELD, 1);
            put(ResourceType.COIN, 3);
        }});
        Requirements gains = new Requirements(
                new HashMap<>(){{ put(ResourceType.SHIELD, 1); }},
                new HashMap<>(){{
                    put(DevelopmentColorType.GREEN, new HashMap<>(){{
                        put(1, 1);
                    }});
                }});
        List<Integer> choices = new ArrayList<>();
        choices.add(0);
        choices.add(3);
        clientController.chooseProductions(choices, costs, gains);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.PRODUCTION, message.getType());
        assertEquals(costs, Serializer.deserializeRequirements(message.getData("costs")));
        assertEquals(gains, Serializer.deserializeRequirements(message.getData("gains")));
        assertEquals(choices, Serializer.deserializeIntList(message.getData("choices")));
    }

    @Test
    void chooseStartResources() {
        Resource[] res = new Resource[] {ResourceType.COIN, NonPhysicalResourceType.FAITH_POINT};
        clientController.chooseStartResources(res);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.START_RESOURCES, message.getType());
        assertEquals(Arrays.asList(res), Arrays.asList(Serializer.deserializeResources(message.getData("resources"))));
    }

    @Test
    void rollback() {
        clientController.rollback();
        assertEquals(Message.MessageType.ROLLBACK, clientSocketStub.popMessage().getType());
    }

    @Test
    void resumeMatch() {
        PlayerBoard playerBoard = new PlayerBoard("TestUsername", new Match("TestMatch"));
        clientController.resumeMatch(playerBoard);
        assertEquals(playerBoard, viewStub.popMessage());
    }

    @Test
    void discardLeaderCard() {
        clientController.discardLeaderCard(1);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.DISCARD_LEADER_CARD, message.getType());
        assertEquals("1", message.getData("num"));
    }

    @Test
    void activateLeaderCard() {
        clientController.activateLeaderCard(1);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.ACTIVATE_LEADER_CARD, message.getType());
        assertEquals("1", message.getData("num"));
    }

    @Test
    void showPlayerBoard() {
        clientController.showPlayerBoard("TestUsername");
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.SHOW_PLAYER_BOARD, message.getType());
        assertEquals("TestUsername", message.getData("username"));
    }

    @Test
    void refreshLobbies() {
        clientController.refreshLobbies();
        assertEquals(Message.MessageType.LOBBY_INFO, clientSocketStub.popMessage().getType());
    }
}