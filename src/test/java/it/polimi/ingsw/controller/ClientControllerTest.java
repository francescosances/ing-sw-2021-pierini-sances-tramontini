package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        LeaderCard[] leaderCards = new LeaderCard[] {
                new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.SERVANT, 5)), ResourceType.SHIELD),
                new ProductionLeaderCard("",4, new Requirements(new Triple<>(DevelopmentColorType.YELLOW, 2, 1)), new Requirements(new Pair<>(ResourceType.SHIELD, 1)))
        };
        clientController.leaderCardsChoice(leaderCards);
        Message message = clientSocketStub.popMessage();
        assertEquals(Message.MessageType.LEADER_CARDS_CHOICE, message.getType());
        assertEquals(Arrays.asList(leaderCards), Serializer.deserializeLeaderCardList(message.getData("leaderCards")));
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