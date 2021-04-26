package it.polimi.ingsw.model;

import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.ResourceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MarbleTypeTest {

    @Test
    void toResource() {
        assertEquals(ResourceType.STONE, MarbleType.GREY.toResource());
        assertEquals(ResourceType.SHIELD, MarbleType.BLUE.toResource());
        assertEquals(ResourceType.SERVANT, MarbleType.PURPLE.toResource());
        assertEquals(ResourceType.COIN, MarbleType.YELLOW.toResource());

        assertEquals(NonPhysicalResourceType.VOID, MarbleType.WHITE.toResource());
        assertEquals(NonPhysicalResourceType.FAITH_POINT, MarbleType.RED.toResource());
    }
}