package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;
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
    public void buyDevelopmentCard_fromStrongboxOnly() {
        Requirements requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1));
        DevelopmentCard developmentCard = new DevelopmentCard("",1, requirements,1, null, null);
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
    public void buyDevelopmentCard_fromWarehouseOnly() throws IncompatibleDepotException {
        Requirements requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1));
        DevelopmentCard developmentCard = new DevelopmentCard("",1, requirements,1, null, null);

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
    public void buyDevelopmentCard_fromWarehouseAndStrongbox() throws IncompatibleDepotException {
        Requirements requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.STONE,7), new Pair<>(ResourceType.COIN,1));
        DevelopmentCard developmentCard = new DevelopmentCard("",1, requirements,1, null, null);

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

    @Test
    public void acceptsDevelopmentCard(){
        DevelopmentCard developmentCard = new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null);
        playerBoard.getDevelopmentCardSlots()[0].addCard(developmentCard);
        assertFalse(playerBoard.getDevelopmentCardSlots()[0].accepts(new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null)));
        assertTrue(playerBoard.getDevelopmentCardSlots()[1].accepts(new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null)));
        assertFalse(playerBoard.getDevelopmentCardSlots()[1].accepts(new DevelopmentCard("",1, null, 2, DevelopmentColorType.GREEN, null, (Requirements) null)));
        assertTrue(playerBoard.getDevelopmentCardSlots()[0].accepts(new DevelopmentCard("",2, null, 2, DevelopmentColorType.GREEN, null, (Requirements) null)));
    }

    @Test
    public void gainFaithPoints(){
        playerBoard.gainFaithPoints(2);
        assertEquals(2, playerBoard.getFaithTrack().getFaithMarker());
    }

    @Test
    public void getTotalVictoryPoints(){
        playerBoard.getDevelopmentCardSlots()[0].addCard(
                new DevelopmentCard("",3,new Requirements(new Pair<>(ResourceType.SHIELD,3)),1,
                        DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.SERVANT,2)),
                        new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.STONE,1)));
        playerBoard.getDevelopmentCardSlots()[0].addCard(
                new DevelopmentCard("",6,new Requirements(new Pair<>(ResourceType.COIN,3),new Pair<>(ResourceType.STONE,2)),2,
                        DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.COIN,1),
                        new Pair<>(ResourceType.STONE,1)),new Pair<>(ResourceType.SERVANT,3)));
        playerBoard.getDevelopmentCardSlots()[1].addCard(
                new DevelopmentCard("",1,new Requirements(new Pair<>(ResourceType.STONE,2)),1,
                        DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SERVANT,1)),
                        new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        assertEquals(10, playerBoard.getDevelopmentCardsVictoryPoints());
    }

    @Test
    public void getDevelopmentCardsVictoryPoints() throws IncompatibleDepotException {
        playerBoard.getDevelopmentCardSlots()[0].addCard(
                        new DevelopmentCard("",3,new Requirements(new Pair<>(ResourceType.SHIELD,3)),1,
                        DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.SERVANT,2)),
                        new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.STONE,1)));
        playerBoard.getDevelopmentCardSlots()[0].addCard(
                        new DevelopmentCard("",6,new Requirements(new Pair<>(ResourceType.COIN,3),new Pair<>(ResourceType.STONE,2)),2,
                        DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.COIN,1),
                        new Pair<>(ResourceType.STONE,1)),new Pair<>(ResourceType.SERVANT,3)));
        playerBoard.getDevelopmentCardSlots()[1].addCard(
                        new DevelopmentCard("",1,new Requirements(new Pair<>(ResourceType.STONE,2)),1,
                        DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SERVANT,1)),
                        new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));

        LeaderCard leaderCard1 = new DepotLeaderCard("",3,new Requirements(new Pair<> (ResourceType.COIN, 0)),ResourceType.SHIELD);
        LeaderCard leaderCard2 = new DiscountLeaderCard("",2,new Requirements(new Pair<> (ResourceType.COIN, 0)), ResourceType.STONE);
        playerBoard.addLeaderCard(leaderCard1);
        playerBoard.addLeaderCard(leaderCard2);
        playerBoard.activateLeaderCard(leaderCard1);
        playerBoard.activateLeaderCard(leaderCard2);

        for (int i = 0; i < 5; i++)
            playerBoard.getStrongbox().addResource(ResourceType.STONE);
        for (int i = 0; i < 3; i++)
            playerBoard.getStrongbox().addResource(ResourceType.COIN);
        for (int i = 0; i < 4; i++)
            playerBoard.getStrongbox().addResource(ResourceType.SHIELD);
        playerBoard.getWarehouse().addResource(0, ResourceType.SERVANT, 1);
        playerBoard.getWarehouse().addResource(2, ResourceType.SHIELD, 2);

        assertEquals(playerBoard.getLeaderCardsVictoryPoints() + playerBoard.getDevelopmentCardsVictoryPoints() + playerBoard.getResourcesVictoryPoints(),
                playerBoard.getTotalVictoryPoints());
    }

    @Test
    public void getLeaderCardsVictoryPoints(){
        LeaderCard leaderCard1 = new DepotLeaderCard("",3,new Requirements(new Pair<> (ResourceType.COIN, 0)),ResourceType.SHIELD);
        LeaderCard leaderCard2 = new DiscountLeaderCard("",2,new Requirements(new Pair<> (ResourceType.COIN, 0)), ResourceType.STONE);
        playerBoard.addLeaderCard(leaderCard1);
        playerBoard.addLeaderCard(leaderCard2);
        playerBoard.activateLeaderCard(leaderCard1);
        assertEquals(3, playerBoard.getLeaderCardsVictoryPoints());
        playerBoard.activateLeaderCard(leaderCard2);
        assertEquals(5, playerBoard.getLeaderCardsVictoryPoints());
    }

    @Test
    public void getResourcesVictoryPoints() throws IncompatibleDepotException {
        assertEquals(0, playerBoard.getResourcesVictoryPoints());
        for (int i = 0; i < 5; i++)
            playerBoard.getStrongbox().addResource(ResourceType.STONE);
        assertEquals(1, playerBoard.getResourcesVictoryPoints());
        for (int i = 0; i < 3; i++)
            playerBoard.getStrongbox().addResource(ResourceType.COIN);
        assertEquals(1, playerBoard.getResourcesVictoryPoints());
        for (int i = 0; i < 4; i++)
            playerBoard.getStrongbox().addResource(ResourceType.SHIELD);
        assertEquals(2, playerBoard.getResourcesVictoryPoints());
        playerBoard.getWarehouse().addResource(0, ResourceType.SERVANT, 1);
        assertEquals(2, playerBoard.getResourcesVictoryPoints());
        playerBoard.getWarehouse().addResource(2, ResourceType.SHIELD, 2);
        assertEquals(3, playerBoard.getResourcesVictoryPoints());
    }

}