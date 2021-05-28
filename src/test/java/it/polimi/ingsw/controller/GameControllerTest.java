package it.polimi.ingsw.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    private GameController gameController;

    @BeforeEach
    void setUp() {
        gameController = new GameController("TestMatch", 2, new FakeStatusObserver());
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void addPlayers() {
        gameController.addPlayer("TestPlayer1", null);
        assertFalse(gameController.isFull());
        gameController.addPlayer("TestPlayer2", null);
        assertTrue(gameController.isFull());
    }

    @Test
    void start() {
    }

    @Test
    void leaderCardsChoice() {
    }
}