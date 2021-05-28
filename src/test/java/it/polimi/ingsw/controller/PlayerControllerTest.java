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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTest {

    PlayerController playerController;
    ViewStub viewStub;
    String expectedMessage; //TODO use ViewStub instead of expectedMessage

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
        expectedMessage = null;
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
        for (int i = 0; i < /*playerController.getPlayerBoard().getDevelopmentCardSlots().length*/ 2; i++) {
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
    void getCurrentStatus() {
    }

    @Test
    void setup() {
        for (int i = 0; i < 4; i++) {
            playerController.setPlayerIndex(i);
            if (i == 0) {
                List<LeaderCard> leaderCardList = new ArrayList<>();
                for (int j = 0; j < 4; j++)
                    leaderCardList.add(playerController.getPlayerBoard().getMatch().getLeaderCards().get(j));
                expectedMessage = leaderCardList.toString();
            } else {
                int num = 1;
                if (i == 3)
                    num = 2;
                expectedMessage = "[COIN, SERVANT, SHIELD, STONE]" + num;
            }
            playerController.setup();
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
        expectedMessage = leaderCardList.toString();
        playerController.listLeaderCards();
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
        expectedMessage = "You cannot activate this card";
        playerController.activateLeaderCard(0);
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
        expectedMessage = leaderCardList.toString();

        boolean bool = false;
        try {
            playerController.chooseStartResources(resources);
        } catch (IllegalArgumentException e){
            assertEquals("Invalid number of resources of your choice", e.getMessage());
            bool = true;
        }
        assertTrue(bool);

        tearDown();
        setUp();

        playerController.setPlayerIndex(1);

        expectedMessage = "[COIN, SERVANT, SHIELD, STONE]" + 1;

        playerController.setup(); // virtualView was null
        try {
            playerController.chooseStartResources(resources);
        } catch (NullPointerException ignored) {} //virtualView is null
        assertEquals(ResourceType.COIN, playerController.getPlayerBoard().getWarehouse().getDepots().get(2).getResourceType());
        assertEquals(1, playerController.getPlayerBoard().getWarehouse().getDepots().get(2).getOccupied());

        tearDown();
        setUp();

        resources = new ResourceType[2];
        resources[0] = ResourceType.COIN;
        resources[1] = ResourceType.STONE;

        playerController.setPlayerIndex(3);

        expectedMessage = "[COIN, SERVANT, SHIELD, STONE]" + 2;

        playerController.setup();
        playerController.chooseStartResources(resources);
        assertEquals(ResourceType.COIN, playerController.getPlayerBoard().getWarehouse().getDepots().get(2).getResourceType());
        assertEquals(1, playerController.getPlayerBoard().getWarehouse().getDepots().get(2).getOccupied());
        assertEquals(ResourceType.STONE, playerController.getPlayerBoard().getWarehouse().getDepots().get(1).getResourceType());
        assertEquals(1, playerController.getPlayerBoard().getWarehouse().getDepots().get(1).getOccupied());
    }

    @Test
    void askForAction() {
        askForActionNoLeadersFormat();
        playerController.askForAction();
        LeaderCard leaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 1)),ResourceType.SHIELD);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        askForActionSomeLeadersFormat();
        playerController.askForAction();
    }

    private void askForActionNoLeadersFormat(){
        expectedMessage = playerController.getPlayerBoard().getMatch().getPlayers().stream().map(p->p.getUsername()).collect(Collectors.toList())
                + "[Take resources from market, Buy development card, Activate production, Move resources, Show a player board]";
    }

    private void askForActionSomeLeadersFormat(){
        expectedMessage = playerController.getPlayerBoard().getMatch().getPlayers().stream().map(p->p.getUsername()).collect(Collectors.toList())
                + "[Take resources from market, Buy development card, Activate production, Move resources, Activate or discard a leader card, Show a player board]";
    }

    @Test
    void askForNormalAction() {
        askForNormalActionNoLeadersFormat();
        playerController.askForNormalAction();
        LeaderCard leaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 1)),ResourceType.SHIELD);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        askForNormalActionSomeLeadersFormat();
        playerController.askForNormalAction();
    }

    private void askForNormalActionNoLeadersFormat(){
        expectedMessage = playerController.getPlayerBoard().getMatch().getPlayers().stream().map(p->p.getUsername()).collect(Collectors.toList())
                + "[Go ahead / Skip, Move resources, Show a player board]";
    }

    private void askForNormalActionSomeLeadersFormat(){
        expectedMessage = playerController.getPlayerBoard().getMatch().getPlayers().stream().map(p->p.getUsername()).collect(Collectors.toList())
                + "[Go ahead / Skip, Move resources, Activate or discard a leader card, Show a player board]";
    }

    @Test
    void rollback(){
        playerController.performAction(Action.CANCEL);
    }

    @Test
    void listPlayableLeaderCards() {
        List<LeaderCard> leaderCardList = new ArrayList<>();
        expectedMessage = leaderCardList.toString();
        playerController.performAction(Action.PLAY_LEADER);

        LeaderCard leaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 0)),ResourceType.SHIELD);
        leaderCardList.add(leaderCard);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        expectedMessage = leaderCardList.toString();
        playerController.performAction(Action.PLAY_LEADER);

        leaderCard = new DiscountLeaderCard("",2, new Requirements(new Triple<>(DevelopmentColorType.YELLOW, 1, 0)), ResourceType.SERVANT);
        leaderCardList.add(leaderCard);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        expectedMessage = leaderCardList.toString();
        playerController.performAction(Action.PLAY_LEADER);

        leaderCardList.remove(0);
        playerController.getPlayerBoard().getLeaderCards().get(0).activate(playerController.getPlayerBoard());
        expectedMessage = leaderCardList.toString();
        playerController.performAction(Action.PLAY_LEADER);
    }

    @Test
    void performAction_moveResources() throws IncompatibleDepotException {
        playerController.getPlayerBoard().getWarehouse().addResources(0, ResourceType.COIN, 1);
        playerController.getPlayerBoard().getWarehouse().addResources(2, ResourceType.STONE, 2);
        LeaderCard leaderCard = new DepotLeaderCard("",3,new Requirements(new Pair<>(ResourceType.COIN, 1)),ResourceType.SHIELD);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        playerController.getPlayerBoard().getLeaderCards().get(0).activate(playerController.getPlayerBoard());
        expectedMessage = playerController.getPlayerBoard().getWarehouse().toString();
        playerController.performAction(Action.MOVE_RESOURCES);
    }

    @Test
    void takeResourcesFromMarket(){
        expectedMessage = playerController.getPlayerBoard().getMatch().getMarket().toString();
        playerController.performAction(Action.TAKE_RESOURCES_FROM_MARKET);
    }

    @Test
    void listDevelopmentCardToBuy(){
        expectedMessage = playerController.getPlayerBoard().getMatch().getDevelopmentCardDecks().stream().map(t->t.top()).collect(Collectors.toList()).toString();
        playerController.performAction(Action.BUY_DEVELOPMENT_CARD);
    }

    @Test
    void performAction_activateProduction(){
        expectedMessage = playerController.getPlayerBoard().getAvailableProductions().toString() + playerController.getPlayerBoard().toString();
        playerController.performAction(Action.ACTIVATE_PRODUCTION);
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
    void addObserver() {
    }

    @Test
    void removeObserver() {
    }

    @Test
    void endGame() {
    }

    @Test
    void swapDepots() throws IncompatibleDepotException {
        playerController.getPlayerBoard().getWarehouse().addResources(0, ResourceType.COIN, 1);
        playerController.getPlayerBoard().getWarehouse().addResources(2, ResourceType.STONE, 1);
        askForActionNoLeadersFormat();
        playerController.swapDepots(0, 2);
        //TODO: Testare anche il lancio dell'eccezione
        // invia due messaggi, gestire multithreading
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
        expectedMessage = resources.get(resources.size()-1).toString() + warehouse;
        playerController.selectMarketRow(j-1);
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
        expectedMessage = resources.get(resources.size()-1).toString() + warehouse;
        playerController.selectMarketColumn(j-1);
    }

    @Test
    void askToStoreResourcesFromMarket() {
        Resource[] resources = new Resource[3];
        resources[0] = NonPhysicalResourceType.VOID;
        resources[1] = NonPhysicalResourceType.FAITH_POINT;
        resources[2] = ResourceType.COIN;
        expectedMessage = ResourceType.COIN.toString() + playerController.getPlayerBoard().getWarehouse();
        playerController.askToStoreResourcesFromMarket(resources);
        assertEquals(1, playerController.getPlayerBoard().getFaithTrack().getFaithMarker());

        tearDown();
        setUp();

        resources = new Resource[1];
        resources[0] = NonPhysicalResourceType.VOID;
        LeaderCard leaderCard = new WhiteMarbleLeaderCard("",5, new Requirements(new Triple<>(DevelopmentColorType.GREEN, 1, 0)), ResourceType.SHIELD, true);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        Warehouse warehouse = new Warehouse();
        expectedMessage = leaderCard.getOutputResourceType().toString() + warehouse;
        playerController.askToStoreResourcesFromMarket(resources);

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
        expectedMessage = resources[1].toString() + warehouse;
        playerController.askToStoreResource();

        warehouse = new Warehouse();
        warehouse.addResources(0, ResourceType.SHIELD, 1);
        expectedMessage = resources[0].toString() + warehouse;
        playerController.storeResourceToWarehouse(0);
    }

    @Test
    void chooseWhiteMarbleConversion() {
        Resource[] resources = new Resource[1];
        resources[0] = NonPhysicalResourceType.VOID;
        LeaderCard leaderCard = new WhiteMarbleLeaderCard("",5, new Requirements(new Triple<>(DevelopmentColorType.GREEN, 1, 0)), ResourceType.SHIELD, true);
        LeaderCard leaderCard1 = new WhiteMarbleLeaderCard("",5, new Requirements(new Triple<>(DevelopmentColorType.GREEN, 1, 0)), ResourceType.COIN, true);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard);
        playerController.getPlayerBoard().getLeaderCards().add(leaderCard1);
        expectedMessage = leaderCard.toString() + leaderCard1;
        playerController.askToStoreResourcesFromMarket(resources);
        expectedMessage = leaderCard1.getOutputResourceType().toString() + playerController.getPlayerBoard().getWarehouse();
        playerController.chooseWhiteMarbleConversion(1);
    }

    @Test
    void showPlayerBoard() {
        expectedMessage = playerController.getPlayerBoard().toString();
        playerController.showPlayerBoard();
        playerController.showPlayerBoard(playerController.getPlayerBoard());
    }

    @Test
    void buyDevelopmentCard() throws IncompatibleDepotException {
        DevelopmentCard developmentCard = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 1)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1));
        //TODO: Testare anche il lancio dell'eccezione
        // invia due messaggi, gestire multithreading
        playerController.getPlayerBoard().getWarehouse().addResources(0, ResourceType.SHIELD, 1);
        expectedMessage = Arrays.toString(playerController.getPlayerBoard().getDevelopmentCardSlots()) + developmentCard;
        playerController.buyDevelopmentCard(developmentCard);

        playerController.chooseDevelopmentCardSlot(1);
        assertEquals(PlayerController.PlayerStatus.ACTION_PERFORMED, playerController.getCurrentStatus());
    }

    @Test
    void chooseProductions() {

    }
}