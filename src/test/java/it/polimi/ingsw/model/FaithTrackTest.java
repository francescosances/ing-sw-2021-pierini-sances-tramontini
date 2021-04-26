package it.polimi.ingsw.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FaithTrackTest {

    Match match;
    FaithTrack faithTrack;

    @BeforeEach
    void setUp() {
        match = new Match("test");
        faithTrack = new FaithTrack(match);
    }

    @AfterEach
    void tearDown() {
        match = null;
        faithTrack = null;
    }

    @Test
    void getFaithMarker() {
        assertEquals(0, faithTrack.getFaithMarker());
        faithTrack.moveMarker();
        assertEquals(1, faithTrack.getFaithMarker());
    }

    @Test
    void isValidVaticanReport(){
        assertTrue(faithTrack.isValidVaticanReport(8));
        assertTrue(faithTrack.isValidVaticanReport(16));
        assertTrue(faithTrack.isValidVaticanReport(24));

        faithTrack.vaticanReportTriggered(0);
        assertFalse(faithTrack.isValidVaticanReport(8));
        assertTrue(faithTrack.isValidVaticanReport(16));
        assertTrue(faithTrack.isValidVaticanReport(24));

        faithTrack.vaticanReportTriggered(1);
        assertFalse(faithTrack.isValidVaticanReport(8));
        assertFalse(faithTrack.isValidVaticanReport(16));
        assertTrue(faithTrack.isValidVaticanReport(24));

        faithTrack.vaticanReportTriggered(2);
        assertFalse(faithTrack.isValidVaticanReport(8));
        assertFalse(faithTrack.isValidVaticanReport(16));
        assertFalse(faithTrack.isValidVaticanReport(24));
    }

    @Test
    void getTrackVictoryPoints() {
        for (int i = 1; i < 3; i++) {
            faithTrack.moveMarker();
            assertEquals(0, faithTrack.getTrackVictoryPoints());
        }
        for (int i = 0; i < 3; i++) {
            faithTrack.moveMarker();
            assertEquals(1, faithTrack.getTrackVictoryPoints());
        }
        for (int i = 0; i < 3; i++) {
            faithTrack.moveMarker();
            assertEquals(2, faithTrack.getTrackVictoryPoints());
        }
        for (int i = 0; i < 3; i++) {
            faithTrack.moveMarker();
            assertEquals(4, faithTrack.getTrackVictoryPoints());
        }
        for (int i = 0; i < 3; i++) {
            faithTrack.moveMarker();
            assertEquals(6, faithTrack.getTrackVictoryPoints());
        }
        for (int i = 0; i < 3; i++) {
            faithTrack.moveMarker();
            assertEquals(9, faithTrack.getTrackVictoryPoints());
        }
        for (int i = 0; i < 3; i++) {
            faithTrack.moveMarker();
            assertEquals(12, faithTrack.getTrackVictoryPoints());
        }
        for (int i = 0; i < 4; i++) {
            try {
                faithTrack.moveMarker();
                assertEquals(16, faithTrack.getTrackVictoryPoints());
            } catch (EndGameException e){}
        }
        assertEquals(20, faithTrack.getTrackVictoryPoints());
    }

    @Test
    void moveMarker(){
        faithTrack = match.addPlayer("test2").faithTrack;
        for (PopeFavorTile pft : faithTrack.getPopeFavorTiles())
            assertEquals(0, pft.getVictoryPoints());
        assertEquals(0, faithTrack.getVictoryPoints());
        for (int i = 0; i < 8; i++)
            faithTrack.moveMarker();
        assertEquals(2, faithTrack.getPopeFavorTiles()[0].getVictoryPoints());
        assertEquals(4, faithTrack.getVictoryPoints());

        match.vaticanReport(16);
        assertNull(faithTrack.getPopeFavorTiles()[1]);
        assertEquals(4, faithTrack.getVictoryPoints());

        for (int i = 0; i < 8; i++)
            faithTrack.moveMarker();
        assertEquals(2, match.getVaticanReportsCount());

        for (int i = 0; i < 7; i++)
            faithTrack.moveMarker();
        assertEquals(faithTrack.getTrackVictoryPoints() +
                faithTrack.getPopeFavorTiles()[0].getVictoryPoints(), faithTrack.getVictoryPoints());

        match.vaticanReport(24);

        assertEquals(faithTrack.getTrackVictoryPoints() +
                faithTrack.getPopeFavorTiles()[0].getVictoryPoints() +
                faithTrack.getPopeFavorTiles()[2].getVictoryPoints(), faithTrack.getVictoryPoints());

        boolean bool = false;
        try {
            faithTrack.moveMarker();
        } catch (EndGameException e) {
            bool = true;
        }
        assertTrue(bool);

        assertEquals(faithTrack.getTrackVictoryPoints() +
                faithTrack.getPopeFavorTiles()[0].getVictoryPoints() +
                faithTrack.getPopeFavorTiles()[2].getVictoryPoints(), faithTrack.getVictoryPoints());
    }
}