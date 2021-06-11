package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.cards.exceptions.NotSatisfiedRequirementsException;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.Strongbox;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import it.polimi.ingsw.utils.Triple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerBoardTest {

    private Match match;
    private PlayerBoard playerBoard;

    @BeforeEach
    void setUp() {
        match = new Match("TestMatch");
        playerBoard = new PlayerBoard("TestPlayerBoard", match);
    }

    @AfterEach
    void tearDown() {
        match = null;
        playerBoard = null;
    }

    @Test
    void addDevelopmentCardToSlot(){
        //Initialization
        DevelopmentCard developmentCard = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1)),1, null, null);
        playerBoard.addDevelopmentCardToSlot(developmentCard, 0);
        DevelopmentCardSlot[] expectedDevelopmentCardSlots = new DevelopmentCardSlot[3];
        for (int i = 0; i < expectedDevelopmentCardSlots.length; i++)
            expectedDevelopmentCardSlots[i] = new DevelopmentCardSlot();

        //level 1 top of nothing
        expectedDevelopmentCardSlots[0].addCard(developmentCard);
        assertArrayEquals(expectedDevelopmentCardSlots, playerBoard.getDevelopmentCardSlots());

        //level 1 on top of level 1
        boolean exceptionCaught = false;
        try{
            playerBoard.addDevelopmentCardToSlot(developmentCard, 0);
        } catch (NotSatisfiedRequirementsException e){
            assertEquals("Invalid slot selection", e.getMessage());
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
        assertArrayEquals(expectedDevelopmentCardSlots, playerBoard.getDevelopmentCardSlots());

        //level 1 on top of nothing
        developmentCard = new DevelopmentCard("",2, new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1)),1, null, null);
        playerBoard.addDevelopmentCardToSlot(developmentCard, 1);
        expectedDevelopmentCardSlots[1].addCard(developmentCard);
        assertArrayEquals(expectedDevelopmentCardSlots, playerBoard.getDevelopmentCardSlots());

        //level 2 on top of nothing
        developmentCard = new DevelopmentCard("",2, new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1)),2, null, null);
        exceptionCaught = false;
        try{
            playerBoard.addDevelopmentCardToSlot(developmentCard, 2);
        } catch (NotSatisfiedRequirementsException e){
            assertEquals("Invalid slot selection", e.getMessage());
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
        assertArrayEquals(expectedDevelopmentCardSlots, playerBoard.getDevelopmentCardSlots());

        //level 2 on top of level 1
        playerBoard.addDevelopmentCardToSlot(developmentCard, 0);
        expectedDevelopmentCardSlots[0].addCard(developmentCard);
        assertArrayEquals(expectedDevelopmentCardSlots, playerBoard.getDevelopmentCardSlots());

        //level 3 on top of level 1
        developmentCard = new DevelopmentCard("",2, new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1)),3, null, null);
        exceptionCaught = false;
        try{
            playerBoard.addDevelopmentCardToSlot(developmentCard, 1);
        } catch (NotSatisfiedRequirementsException e){
            assertEquals("Invalid slot selection", e.getMessage());
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
        assertArrayEquals(expectedDevelopmentCardSlots, playerBoard.getDevelopmentCardSlots());

        //level 3 on top of level 2
        playerBoard.addDevelopmentCardToSlot(developmentCard, 0);
        expectedDevelopmentCardSlots[0].addCard(developmentCard);
        assertArrayEquals(expectedDevelopmentCardSlots, playerBoard.getDevelopmentCardSlots());

        //level 3 on top of level 3
        exceptionCaught = false;
        try{
            playerBoard.addDevelopmentCardToSlot(developmentCard, 0);
        } catch (NotSatisfiedRequirementsException e){
            assertEquals("Invalid slot selection", e.getMessage());
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
        assertArrayEquals(expectedDevelopmentCardSlots, playerBoard.getDevelopmentCardSlots());

        //level 1 on top of level 3
        developmentCard = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1)),1, null, null);
        exceptionCaught = false;
        try{
            playerBoard.addDevelopmentCardToSlot(developmentCard, 0);
        } catch (NotSatisfiedRequirementsException e){
            assertEquals("Invalid slot selection", e.getMessage());
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
        assertArrayEquals(expectedDevelopmentCardSlots, playerBoard.getDevelopmentCardSlots());
    }

    @Test
    void buyDevelopmentCard() throws IncompatibleDepotException {
        //from strongbox only
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

        tearDown();
        setUp();

        //from warehouse only
        requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1));
        developmentCard = new DevelopmentCard("",1, requirements,1, null, null);

        playerBoard.warehouse.addResources(0, ResourceType.COIN, 1);
        playerBoard.warehouse.addResources(2, ResourceType.SHIELD, 3);

        playerBoard.buyDevelopmentCard(developmentCard);

        expectedWarehouse = new Requirements(new Pair<>(ResourceType.SHIELD,1));
        assertEquals(expectedWarehouse, playerBoard.warehouse.getAllResources());

        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SHIELD));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.COIN));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SERVANT));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.STONE));

        tearDown();
        setUp();

        //from both warehouse and strongbox
        requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.STONE,7), new Pair<>(ResourceType.COIN,1));
        developmentCard = new DevelopmentCard("",1, requirements,1, null, null);

        playerBoard.warehouse.addResources(0, ResourceType.COIN, 1);
        playerBoard.warehouse.addResources(1, ResourceType.STONE, 2);
        playerBoard.warehouse.addResources(2, ResourceType.SHIELD, 3);

        playerBoard.strongbox.addResources(new HashMap<ResourceType, Integer>(){{
            put(ResourceType.STONE, 7);
            put(ResourceType.COIN, 1);
            put(ResourceType.SERVANT, 4);
        }});

        playerBoard.buyDevelopmentCard(developmentCard);

        expectedWarehouse = new Requirements(new Pair<>(ResourceType.SHIELD,1));
        assertEquals(expectedWarehouse, playerBoard.warehouse.getAllResources());

        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SHIELD));
        assertEquals(1, playerBoard.strongbox.getResourcesNum(ResourceType.COIN));
        assertEquals(4, playerBoard.strongbox.getResourcesNum(ResourceType.SERVANT));
        assertEquals(2, playerBoard.strongbox.getResourcesNum(ResourceType.STONE));

        tearDown();
        setUp();

        //Discounted card test
        requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1));
        developmentCard = new DevelopmentCard("",1, requirements,1, null, null);

        playerBoard.warehouse.addResources(0, ResourceType.COIN, 1);
        playerBoard.warehouse.addResources(2, ResourceType.SHIELD, 3);
        LeaderCard leaderCard = new DiscountLeaderCard("",2, new Requirements(new Triple<>(DevelopmentColorType.YELLOW, 1, 0)), ResourceType.SHIELD, 1, true);
        playerBoard.getLeaderCards().add(leaderCard);

        playerBoard.buyDevelopmentCard(developmentCard);

        expectedWarehouse = new Requirements(new Pair<>(ResourceType.SHIELD,2));
        assertEquals(expectedWarehouse, playerBoard.warehouse.getAllResources());

        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SHIELD));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.COIN));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SERVANT));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.STONE));

        tearDown();
        setUp();

        //Requirements not satisfied
        requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1));
        developmentCard = new DevelopmentCard("",1, requirements,1, null, null);

        boolean exceptionCaught = false;
        try {
            playerBoard.buyDevelopmentCard(developmentCard);
        } catch (IllegalArgumentException e) {
            assertEquals("Card not purchasable", e.getMessage());
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);

        expectedWarehouse = new Requirements();
        assertEquals(expectedWarehouse, playerBoard.warehouse.getAllResources());

        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SHIELD));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.COIN));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.SERVANT));
        assertEquals(0, playerBoard.strongbox.getResourcesNum(ResourceType.STONE));

        tearDown();
        setUp();

        //7th card bought
        requirements = new Requirements(new Pair<>(ResourceType.SHIELD,0), new Pair<>(ResourceType.COIN,0));
        for (int slot = 0; slot < 2; slot++) {
            for (int level = 1; level <= 3; level++) {
                developmentCard = new DevelopmentCard("",1, requirements,level, null, null);
                playerBoard.buyDevelopmentCard(developmentCard);
                playerBoard.addDevelopmentCardToSlot(developmentCard, slot);
            }
        }

        requirements = new Requirements(new Pair<>(ResourceType.SHIELD,2), new Pair<>(ResourceType.COIN,1));
        developmentCard = new DevelopmentCard("",1, requirements,1, null, null);

        playerBoard.warehouse.addResources(0, ResourceType.COIN, 1);
        playerBoard.warehouse.addResources(2, ResourceType.SHIELD, 3);

        exceptionCaught = false;
        try {
            playerBoard.buyDevelopmentCard(developmentCard);
        } catch (EndGameException e) {
            exceptionCaught = true;
        }

        assertEquals(7, playerBoard.getBoughtDevelopmentCardsCounter());
        assertTrue(exceptionCaught);

    }

    @Test
    void acceptsDevelopmentCard(){
        DevelopmentCard developmentCard = new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null);
        playerBoard.getDevelopmentCardSlots()[0].addCard(developmentCard);
        assertFalse(playerBoard.getDevelopmentCardSlots()[0].accepts(new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null)));
        assertTrue(playerBoard.getDevelopmentCardSlots()[1].accepts(new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null)));
        assertFalse(playerBoard.getDevelopmentCardSlots()[1].accepts(new DevelopmentCard("",1, null, 2, DevelopmentColorType.GREEN, null, (Requirements) null)));
        assertTrue(playerBoard.getDevelopmentCardSlots()[0].accepts(new DevelopmentCard("",2, null, 2, DevelopmentColorType.GREEN, null, (Requirements) null)));
    }

    @Test
    void gainFaithPoints(){
        playerBoard.gainFaithPoints(2);
        assertEquals(2, playerBoard.getFaithTrack().getFaithMarker());
    }

    @Test
    void getTotalVictoryPoints(){
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
    void getDevelopmentCardsVictoryPoints() throws IncompatibleDepotException {
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
        playerBoard.getWarehouse().addResources(0, ResourceType.SERVANT, 1);
        playerBoard.getWarehouse().addResources(2, ResourceType.SHIELD, 2);

        assertEquals(playerBoard.getLeaderCardsVictoryPoints() + playerBoard.getDevelopmentCardsVictoryPoints() + playerBoard.getResourcesVictoryPoints(),
                playerBoard.getTotalVictoryPoints());
    }

    @Test
    void getLeaderCardsVictoryPoints(){
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
    void getResourcesVictoryPoints() throws IncompatibleDepotException {
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
        playerBoard.getWarehouse().addResources(0, ResourceType.SERVANT, 1);
        assertEquals(2, playerBoard.getResourcesVictoryPoints());
        playerBoard.getWarehouse().addResources(2, ResourceType.SHIELD, 2);
        assertEquals(3, playerBoard.getResourcesVictoryPoints());
    }

    @Test
    void activateAndDiscardLeaderCard(){
        LeaderCard depotLeaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<> (ResourceType.COIN, 0)),ResourceType.SHIELD);
        LeaderCard discountLeaderCard = new DiscountLeaderCard("",2,new Requirements(new Pair<> (ResourceType.COIN, 0)), ResourceType.STONE);
        LeaderCard whiteMarbleLeaderCard = new WhiteMarbleLeaderCard("",2,new Requirements(new Pair<> (ResourceType.COIN, 1)), ResourceType.SERVANT);
        LeaderCard productionLeaderCard = new ProductionLeaderCard("",4, new Requirements(new Pair<>(ResourceType.COIN, 0)), new Requirements(new Pair<>(ResourceType.SHIELD, 1)));

        playerBoard.getLeaderCards().add(depotLeaderCard);
        playerBoard.getLeaderCards().add(discountLeaderCard);

        assertEquals(2, playerBoard.getAvailableLeaderCards().size());
        playerBoard.activateLeaderCard(0);
        assertEquals(4, playerBoard.getWarehouse().getDepots().size());
        assertEquals(1, playerBoard.getAvailableLeaderCards().size());
        assertEquals(depotLeaderCard, playerBoard.getWarehouse().getDepots().get(3));

        boolean exceptionCaught = false;
        try {
            playerBoard.activateLeaderCard(1);
        } catch (IndexOutOfBoundsException e) {
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);

        playerBoard.activateLeaderCard(0);
        assertEquals(0, playerBoard.getAvailableLeaderCards().size());

        playerBoard.getLeaderCards().remove(0);
        playerBoard.getLeaderCards().remove(0);
        playerBoard.getLeaderCards().add(productionLeaderCard);
        playerBoard.getLeaderCards().add(whiteMarbleLeaderCard);

        exceptionCaught = false;
        try {
            playerBoard.activateLeaderCard(1);
        } catch (NotSatisfiedRequirementsException e) {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
        assertEquals(2, playerBoard.getAvailableLeaderCards().size());


        playerBoard.discardLeaderCard(1);
        assertEquals(1, playerBoard.getAvailableLeaderCards().size());
        assertEquals(1, playerBoard.getFaithTrack().getFaithMarker());

        playerBoard.activateLeaderCard(0);
        assertEquals(0, playerBoard.getAvailableLeaderCards().size());
        assertEquals(2, playerBoard.getAvailableProductions().size());
        assertEquals(productionLeaderCard, playerBoard.getAvailableProductions().get(1));

        exceptionCaught = false;
        try {
            playerBoard.discardLeaderCard(1);
        } catch (IndexOutOfBoundsException e) {
            exceptionCaught = true;
        }

        assertTrue(exceptionCaught);
    }

    @Test
    void getAvailableProductions(){
        List<Producer> expectedList = new ArrayList<>();
        expectedList.add(DevelopmentCard.getBaseProduction());
        assertEquals(expectedList, playerBoard.getAvailableProductions());

        DevelopmentCard developmentCard = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD,0)),1, null, null);
        playerBoard.addDevelopmentCardToSlot(developmentCard, 0);
        ProductionLeaderCard productionLeaderCard = new ProductionLeaderCard("",4, new Requirements(new Pair<>(ResourceType.COIN, 0)), new Requirements(new Pair<>(ResourceType.SHIELD, 1)));
        playerBoard.getLeaderCards().add(productionLeaderCard);
        productionLeaderCard.activate(playerBoard);

        expectedList.add(developmentCard);
        expectedList.add(productionLeaderCard);

        assertEquals(expectedList, playerBoard.getAvailableProductions());
    }

    @Test
    void produce() throws IncompatibleDepotException {
        LeaderCard leaderCard = new ProductionLeaderCard("",4, new Requirements(new Pair<>(ResourceType.COIN, 0)), new Requirements(new Pair<>(ResourceType.SHIELD, 1)));
        playerBoard.addLeaderCard(leaderCard);
        playerBoard.activateLeaderCard(leaderCard);
        Requirements developmentCost = new Requirements(new Pair<>(ResourceType.SERVANT, 2));
        Requirements developmentGains = new Requirements(new Pair<>(ResourceType.COIN, 1), new Pair<>(ResourceType.SHIELD, 1), new Pair<>(ResourceType.STONE, 1));
        DevelopmentCard developmentCard = new DevelopmentCard("",3, new Requirements(new Pair<>(ResourceType.SHIELD, 3)), 1, DevelopmentColorType.GREEN,
                developmentCost, developmentGains);
        playerBoard.addDevelopmentCardToSlot(developmentCard, 0);
        List<Integer> choices = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            choices.add(i);
        boolean exceptionCaught;
        Requirements onDemandCost = new Requirements();
        Requirements onDemandGains = new Requirements();
        Warehouse expectedWarehouse = new Warehouse();
        Strongbox expectedStrongbox = new Strongbox();

        onDemandCost.addResourceRequirement(ResourceType.COIN, 3);
        exceptionCaught = false;
        try {
            playerBoard.produce(choices, onDemandCost, onDemandGains);
        } catch (NotSatisfiedRequirementsException e) {
            assertEquals("Wrong on demand choices", e.getMessage());
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
        assertEquals(expectedWarehouse, playerBoard.getWarehouse());
        assertEquals(expectedStrongbox, playerBoard.getStrongbox());

        onDemandCost.removeResourceRequirement(ResourceType.COIN, 1);
        onDemandGains.addResourceRequirement(ResourceType.SHIELD, 3);
        exceptionCaught = false;
        try {
            playerBoard.produce(choices, onDemandCost, onDemandGains);
        } catch (NotSatisfiedRequirementsException e) {
            assertEquals("Wrong on demand choices", e.getMessage());
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
        assertEquals(expectedWarehouse, playerBoard.getWarehouse());
        assertEquals(expectedStrongbox, playerBoard.getStrongbox());

        onDemandGains.removeResourceRequirement(ResourceType.SHIELD, 1);
        exceptionCaught = false;
        try {
            playerBoard.produce(choices, onDemandCost, onDemandGains);
        } catch (NotSatisfiedRequirementsException e) {
            assertEquals("Costs not satisfied", e.getMessage());
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
        assertEquals(expectedWarehouse, playerBoard.getWarehouse());
        assertEquals(expectedStrongbox, playerBoard.getStrongbox());


        playerBoard.getWarehouse().addResources(0, ResourceType.SHIELD, 1);
        playerBoard.getWarehouse().addResources(1, ResourceType.COIN, 2);
        playerBoard.getWarehouse().addResources(2, ResourceType.SERVANT, 2);
        expectedStrongbox.addResource(ResourceType.COIN);
        expectedStrongbox.addResource(ResourceType.STONE);
        expectedStrongbox.addResource(ResourceType.SHIELD);
        expectedStrongbox.addResource(ResourceType.SHIELD);
        expectedStrongbox.addResource(ResourceType.SHIELD);
        playerBoard.produce(choices, onDemandCost, onDemandGains);
        assertEquals(expectedWarehouse, playerBoard.getWarehouse());
        assertEquals(expectedStrongbox, playerBoard.getStrongbox());

        playerBoard.getWarehouse().addResources(1, ResourceType.COIN, 1);
        playerBoard.getWarehouse().addResources(2, ResourceType.SERVANT, 2);
        expectedStrongbox.addResource(ResourceType.STONE);
        expectedStrongbox.addResource(ResourceType.SHIELD);
        expectedStrongbox.addResource(ResourceType.SHIELD);
        playerBoard.produce(choices, onDemandCost, onDemandGains);
        assertEquals(expectedWarehouse, playerBoard.getWarehouse());
        assertEquals(expectedStrongbox, playerBoard.getStrongbox());
    }

}