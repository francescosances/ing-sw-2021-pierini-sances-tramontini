package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.model.Action.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

class ActionTest {

    @Test
    void testToString() {
        assertEquals("Go ahead / Skip", SKIP.toString());
        assertEquals("Move resources", MOVE_RESOURCES.toString());
        assertEquals("Discard leader card", DISCARD_LEADER.toString());
        assertEquals("Cancel", CANCEL.toString());
        assertEquals("Take resources from market", TAKE_RESOURCES_FROM_MARKET.toString());
        assertEquals("Buy development card", BUY_DEVELOPMENT_CARD.toString());
        assertEquals("Activate production", ACTIVATE_PRODUCTION.toString());
    }

}