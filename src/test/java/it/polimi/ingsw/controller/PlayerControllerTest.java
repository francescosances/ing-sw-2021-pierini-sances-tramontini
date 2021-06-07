package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.MarbleType;
import it.polimi.ingsw.model.Market;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.VirtualView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTest {

    PlayerController playerController;
    ViewStub viewStub;

    @BeforeEach
    void setUp() {
        String username = "Test";
        Match match = new Match(username);
        viewStub = new ViewStub();
        playerController = new PlayerController(username, match.addPlayer(username), viewStub);
        playerController.active = true;
    }

    @AfterEach
    void tearDown() {
        playerController = null;
        assertTrue(viewStub.isEmpty());
        viewStub = null;
    }

    @Test
    void activate() {
        playerController.active = false;
        playerController.activate();
        assertTrue(playerController.isActive());
        assertEquals(PlayerController.PlayerStatus.TURN_ENDED, playerController.getCurrentStatus());
    }

    @Test
    void deactivate() {
        playerController.activate();
        playerController.deactivate();
        assertFalse(playerController.isActive());
        assertEquals(PlayerController.PlayerStatus.TURN_ENDED, playerController.getCurrentStatus());
        DevelopmentCardSlot[] developmentCardSlots = new DevelopmentCardSlot[3];
        for (int j = 0; j < developmentCardSlots.length; j++)
            developmentCardSlots[j] = new DevelopmentCardSlot();
        Requirements cost = new Requirements();
        DevelopmentCard developmentCard = new DevelopmentCard("", 1, cost, 1, DevelopmentColorType.GREEN, null, (Requirements) null);
        for (int i = 0; i < 2; i++) {
            playerController.activate();
            playerController.buyDevelopmentCard(developmentCard);
            playerController.deactivate();
            assertEquals(Arrays.toString(developmentCardSlots) + developmentCard, viewStub.popMessage());
            developmentCardSlots[i].addCard(developmentCard);
            assertEquals(developmentCard, playerController.getPlayerBoard().getDevelopmentCardSlots()[i].getTopCard());
        }
    }

    @Test
    void startTurn_turnEnded() {
        assertEquals(playerController.getCurrentStatus(), PlayerController.PlayerStatus.PERFORMING_ACTION);
        playerController.turnEnded();
        assertEquals(playerController.getCurrentStatus(), PlayerController.PlayerStatus.TURN_ENDED);
        playerController.startTurn();
        assertEquals(playerController.getCurrentStatus(), PlayerController.PlayerStatus.PERFORMING_ACTION);
    }

    @Test
    void setVirtualView() {
        VirtualView virtualView = new VirtualView(null, playerController.username);
        playerController.setView(virtualView);
        assertEquals(virtualView, playerController.getView());
    }

    @Test
    void setup() {
        String expectedMessage;
        for (int i = 0; i < 4; i++) {
            playerController.setPlayerIndex(i);
            if (i == 0) {
                List<LeaderCard> leaderCardList = new ArrayList<>();
                for (int j = 0; j < 4; j++)
                    leaderCardList.add(playerController.getPlayerBoard().getMatch().getLeaderCards().get(j));
                expectedMessage = leaderCardList.toString() + 2;
            } else {
                int num = 1;
                if (i == 3)
                    num = 2;
                expectedMessage = "[COIN, SERVANT, SHIELD, STONE]" + num;
            }
            playerController.setup();
            assertEquals(expectedMessage, viewStub.popMessage());
            assertEquals(i / 2, playerController.getPlayerBoard().getFaithTrack().getFaithMarker());
            tearDown();
            setUp();
        }

    }

    @Test
    void defaultSetup() {
        playerController.setPlayerIndex(2);
        playerController.deactivate();
        playerController.setup();
        assertEquals(1, playerController.getPlayerBoard().getFaithTrack().getFaithMarker());
        assertEquals(1, playerController.getPlayerBoard().getWarehouse().getDepots().get(2).getOccupied());
        assertEquals(2, playerController.getPlayerBoard().getLeaderCards().size());
    }

    @Test
    void listAndChooseLeaderCards() {
        List<LeaderCard> leaderCardList = new ArrayList<>();
        for (int j = 0; j < 4; j++)
            leaderCardList.add(playerController.getPlayerBoard().getMatch().getLeaderCards().get(j));
        playerController.listLeaderCards();
        assertEquals(leaderCardList.toString() + 2,viewStub.popMessage());
        leaderCardList.subList(0, 2).clear();
        playerController.chooseLeaderCards(leaderCardList.get(0), leaderCardList.get(1));
        assertEquals(leaderCardList.get(0), playerController.getPlayerBoard().getLeaderCards().get(0));
        assertEquals(leaderCardList.get(1), playerController.getPlayerBoard().getLeaderCards().get(1));
    }

    @Test
    void discardLeaderCard() {
        playerController.setPlayerIndex(0);
        playerController.deactivate();
        playerController.setup();
        LeaderCard leaderCard = playerController.getPlayerBoard().getLeaderCards().get(0);
        playerController.discardLeaderCard(0);
        assertTrue(playerController.getPlayerBoard().getLeaderCards().stream().noneMatch(c->c.equals(leaderCard)));
    }

    @Test
    void activateLeaderCard() throws IncompatibleDepotException {
        LeaderCard leaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 1)),ResourceType.SHIELD);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        playerController.activateLeaderCard(0);
        assertEquals("You cannot activate this card", viewStub.popMessage());
        assertFalse(playerController.getPlayerBoard().getLeaderCards().get(0).isActive());
        playerController.getPlayerBoard().getWarehouse().getDepots().get(0).addResource(ResourceType.COIN);
        playerController.activateLeaderCard(0);
        assertTrue(playerController.getPlayerBoard().getLeaderCards().get(0).isActive());
    }

    @Test
    void chooseStartResources() {
        ResourceType[] resources = new ResourceType[1];
        resources[0] = ResourceType.COIN;
        playerController.setPlayerIndex(0);
        playerController.setup();

        List<LeaderCard> leaderCardList = new ArrayList<>();
        for (int j = 0; j < 4; j++)
            leaderCardList.add(playerController.getPlayerBoard().getMatch().getLeaderCards().get(j));

        boolean bool = false;
        try {
            playerController.chooseStartResources(resources);
        } catch (IllegalArgumentException e){
            assertEquals("Invalid number of resources of your choice", e.getMessage());
            bool = true;
        }
        assertTrue(bool);

        assertEquals(leaderCardList.toString() + 2, viewStub.popMessage());

        tearDown();
        setUp();

        playerController.setPlayerIndex(1);

        leaderCardList = new ArrayList<>();
        for (int j = 0; j < 4; j++)
            leaderCardList.add(playerController.getPlayerBoard().getMatch().getLeaderCards().get(j));

        playerController.setup();
        assertEquals("[COIN, SERVANT, SHIELD, STONE]" + 1, viewStub.popMessage());

        playerController.chooseStartResources(resources);
        assertEquals(ResourceType.COIN, playerController.getPlayerBoard().getWarehouse().getDepots().get(2).getResourceType());
        assertEquals(1, playerController.getPlayerBoard().getWarehouse().getDepots().get(2).getOccupied());

        assertEquals(leaderCardList.toString() + 2, viewStub.popMessage());

        tearDown();
        setUp();

        resources = new ResourceType[2];
        resources[0] = ResourceType.COIN;
        resources[1] = ResourceType.STONE;

        playerController.setPlayerIndex(3);

        playerController.setup();
        assertEquals("[COIN, SERVANT, SHIELD, STONE]" + 2, viewStub.popMessage());

        leaderCardList = new ArrayList<>();
        for (int j = 0; j < 4; j++)
            leaderCardList.add(playerController.getPlayerBoard().getMatch().getLeaderCards().get(j));

        playerController.chooseStartResources(resources);
        assertEquals(ResourceType.COIN, playerController.getPlayerBoard().getWarehouse().getDepots().get(2).getResourceType());
        assertEquals(1, playerController.getPlayerBoard().getWarehouse().getDepots().get(2).getOccupied());
        assertEquals(ResourceType.STONE, playerController.getPlayerBoard().getWarehouse().getDepots().get(1).getResourceType());
        assertEquals(1, playerController.getPlayerBoard().getWarehouse().getDepots().get(1).getOccupied());
        assertEquals(leaderCardList.toString() + 2, viewStub.popMessage());
    }

    @Test
    void askForAction() {
        playerController.askForAction();
        assertEquals(askForActionNoLeadersFormat(), viewStub.popMessage());
        LeaderCard leaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 1)),ResourceType.SHIELD);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        playerController.askForAction();
        assertEquals(askForActionSomeLeadersFormat(), viewStub.popMessage());
    }

    private String askForActionNoLeadersFormat(){
        return playerController.getPlayerBoard().getMatch().getPlayers().stream().map(p->p.getUsername()).collect(Collectors.toList())
                + Arrays.toString(Arrays.stream(Action.ALL_ACTIONS).filter(x -> !((x == Action.PLAY_LEADER))).toArray());
    }

    private String askForActionSomeLeadersFormat(){
        return playerController.getPlayerBoard().getMatch().getPlayers().stream().map(p->p.getUsername()).collect(Collectors.toList())
                + Arrays.toString(Action.ALL_ACTIONS);
    }

    @Test
    void askForNormalAction() {
        playerController.askForNormalAction();
        assertEquals(askForNormalActionNoLeadersFormat(), viewStub.popMessage());
        LeaderCard leaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 1)),ResourceType.SHIELD);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        playerController.askForNormalAction();
        assertEquals(askForNormalActionSomeLeadersFormat(), viewStub.popMessage());
    }

    private String askForNormalActionNoLeadersFormat(){
        return playerController.getPlayerBoard().getMatch().getPlayers().stream().map(p->p.getUsername()).collect(Collectors.toList())
                + Arrays.toString(Arrays.stream(Action.NORMAL_ACTIONS).filter(x -> !((x == Action.PLAY_LEADER))).toArray());
    }

    private String askForNormalActionSomeLeadersFormat(){
        return playerController.getPlayerBoard().getMatch().getPlayers().stream().map(p->p.getUsername()).collect(Collectors.toList())
                + Arrays.toString(Action.NORMAL_ACTIONS);
    }

    @Test
    void rollback(){
        playerController.performAction(Action.CANCEL);
    }

    @Test
    void listPlayableLeaderCards() {
        List<LeaderCard> leaderCardList = new ArrayList<>();
        playerController.performAction(Action.PLAY_LEADER);
        assertEquals(leaderCardList.toString(), viewStub.popMessage());

        LeaderCard leaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 0)),ResourceType.SHIELD);
        leaderCardList.add(leaderCard);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        playerController.performAction(Action.PLAY_LEADER);
        assertEquals(leaderCardList.toString(), viewStub.popMessage());

        leaderCard = new DiscountLeaderCard("",2, new Requirements(new Triple<>(DevelopmentColorType.YELLOW, 1, 0)), ResourceType.SERVANT);
        leaderCardList.add(leaderCard);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        playerController.performAction(Action.PLAY_LEADER);
        assertEquals(leaderCardList.toString(), viewStub.popMessage());

        leaderCardList.remove(0);
        playerController.getPlayerBoard().getLeaderCards().get(0).activate(playerController.getPlayerBoard());
        playerController.performAction(Action.PLAY_LEADER);
        assertEquals(leaderCardList.toString(), viewStub.popMessage());
    }

    @Test
    void performAction_moveResources() throws IncompatibleDepotException {
        playerController.getPlayerBoard().getWarehouse().addResources(0, ResourceType.COIN, 1);
        playerController.getPlayerBoard().getWarehouse().addResources(2, ResourceType.STONE, 2);
        LeaderCard leaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 1)),ResourceType.SHIELD);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        playerController.getPlayerBoard().getLeaderCards().get(0).activate(playerController.getPlayerBoard());
        playerController.performAction(Action.MOVE_RESOURCES);
        assertEquals(playerController.getPlayerBoard().getWarehouse().toString(), viewStub.popMessage());
    }

    @Test
    void takeResourcesFromMarket(){
        playerController.performAction(Action.TAKE_RESOURCES_FROM_MARKET);
        assertEquals(playerController.getPlayerBoard().getMatch().getMarket().toString(), viewStub.popMessage());
    }

    @Test
    void listDevelopmentCardToBuy(){
        playerController.performAction(Action.BUY_DEVELOPMENT_CARD);
        String message = viewStub.popMessage();
        message = adjustMessage(message); //comment this line if testing only this method
        assertEquals(playerController.getPlayerBoard().getMatch().getDevelopmentCardDecks().toString()
                + 1 + playerController.getPlayerBoard(), message);
    }

    private String adjustMessage(String message){
        message = message.replace("cost = [1 STONE, 1 SERVANT, 1 SHIELD]", "cost = [1 SERVANT, 1 STONE, 1 SHIELD]");
        message = message.replace("productionCost = [1 STONE, 1 SERVANT]", "productionCost = [1 SERVANT, 1 STONE]");
        message = message.replace("productionCost = [1 COIN, 1 SERVANT]", "productionCost = [1 SERVANT, 1 COIN]");
        message = message.replace("productionGain = [2 STONE, 1 FAITH_POINT, 2 SHIELD]", "productionGain = [1 FAITH_POINT, 2 STONE, 2 SHIELD]");
        message = message.replace("cost = [1 STONE, 1 SERVANT, 1 COIN]", "cost = [1 SERVANT, 1 STONE, 1 COIN]");
        message = message.replace("cost = [2 COIN, 2 SERVANT]", "cost = [2 SERVANT, 2 COIN]");
        message = message.replace("productionGain = [1 FAITH_POINT, 2 SERVANT]", "productionGain = [2 SERVANT, 1 FAITH_POINT]");
        message = message.replace("productionGain = [2 STONE, 2 SERVANT, 1 FAITH_POINT]", "productionGain = [2 SERVANT, 1 FAITH_POINT, 2 STONE]");
        message = message.replace("productionCost = [1 COIN, 1 SERVANT]", "productionCost = [1 SERVANT, 1 COIN]");
        message = message.replace("cost = [3 STONE, 3 SERVANT]", "cost = [3 SERVANT, 3 STONE]");
        message = message.replace("productionGain = [2 FAITH_POINT, 2 SERVANT]", "productionGain = [2 SERVANT, 2 FAITH_POINT]");
        message = message.replace("productionGain = [1 STONE, 1 SERVANT, 1 COIN]", "productionGain = [1 SERVANT, 1 STONE, 1 COIN]");
        message = message.replace("cost = [5 STONE, 2 SERVANT]", "cost = [2 SERVANT, 5 STONE]");
        message = message.replace("productionCost = [1 STONE, 1 SERVANT]", "productionCost = [1 SERVANT, 1 STONE]");
        message = message.replace("productionGain = [3 FAITH_POINT, 1 SERVANT]", "productionGain = [1 SERVANT, 3 FAITH_POINT]");
        message = message.replace("cost = [4 STONE, 4 SERVANT]", "cost = [4 SERVANT, 4 STONE]");
        message = message.replace("productionGain = [1 STONE, 3 SERVANT]", "productionGain = [3 SERVANT, 1 STONE]");
        message = message.replace("productionGain = [2 FAITH_POINT, 3 SERVANT]", "productionGain = [3 SERVANT, 2 FAITH_POINT]");
        message = message.replace("productionGain = [1 STONE, 1 SERVANT, 1 SHIELD]","productionGain = [1 SERVANT, 1 STONE, 1 SHIELD]");
        message = message.replace("cost = [2 STONE, 2 SERVANT]","cost = [2 SERVANT, 2 STONE]");
        message = message.replace("productionGain = [1 FAITH_POINT, 2 SERVANT]","productionGain = [2 SERVANT, 1 FAITH_POINT]");
        message = message.replace("cost = [2 COIN, 3 SERVANT]","cost = [3 SERVANT, 2 COIN]");
        message = message.replace("productionCost = [1 COIN, 1 SERVANT]","productionCost = [1 SERVANT, 1 COIN]");
        message = message.replace("productionGain = [3 STONE, 1 SERVANT]","productionGain = [1 SERVANT, 3 STONE]");
        message = message.replace("cost = [2 COIN, 5 SERVANT]","cost = [5 SERVANT, 2 COIN]");
        return message;
    }

    @Test
    void performAction_activateProduction(){
        playerController.performAction(Action.ACTIVATE_PRODUCTION);
        assertEquals(playerController.getPlayerBoard().getAvailableProductions().toString() + playerController.getPlayerBoard().toString(), viewStub.popMessage());
    }

    @Test
    void next_status(){
        assertEquals(PlayerController.PlayerStatus.PERFORMING_ACTION, playerController.getCurrentStatus());
        playerController.performAction(Action.SKIP);
        assertEquals(PlayerController.PlayerStatus.ACTION_PERFORMED, playerController.getCurrentStatus());
        playerController.performAction(Action.SKIP);
        assertEquals(PlayerController.PlayerStatus.TURN_ENDED, playerController.getCurrentStatus());
        playerController.performAction(Action.SKIP);
        assertEquals(PlayerController.PlayerStatus.PERFORMING_ACTION, playerController.getCurrentStatus());
    }

    @Test
    void endGame() {
        //TODO
    }

    @Test
    void swapDepots() throws IncompatibleDepotException {
        playerController.getPlayerBoard().getWarehouse().addResources(0, ResourceType.COIN, 1);
        playerController.getPlayerBoard().getWarehouse().addResources(2, ResourceType.STONE, 1);
        playerController.swapDepots(0, 2);
        assertEquals(askForActionNoLeadersFormat(), viewStub.popMessage());
        playerController.getPlayerBoard().getWarehouse().addResources(2, ResourceType.COIN, 1);
        playerController.swapDepots(0, 2);
        assertEquals("Unable to swap selected depots", viewStub.popMessage());
        assertEquals(askForActionNoLeadersFormat(), viewStub.popMessage());
    }

    @Test
    void selectMarketRow() {
        Market market = playerController.getPlayerBoard().getMatch().getMarket();
        List<MarbleType> marbles = new ArrayList<>();
        int j = 0;
        do {
            for (int i = 0; i < Market.COLUMNS; i++) {
                MarbleType marbleType = market.getMarble(j, i);
                if (!(marbleType == MarbleType.RED || marbleType == MarbleType.WHITE))
                    marbles.add(marbleType);
            }
            j++;
        } while (marbles.isEmpty());
        List<Resource> resources = marbles.stream().map(MarbleType::toResource).collect(Collectors.toList());
        Warehouse warehouse = new Warehouse();
        warehouse.toBeStored(resources.subList(0, resources.size()-1).toArray(Resource[]::new));
        playerController.selectMarketRow(j-1);
        assertEquals(resources.get(resources.size()-1).toString() + warehouse, viewStub.popMessage());
    }

    @Test
    void selectMarketColumn() {
        Market market = playerController.getPlayerBoard().getMatch().getMarket();
        List<MarbleType> marbles = new ArrayList<>();
        int j = 0;
        do {
            for (int i = 0; i < Market.ROWS; i++) {
                MarbleType marbleType = market.getMarble(i, j);
                if (!(marbleType == MarbleType.RED || marbleType == MarbleType.WHITE))
                    marbles.add(marbleType);
            }
            j++;
        } while (marbles.isEmpty());
        List<Resource> resources = marbles.stream().map(MarbleType::toResource).collect(Collectors.toList());
        Warehouse warehouse = new Warehouse();
        warehouse.toBeStored(resources.subList(0, resources.size()-1).toArray(Resource[]::new));
        playerController.selectMarketColumn(j-1);
        assertEquals(resources.get(resources.size()-1).toString() + warehouse, viewStub.popMessage());
    }

    @Test
    void askToStoreResourcesFromMarket() {
        Resource[] resources = new Resource[3];
        resources[0] = NonPhysicalResourceType.VOID;
        resources[1] = NonPhysicalResourceType.FAITH_POINT;
        resources[2] = ResourceType.COIN;
        playerController.askToStoreResourcesFromMarket(resources);
        assertEquals(1, playerController.getPlayerBoard().getFaithTrack().getFaithMarker());
        assertEquals(ResourceType.COIN.toString() + playerController.getPlayerBoard().getWarehouse(), viewStub.popMessage());

        tearDown();
        setUp();

        resources = new Resource[1];
        resources[0] = NonPhysicalResourceType.VOID;
        LeaderCard leaderCard = new WhiteMarbleLeaderCard("",5, new Requirements(new Triple<>(DevelopmentColorType.GREEN, 1, 0)), ResourceType.SHIELD, true);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        Warehouse warehouse = new Warehouse();
        playerController.askToStoreResourcesFromMarket(resources);
        assertEquals(leaderCard.getOutputResourceType().toString() + warehouse, viewStub.popMessage());

        tearDown();
        setUp();

        resources = new Resource[2];
        resources[0] = NonPhysicalResourceType.VOID;
        resources[1] = NonPhysicalResourceType.FAITH_POINT;
        playerController.askToStoreResourcesFromMarket(resources);
        assertEquals(1, playerController.getPlayerBoard().getFaithTrack().getFaithMarker());
        assertEquals(PlayerController.PlayerStatus.ACTION_PERFORMED, playerController.getCurrentStatus());
    }

    @Test
    void askToStoreResource() throws IncompatibleDepotException {
        Resource[] resources = new Resource[2];
        resources[0] = ResourceType.SERVANT;
        resources[1] = ResourceType.SHIELD;
        playerController.getPlayerBoard().getWarehouse().toBeStored(resources);
        Warehouse warehouse = new Warehouse();
        Resource[] resources1 = new Resource[1];
        resources1[0] = ResourceType.SERVANT;
        warehouse.toBeStored(resources1);
        playerController.askToStoreResource();
        assertEquals(resources[1].toString() + warehouse, viewStub.popMessage());

        warehouse = new Warehouse();
        warehouse.addResources(0, ResourceType.SHIELD, 1);
        playerController.storeResourceToWarehouse(0);
        assertEquals(resources[0].toString() + warehouse, viewStub.popMessage());
    }

    @Test
    void chooseWhiteMarbleConversion() {
        Resource[] resources = new Resource[1];
        resources[0] = NonPhysicalResourceType.VOID;
        LeaderCard leaderCard = new WhiteMarbleLeaderCard("",5, new Requirements(new Triple<>(DevelopmentColorType.GREEN, 1, 0)), ResourceType.SHIELD, true);
        LeaderCard leaderCard1 = new WhiteMarbleLeaderCard("",5, new Requirements(new Triple<>(DevelopmentColorType.GREEN, 1, 0)), ResourceType.COIN, true);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard1);
        playerController.askToStoreResourcesFromMarket(resources);
        assertEquals(leaderCard.toString() + leaderCard1, viewStub.popMessage());
        playerController.chooseWhiteMarbleConversion(1);
        assertEquals(leaderCard1.getOutputResourceType().toString() + playerController.getPlayerBoard().getWarehouse(), viewStub.popMessage());
    }

    @Test
    void showPlayerBoard() {
        playerController.showPlayerBoard();
        assertEquals(playerController.getPlayerBoard().toString(), viewStub.popMessage());

        playerController.showPlayerBoard(playerController.getPlayerBoard());
        assertEquals(playerController.getPlayerBoard().toString(), viewStub.popMessage());
    }

    @Test
    void buyDevelopmentCard() throws IncompatibleDepotException {
        DevelopmentCard developmentCard = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 1)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1));
        playerController.getPlayerBoard().getWarehouse().addResources(0, ResourceType.SHIELD, 1);
        playerController.buyDevelopmentCard(developmentCard);
        assertEquals(Arrays.toString(playerController.getPlayerBoard().getDevelopmentCardSlots()) + developmentCard, viewStub.popMessage());

        playerController.chooseDevelopmentCardSlot(1);
        assertEquals(PlayerController.PlayerStatus.ACTION_PERFORMED, playerController.getCurrentStatus());

        tearDown();
        setUp();

        playerController.buyDevelopmentCard(developmentCard);
        assertEquals("You cannot buy this card", viewStub.popMessage());
        String message = viewStub.popMessage();
        message = adjustMessage(message); //comment this line if testing only this method
        assertEquals(playerController.getPlayerBoard().getMatch().getDevelopmentCardDecks().toString()
                + 1 + playerController.getPlayerBoard(), message);
        assertEquals(PlayerController.PlayerStatus.PERFORMING_ACTION, playerController.getCurrentStatus());
    }

    @Test
    void chooseProductions() {
        //TODO
    }
}