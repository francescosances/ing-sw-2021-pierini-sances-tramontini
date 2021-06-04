package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.FaithTrack;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.view.View;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    private GameController gameController;
    List<PlayerController> playerControllerList;
    List<ViewStub> views;

    @BeforeEach
    void setUp() {
        setUp(2);
    }

    private void setUp(int playersNumber) {
        gameController = new GameController("TestMatch", playersNumber, new FakeStatusObserver());
        playerControllerList = new ArrayList<>();
        views = new ArrayList<>();
        for (int i = 0; i < playersNumber; i++) {
            views.add(new ViewStub());
            playerControllerList.add(gameController.addPlayer("TestPlayer" + i, null, false));
            playerControllerList.get(i).setView(views.get(i));
        }
    }

    @AfterEach
    void tearDown() {
        gameController = null;
        playerControllerList = null;
        views = null;
    }

    @Test
    void addPlayers() {
        tearDown();
        gameController = new GameController("TestMatch", 2, new FakeStatusObserver());
        gameController.addPlayer("TestPlayer0", null);
        assertFalse(gameController.isFull());
        gameController.addPlayer("TestPlayer1", null);
        assertTrue(gameController.isFull());
    }

    @Test
    void endGame() {
        tearDown();
        setUp(4);

        gameController.getMatch().setCurrentPlayerIndex(0);
        playerControllerList.get(0).getPlayerBoard().gainFaithPoints(10);
        playerControllerList.get(1).getPlayerBoard().gainFaithPoints(19);
        playerControllerList.get(2).getPlayerBoard().gainFaithPoints(10);

        DevelopmentCard developmentCard = new DevelopmentCard("",3, new Requirements(), 1, DevelopmentColorType.GREEN,
                new Requirements(), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 20));
        playerControllerList.get(0).getPlayerBoard().addDevelopmentCardToSlot(developmentCard, 0);

        Message message = new Message(Message.MessageType.PRODUCTION);
        List<Integer> choices = new ArrayList<>();
        choices.add(1);
        message.addData("choices",Serializer.serializeIntList(choices));
        message.addData("costs",Serializer.serializeRequirements(new Requirements()));
        message.addData("gains",Serializer.serializeRequirements(new Requirements()));
        gameController.handleReceivedGameMessage(message, "TestPlayer0");

        List<PlayerBoard> charts = playerControllerList.stream().map(PlayerController::getPlayerBoard).collect(Collectors.toList());

        for (ViewStub view: views) {
            assertEquals("MATCH ENDED", view.popMessage());
            assertEquals(charts.toString(), view.popMessage());
        }

    }

    @Test
    void start() {
    }

    @Test
    void leaderCardsChoice() {
    }
}