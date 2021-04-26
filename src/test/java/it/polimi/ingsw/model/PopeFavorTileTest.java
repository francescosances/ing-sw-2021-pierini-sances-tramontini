package it.polimi.ingsw.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PopeFavorTileTest {

    PopeFavorTile popeFavorTile;

    @AfterEach
    void tearDown() {
        popeFavorTile = null;
    }

    @Test
    void isUncovered() {
        popeFavorTile = new PopeFavorTile(3);
        assertFalse(popeFavorTile.isUncovered());
        popeFavorTile.uncover();
        assertTrue(popeFavorTile.isUncovered());
    }

    @Test
    void getVictoryPoints() {
        popeFavorTile = new PopeFavorTile(3);
        assertEquals(0, popeFavorTile.getVictoryPoints());
        popeFavorTile.uncover();
        assertEquals(3, popeFavorTile.getVictoryPoints());
    }
}