package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.cards.DepotLeaderCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.utils.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseTest {

    Warehouse warehouse;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
    }

    @AfterEach
    void tearDown() {
        warehouse = null;
    }

    @Test
    void addResource() throws IncompatibleDepotException {
        warehouse.addResource(1, ResourceType.SHIELD, 1);
        assertEquals(ResourceType.SHIELD, warehouse.getDepots().get(1).getResourceType());
        assertEquals(1, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertNull(warehouse.getDepots().get(2).getResourceType());

        boolean bool = false;
        try{
            warehouse.addResource(2, ResourceType.SHIELD, 2);
        } catch (IncompatibleDepotException e){
            assertEquals("You can’t place the same type of Resource in two different depots.", e.getMessage());
            bool = true;
        }
        assertTrue(bool);
        bool = false;

        assertEquals(ResourceType.SHIELD, warehouse.getDepots().get(1).getResourceType());
        assertEquals(1, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertNull(warehouse.getDepots().get(2).getResourceType());

        try {
            warehouse.addResource(1, ResourceType.SERVANT, 1);
        } catch (IncompatibleDepotException e){
            bool = true;
            assertEquals("Resource Type not compatible with depot", e.getMessage());
        }
        assertTrue(bool);
        bool = false;

        assertEquals(ResourceType.SHIELD, warehouse.getDepots().get(1).getResourceType());
        assertEquals(1, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertNull(warehouse.getDepots().get(2).getResourceType());

        try {
            warehouse.addResource(1, ResourceType.SHIELD, 2);
        } catch (IncompatibleDepotException e){
            assertEquals("Depot is full", e.getMessage());
            bool = true;
        }
        assertTrue(bool);
        bool = false;

        assertEquals(ResourceType.SHIELD, warehouse.getDepots().get(1).getResourceType());
        assertEquals(2, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertNull(warehouse.getDepots().get(2).getResourceType());

        try{
            warehouse.addResource(0, null, 2);
        } catch (NullPointerException e){
            bool = true;
        }

        assertTrue(bool);

        assertEquals(ResourceType.SHIELD, warehouse.getDepots().get(1).getResourceType());
        assertEquals(2, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertNull(warehouse.getDepots().get(2).getResourceType());

        warehouse.addResource(0, ResourceType.COIN, 1);

        assertEquals(ResourceType.SHIELD, warehouse.getDepots().get(1).getResourceType());
        assertEquals(ResourceType.COIN, warehouse.getDepots().get(0).getResourceType());
        assertEquals(2, warehouse.getDepots().get(1).getOccupied());
        assertEquals(1, warehouse.getDepots().get(0).getOccupied());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertNull(warehouse.getDepots().get(2).getResourceType());
    }

    @Test
    void removeResources() throws IncompatibleDepotException {
        warehouse.addResource(2, ResourceType.SERVANT, 3);
        warehouse.addResource(1, ResourceType.SHIELD, 1);
        warehouse.removeResources(new Requirements(new Pair<>(ResourceType.SERVANT, 2)));

        assertEquals(ResourceType.SERVANT, warehouse.getDepots().get(2).getResourceType());
        assertEquals(ResourceType.SHIELD, warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(1, warehouse.getDepots().get(2).getOccupied());
        assertEquals(1, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());

        Requirements requirements = warehouse.removeResources(new Requirements(new Pair<>(ResourceType.SERVANT, 2)));

        assertEquals(new Requirements(new Pair<>(ResourceType.SERVANT, 1)), requirements);
        assertNull(warehouse.getDepots().get(2).getResourceType());
        assertEquals(ResourceType.SHIELD, warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertEquals(1, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
    }

    @Test
    void toBeStored() {
        assertNull(warehouse.getDepots().get(2).getResourceType());
        assertNull(warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertEquals(0, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertFalse(warehouse.hasResourcesToStore());

        warehouse.toBeStored(new Resource[]{NonPhysicalResourceType.VOID, ResourceType.STONE, ResourceType.COIN});

        assertTrue(warehouse.hasResourcesToStore());
        assertEquals(ResourceType.COIN, warehouse.popResourceToBeStored());
        assertNull(warehouse.getDepots().get(2).getResourceType());
        assertNull(warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertEquals(0, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());

        assertTrue(warehouse.hasResourcesToStore());
        assertEquals(ResourceType.STONE, warehouse.popResourceToBeStored());
        assertNull(warehouse.getDepots().get(2).getResourceType());
        assertNull(warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertEquals(0, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());

        assertTrue(warehouse.hasResourcesToStore());
        assertEquals(NonPhysicalResourceType.VOID, warehouse.popResourceToBeStored());
        assertNull(warehouse.getDepots().get(2).getResourceType());
        assertNull(warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertEquals(0, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());

        assertFalse(warehouse.hasResourcesToStore());
    }

    @Test
    void pushResourceToBeStored() {
        assertNull(warehouse.getDepots().get(2).getResourceType());
        assertNull(warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertEquals(0, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertFalse(warehouse.hasResourcesToStore());

        warehouse.pushResourceToBeStored(ResourceType.COIN);
        assertNull(warehouse.getDepots().get(2).getResourceType());
        assertNull(warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(0, warehouse.getDepots().get(2).getOccupied());
        assertEquals(0, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertTrue(warehouse.hasResourcesToStore());
        assertEquals(ResourceType.COIN, warehouse.popResourceToBeStored());
    }

    @Test
    void swapDepots() throws IncompatibleDepotException {
        warehouse.addResource(2, ResourceType.COIN, 1);
        warehouse.addResource(0, ResourceType.SERVANT, 1);

        assertEquals(ResourceType.COIN, warehouse.getDepots().get(2).getResourceType());
        assertNull(warehouse.getDepots().get(1).getResourceType());
        assertEquals(ResourceType.SERVANT, warehouse.getDepots().get(0).getResourceType());
        assertEquals(1, warehouse.getDepots().get(2).getOccupied());
        assertEquals(0, warehouse.getDepots().get(1).getOccupied());
        assertEquals(1, warehouse.getDepots().get(0).getOccupied());

        warehouse.swapDepots(0,2);
        assertEquals(ResourceType.SERVANT, warehouse.getDepots().get(2).getResourceType());
        assertNull(warehouse.getDepots().get(1).getResourceType());
        assertEquals(ResourceType.COIN, warehouse.getDepots().get(0).getResourceType());
        assertEquals(1, warehouse.getDepots().get(2).getOccupied());
        assertEquals(0, warehouse.getDepots().get(1).getOccupied());
        assertEquals(1, warehouse.getDepots().get(0).getOccupied());
        assertFalse(warehouse.hasResourcesToStore());

        warehouse.swapDepots(1,0);
        assertEquals(ResourceType.SERVANT, warehouse.getDepots().get(2).getResourceType());
        assertEquals(ResourceType.COIN, warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(1, warehouse.getDepots().get(2).getOccupied());
        assertEquals(1, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertFalse(warehouse.hasResourcesToStore());
    }

    @Test
    void getAllResources() throws IncompatibleDepotException {
        warehouse.addResource(1, ResourceType.COIN, 2);
        warehouse.addResource(2,ResourceType.SHIELD, 2);

        assertEquals(new Requirements(new Pair<>(ResourceType.COIN, 2), new Pair<>(ResourceType.SHIELD, 2)), warehouse.getAllResources());
        assertEquals(ResourceType.SHIELD, warehouse.getDepots().get(2).getResourceType());
        assertEquals(ResourceType.COIN, warehouse.getDepots().get(1).getResourceType());
        assertNull(warehouse.getDepots().get(0).getResourceType());
        assertEquals(2, warehouse.getDepots().get(2).getOccupied());
        assertEquals(2, warehouse.getDepots().get(1).getOccupied());
        assertEquals(0, warehouse.getDepots().get(0).getOccupied());
        assertFalse(warehouse.hasResourcesToStore());
    }

    @Test
    void addDepotLeaderCard() {
        LeaderCard leaderCard = new DepotLeaderCard("",3, null,ResourceType.SHIELD);
        warehouse.addDepotLeaderCard(leaderCard);
        assertEquals(leaderCard, warehouse.getDepots().get(3));
    }
}