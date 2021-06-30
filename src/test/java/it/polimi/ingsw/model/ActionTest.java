package it.polimi.ingsw.model;

import org.junit.jupiter.api.Test;

import static it.polimi.ingsw.controller.Action.*;
import static org.junit.Assert.assertEquals;

class ActionTest {

    @Test
    void testToString() {
        assertEquals("Go ahead / Skip", SKIP.toString());
        assertEquals("Move resources", MOVE_RESOURCES.toString());
        assertEquals("Cancel", CANCEL.toString());
        assertEquals("Take resources from market", TAKE_RESOURCES_FROM_MARKET.toString());
        assertEquals("Buy development card", BUY_DEVELOPMENT_CARD.toString());
        assertEquals("Activate production", ACTIVATE_PRODUCTION.toString());
    }

}