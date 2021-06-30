package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    private GameController gameController;

    private void addTestPlayer(String username) {
        gameController.addPlayer(username, null, false).setView(new ViewStub());
    }

    private void setUp(int playersNumber) {
        gameController = new GameController("TestMatch", playersNumber, new GameStatusObserverStub());
        assertEquals("TestMatch", gameController.getMatchName());
        assertEquals(playersNumber, gameController.getTotalPlayers());

        for (int i = 0; i < playersNumber; i++) {
            assertEquals(i, gameController.getJoinedPlayers());
            addTestPlayer("TestPlayer" + i);
        }

        assertEquals(playersNumber, gameController.getJoinedPlayers());
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void addPlayers() {
        gameController = new GameController("TestMatch", 2, new GameStatusObserverStub());
        addTestPlayer("TestPlayer0");
        assertFalse(gameController.isFull());
        addTestPlayer("TestPlayer1");
        assertTrue(gameController.isFull());
    }

    @Test
    void regenerateControllerAndResume() {
        List<String> players = new ArrayList<>(){{
            add("TestPlayer1");
            add("TestPlayer2");
        }};
        Match match = new Match("TestMatch", 2);
        match.addPlayer("TestPlayer1");
        match.addPlayer("TestPlayer2");
        match.setCurrentPhase(Match.GamePhase.TURN);
        GameStatusObserver gameStatusObserver = new GameStatusObserverStub();

        gameController = GameController.regenerateController(match, gameStatusObserver, players);

        assertEquals(match, gameController.getMatch());
        for (String player : players) {
            PlayerController playerController = gameController.getPlayerController(player);
            assertEquals(player, playerController.getPlayerBoard().getUsername());
            assertNull(playerController.getView());
            playerController.setView(new ViewStub());
        }

        assertTrue(gameController.isSuspended());

        gameController.start();
        assertFalse(gameController.isSuspended());
        assertEquals(Match.GamePhase.TURN, gameController.getMatch().getCurrentPhase());
    }

    @Test
    void endGame() {
        setUp(4);

        gameController.getMatch().setCurrentPlayerIndex(0);
        gameController.getPlayerController("TestPlayer0").getPlayerBoard().gainFaithPoints(10);
        gameController.getPlayerController("TestPlayer1").getPlayerBoard().gainFaithPoints(19);
        gameController.getPlayerController("TestPlayer2").getPlayerBoard().gainFaithPoints(10);

        DevelopmentCard developmentCard = new DevelopmentCard("",3, new Requirements(), 1, DevelopmentColorType.GREEN,
                new Requirements(), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 20));
        gameController.getPlayerController("TestPlayer0").getPlayerBoard().addDevelopmentCardToSlot(developmentCard, 0);

        Message message = new Message(Message.MessageType.PRODUCTION);
        List<Integer> choices = new ArrayList<>();
        choices.add(1);
        message.addData("choices",Serializer.serializeIntList(choices));
        message.addData("costs",Serializer.serializeRequirements(new Requirements()));
        message.addData("gains",Serializer.serializeRequirements(new Requirements()));
        gameController.handleReceivedGameMessage(message, "TestPlayer0");

        List<PlayerBoard> charts = gameController.getPlayers().stream().map(PlayerController::getPlayerBoard).collect(Collectors.toList());
    }

    @Test
    void start() {
        setUp(4);
        assertFalse(gameController.isStarted());
        gameController.start();
        assertTrue(gameController.isStarted());
        assertEquals(Match.GamePhase.PLAYERS_SETUP, gameController.getMatch().getCurrentPhase());
    }

    @Test
    void soloStart() {
        setUp(1);
        assertFalse(gameController.isStarted());
        gameController.start();
        assertTrue(gameController.isStarted());
        assertTrue(((SoloMatch) gameController.getMatch()).getBlackCross().isBlackCross());
        assertEquals(Match.GamePhase.PLAYERS_SETUP, gameController.getMatch().getCurrentPhase());
    }

    @Test
    void leaderCardsChoice() {
        setUp(2);
        gameController.start();
        assertEquals(Match.GamePhase.PLAYERS_SETUP, gameController.getMatch().getCurrentPhase());

        assertEquals(1, gameController.getMatch().getUsersReadyToPlay());
        gameController.getCurrentPlayer().chooseLeaderCards(
                new ArrayList<>(){{
                    add(0);
                    add(1);
                }});
        assertEquals(2, gameController.getMatch().getUsersReadyToPlay());
        gameController.getCurrentPlayer().chooseStartResources(new Resource[] {ResourceType.COIN});
        gameController.getCurrentPlayer().chooseLeaderCards(
                new ArrayList<>(){{
                    add(0);
                    add(1);
                    add(2);
                }});
        assertNotEquals(Match.GamePhase.TURN, gameController.getMatch().getCurrentPhase());
        gameController.getCurrentPlayer().chooseLeaderCards(
                new ArrayList<>(){{
                    add(0);
                    add(3);
                }});
        assertEquals(Match.GamePhase.TURN, gameController.getMatch().getCurrentPhase());
    }

    @Test
    void disconnectAndReconnect() {
        setUp(2);
        gameController.start();

        PlayerController playerController1 = gameController.getCurrentPlayer();
        gameController.disconnect(playerController1.getUsername());

        assertFalse(playerController1.isActive());
        assertFalse(gameController.isConnected(playerController1.getUsername()));
        assertNotEquals(gameController.getCurrentPlayer(), playerController1);

        gameController.getCurrentPlayer().turnEnded();
        assertNotEquals(gameController.getCurrentPlayer(), playerController1);

        gameController.connect(playerController1.getUsername());
        assertTrue(playerController1.isActive());
        assertTrue(gameController.isConnected(playerController1.getUsername()));
    }

    @Test
    void disconnectAll() {
        setUp(2);
        gameController.start();

        PlayerController pc = gameController.getCurrentPlayer();
        gameController.disconnect(pc.getUsername());

        assertFalse(pc.isActive());
        assertFalse(gameController.isConnected(pc.getUsername()));
        assertNotEquals(gameController.getCurrentPlayer(), pc);

        pc = gameController.getCurrentPlayer();
        gameController.disconnect(pc.getUsername());

        assertFalse(pc.isActive());
        assertFalse(gameController.isConnected(pc.getUsername()));

        assertEquals(Match.GamePhase.END_GAME, gameController.getMatch().getCurrentPhase());
    }
}