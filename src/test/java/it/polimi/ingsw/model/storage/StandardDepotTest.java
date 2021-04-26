package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StandardDepotTest {

    Depot[] depots;

    @BeforeEach
    void setUp() {
        depots = new Depot[3];
        for (int i = 0; i < depots.length; i++)
            depots[i] = new StandardDepot(i+1);
    }

    @AfterEach
    void tearDown() {
        depots = null;
    }

    @Test
    void getResourceType() throws IncompatibleDepotException {
        depots[0].addResource(ResourceType.STONE);
        assertEquals(depots[0].getResourceType(), ResourceType.STONE);
    }

    @Test
    void getSize() {
        for (int i = 0; i < depots.length; i++)
        assertEquals(i+1, depots[i].getSize());
    }

    @Test
    void getOccupied() throws IncompatibleDepotException {
        depots[0].addResource(ResourceType.STONE);
        depots[1].addResource(ResourceType.SHIELD);
        depots[1].addResource(ResourceType.SHIELD);
        depots[2].addResource(ResourceType.COIN);


        assertEquals(1, depots[0].getOccupied());
        assertEquals(2, depots[1].getOccupied());
        assertEquals(1, depots[2].getOccupied());

    }

    @Test
    void addResource() throws IncompatibleDepotException {
        boolean bool = false;
        depots[0].addResource(ResourceType.SHIELD);
        try {
            depots[0].addResource(ResourceType.SHIELD);
        } catch (IncompatibleDepotException e) {
            assertEquals("Depot is full", e.getMessage());
            bool = true;
        }
        assertTrue(bool);
        bool = false;

        depots[1].addResource(ResourceType.STONE);
        try {
            depots[1].addResource(ResourceType.COIN);
        } catch (IncompatibleDepotException e) {
            assertEquals("Resource Type not compatible with depot", e.getMessage());
            bool = true;
        }
        assertTrue(bool);
        bool = false;
        try {
            depots[1].addResource(null);
        } catch (NullPointerException e) {
            bool = true;
        }
        assertTrue(bool);

    }

    @Test
    void removeResource() throws IncompatibleDepotException {
        depots[0].addResource(ResourceType.SERVANT);
        depots[0].removeResource();
        assertNull(depots[0].getResourceType());
        assertEquals(0, depots[0].getOccupied());

        boolean bool = false;
        try {
            depots[0].removeResource();
        } catch (IndexOutOfBoundsException e){
            bool = true;
        }
        assertTrue(bool);
    }
}