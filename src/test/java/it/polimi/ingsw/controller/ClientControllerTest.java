package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.network.Server;
import it.polimi.ingsw.network.SocketServer;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.locks.ReadWriteLock;

import static org.junit.jupiter.api.Assertions.*;

class ClientControllerTest {
    private ClientController clientController;
    private ClientSocketStub clientSocketStub; // for testing ClientController -> server
    //private ViewStub for testing ClientController -> View

    @BeforeEach
    void setUp() {
        clientController = new ClientController();
        clientSocketStub = new ClientSocketStub(clientController);
        clientController.setClientSocket(clientSocketStub);
    }

    @AfterEach
    void tearDown() {
        clientController = null;
        clientSocketStub = null;
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
        assertEquals(Message.MessageType.LOGIN_REQUEST, clientSocketStub.getMessage().getType());
        assertEquals("TestUsername", clientSocketStub.getMessage().getData("username"));
    }

    @Test
    void createNewLobby() {
    }

    @Test
    void lobbyChoice() {
    }

    @Test
    void leaderCardsChoice() {
    }

    @Test
    void chooseDevelopmentCards() {
        DevelopmentCard devCard1 = new DevelopmentCard("TestDevCard1", 2, new Requirements(), 1, DevelopmentColorType.GREEN, new Requirements(), new Requirements());
        DevelopmentCard devCard2 = new DevelopmentCard("TestDevCard1", 5, new Requirements(), 2, DevelopmentColorType.BLUE, new Requirements(), new Requirements());
        clientController.chooseDevelopmentCards(devCard1, devCard2);
        assertEquals(Message.MessageType.DEVELOPMENT_CARDS_TO_BUY, clientSocketStub.getMessage().getType());
        assertEquals(Arrays.asList(devCard1, devCard2), Serializer.deserializeDevelopmentCardsList(clientSocketStub.getMessage().getData("developmentCards")));
    }

    @Test
    void chooseDevelopmentCardsSlot() {
        clientController.chooseDevelopmentCardsSlot(2);
        assertEquals(Message.MessageType.CHOOSE_DEVELOPMENT_CARD_SLOT, clientSocketStub.getMessage().getType());
        assertEquals("2", clientSocketStub.getMessage().getData("slotIndex"));
    }

    @Test
    void performAction() {
        clientController.performAction(Action.TAKE_RESOURCES_FROM_MARKET);
        Gson gson = new Gson();
        assertEquals(Message.MessageType.PERFORM_ACTION, clientSocketStub.getMessage().getType());
        assertEquals(Action.TAKE_RESOURCES_FROM_MARKET, gson.fromJson(clientSocketStub.getMessage().getData("action"), Action.class));
    }

    @Test
    void swapDepots() {
        clientController.swapDepots(2, 3);
        assertEquals(Message.MessageType.SWAP_DEPOTS, clientSocketStub.getMessage().getType());
        assertEquals("2", clientSocketStub.getMessage().getData("depotA"));
        assertEquals("3", clientSocketStub.getMessage().getData("depotB"));
    }

    @Test
    void chooseMarketRow() {
        clientController.chooseMarketRow(2);
        assertEquals(Message.MessageType.SELECT_MARKET_ROW, clientSocketStub.getMessage().getType());
        assertEquals("2", clientSocketStub.getMessage().getData("row"));
    }

    @Test
    void chooseMarketColumn() {
        clientController.chooseMarketColumn(2);
        assertEquals(Message.MessageType.SELECT_MARKET_COLUMN, clientSocketStub.getMessage().getType());
        assertEquals("2", clientSocketStub.getMessage().getData("column"));
    }

    @Test
    void chooseWhiteMarbleConversion() {
        clientController.chooseWhiteMarbleConversion(1);
        assertEquals(Message.MessageType.WHITE_MARBLE_CONVERSION, clientSocketStub.getMessage().getType());
        assertEquals("1", clientSocketStub.getMessage().getData("choice"));
    }

    @Test
    void chooseDepot() {
        clientController.chooseDepot(2);
        assertEquals(Message.MessageType.RESOURCE_TO_STORE, clientSocketStub.getMessage().getType());
        assertEquals("2", clientSocketStub.getMessage().getData("choice"));
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
        clientController.chooseProductions(costs, gains);
        assertEquals(Message.MessageType.PRODUCTION, clientSocketStub.getMessage().getType());
        assertEquals(costs, Serializer.deserializeRequirements(clientSocketStub.getMessage().getData("costs")));
        assertEquals(gains, Serializer.deserializeRequirements(clientSocketStub.getMessage().getData("gains")));
    }

    @Test
    void chooseStartResources() {
        Resource[] res = new Resource[] {ResourceType.COIN, NonPhysicalResourceType.FAITH_POINT};
        clientController.chooseStartResources(res);
        assertEquals(Message.MessageType.START_RESOURCES, clientSocketStub.getMessage().getType());
        assertEquals(Arrays.asList(res), Arrays.asList(Serializer.deserializeResources(clientSocketStub.getMessage().getData("resources"))));
    }

    @Test
    void rollback() {
        clientController.rollback();
        assertEquals(Message.MessageType.ROLLBACK, clientSocketStub.getMessage().getType());
    }

    @Test
    void resumeMatch() {
    }

    @Test
    void discardLeaderCard() {
        clientController.discardLeaderCard(1);
        assertEquals(Message.MessageType.DISCARD_LEADER_CARD, clientSocketStub.getMessage().getType());
        assertEquals("1", clientSocketStub.getMessage().getData("num"));
    }

    @Test
    void activateLeaderCard() {
        clientController.activateLeaderCard(1);
        assertEquals(Message.MessageType.ACTIVATE_LEADER_CARD, clientSocketStub.getMessage().getType());
        assertEquals("1", clientSocketStub.getMessage().getData("num"));
    }

    @Test
    void showPlayerBoard() {
        clientController.showPlayerBoard("TestUsername");
        assertEquals(Message.MessageType.SHOW_PLAYER_BOARD, clientSocketStub.getMessage().getType());
        assertEquals("TestUsername", clientSocketStub.getMessage().getData("username"));
    }

    @Test
    void refreshLobbies() {
        clientController.refreshLobbies();
        assertEquals(Message.MessageType.LOBBY_INFO, clientSocketStub.getMessage().getType());
    }
}