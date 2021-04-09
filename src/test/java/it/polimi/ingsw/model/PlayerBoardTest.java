package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.utils.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

public class PlayerBoardTest {

    private Match match;
    private PlayerBoard playerBoard;

    @Before
    public void setUp() throws Exception {
        match = new Match("TestMatch");
        playerBoard = new PlayerBoard("TestPlayerBoard", match);
    }

    @After
    public void tearDown() throws Exception {
        match = null;
        playerBoard = null;
    }

    @Test
    public void takeResourcesFromMarket_Row() {
        Market market = match.getMarket();
        market.setSlideMarble(MarbleType.WHITE);
        market.setMarble(0,0, MarbleType.WHITE);
        market.setMarble(0,1, MarbleType.PURPLE);
        market.setMarble(0,2, MarbleType.BLUE);
        market.setMarble(0,3, MarbleType.YELLOW);
        market.setMarble(1,0, MarbleType.PURPLE);
        market.setMarble(1,1, MarbleType.YELLOW);
        market.setMarble(1,2, MarbleType.WHITE);
        market.setMarble(1,3, MarbleType.GREY);
        market.setMarble(2,0, MarbleType.WHITE);
        market.setMarble(2,1, MarbleType.RED);
        market.setMarble(2,2, MarbleType.GREY);
        market.setMarble(2,3, MarbleType.BLUE);

        playerBoard.takeResourcesFromMarket(0);

        List<Resource> testList = new ArrayList<>();
        testList.add(MarbleType.WHITE.toResource());
        testList.add(MarbleType.PURPLE.toResource());
        testList.add(MarbleType.BLUE.toResource());
        testList.add(MarbleType.YELLOW.toResource());

        for (int i = 0; i < 4; i++)
            assertTrue(testList.remove(playerBoard.warehouse.popResourceToBeStored()));

        assertTrue(testList.isEmpty());
        assertFalse(playerBoard.warehouse.hasResourcesToStore());
    }

    @Test
    public void takeResourcesFromMarket_Column() {
        Market market = match.getMarket();
        market.setSlideMarble(MarbleType.WHITE);
        market.setMarble(0,0, MarbleType.WHITE);
        market.setMarble(0,1, MarbleType.PURPLE);
        market.setMarble(0,2, MarbleType.BLUE);
        market.setMarble(0,3, MarbleType.YELLOW);
        market.setMarble(1,0, MarbleType.PURPLE);
        market.setMarble(1,1, MarbleType.YELLOW);
        market.setMarble(1,2, MarbleType.WHITE);
        market.setMarble(1,3, MarbleType.GREY);
        market.setMarble(2,0, MarbleType.WHITE);
        market.setMarble(2,1, MarbleType.RED);
        market.setMarble(2,2, MarbleType.GREY);
        market.setMarble(2,3, MarbleType.BLUE);

        playerBoard.takeResourcesFromMarket(6);

        List<Resource> testList = new ArrayList<>();
        testList.add(MarbleType.YELLOW.toResource());
        testList.add(MarbleType.GREY.toResource());
        testList.add(MarbleType.BLUE.toResource());

        for (int i = 0; i < 3; i++)
            assertTrue(testList.remove(playerBoard.warehouse.popResourceToBeStored()));

        assertTrue(testList.isEmpty());
        assertFalse(playerBoard.warehouse.hasResourcesToStore());
    }

    @Test
    public void buyDevelopmentCard_fromStrongboxOnly() {
        Requirements requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1));
        DevelopmentCard developmentCard = new DevelopmentCard(1, requirements,1, null, null);
        playerBoard.strongbox.addResources(new HashMap<ResourceType, Integer>(){{
            put(ResourceType.SHIELD, 3);
            put(ResourceType.COIN, 1);
            put(ResourceType.SERVANT, 4);
        }});

        playerBoard.buyDevelopmentCard(developmentCard);

        Requirements expectedWarehouse = new Requirements();
        assertEquals(expectedWarehouse, playerBoard.warehouse.getAllResources());

        assertEquals(1, playerBoard.strongbox.getResourcesNum(ResourceType.SHIELD));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.COIN));
        assertEquals(4, playerBoard.strongbox.getResourcesNum(ResourceType.SERVANT));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.STONE));
    }

    @Test
    public void buyDevelopmentCard_fromWarehouseOnly() {
        Requirements requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1));
        DevelopmentCard developmentCard = new DevelopmentCard(1, requirements,1, null, null);

        playerBoard.warehouse.addResource(0, ResourceType.COIN, 1);
        playerBoard.warehouse.addResource(2, ResourceType.SHIELD, 3);

        playerBoard.buyDevelopmentCard(developmentCard);

        Requirements expectedWarehouse = new Requirements(new Pair<>(ResourceType.SHIELD,1));
        assertEquals(expectedWarehouse, playerBoard.warehouse.getAllResources());

        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SHIELD));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.COIN));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SERVANT));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.STONE));
    }

    @Test
    public void buyDevelopmentCard_fromWarehouseAndStrongbox() {
        Requirements requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.STONE,7), new Pair<>(ResourceType.COIN,1));
        DevelopmentCard developmentCard = new DevelopmentCard(1, requirements,1, null, null);

        playerBoard.warehouse.addResource(0, ResourceType.COIN, 1);
        playerBoard.warehouse.addResource(1, ResourceType.STONE, 2);
        playerBoard.warehouse.addResource(2, ResourceType.SHIELD, 3);

        playerBoard.strongbox.addResources(new HashMap<ResourceType, Integer>(){{
            put(ResourceType.STONE, 7);
            put(ResourceType.COIN, 1);
            put(ResourceType.SERVANT, 4);
        }});

        playerBoard.buyDevelopmentCard(developmentCard);

        Requirements expectedWarehouse = new Requirements(new Pair<>(ResourceType.SHIELD,1));
        assertEquals(expectedWarehouse, playerBoard.warehouse.getAllResources());

        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SHIELD));
        assertEquals(1, playerBoard.strongbox.getResourcesNum(ResourceType.COIN));
        assertEquals(4, playerBoard.strongbox.getResourcesNum(ResourceType.SERVANT));
        assertEquals(2, playerBoard.strongbox.getResourcesNum(ResourceType.STONE));
    }

}