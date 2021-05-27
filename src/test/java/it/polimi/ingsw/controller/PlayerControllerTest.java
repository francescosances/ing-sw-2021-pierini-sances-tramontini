package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.view.ObservableFromView;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.VirtualView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTest implements FakeViewTester {

    PlayerController playerController;
    String expectedMessage;

    @BeforeEach
    void setUp() {
        Match match = new Match("Test");
        PlayerBoard playerBoard = new PlayerBoard("Test", match);
        playerController = new PlayerController(playerBoard.getUsername(), playerBoard, new FakeView(this));
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
            expectedMessage = Arrays.toString(developmentCardSlots) + developmentCard;
            playerController.buyDevelopmentCard(developmentCard);
            playerController.deactivate();
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
    void askToChooseStartResources() {
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
    }

    @Test
    void performAction() {
    }

    @Test
    void startNormalAction() {
    }

    @Test
    void askForNormalAction() {
    }

    @Test
    void listPlayableLeaderCards() {
    }

    @Test
    void showWarehouseStatus() {
    }

    @Test
    void addObserver() {
    }

    @Test
    void removeObserver() {
    }

    @Test
    void getPlayerBoard() {
    }

    @Test
    void endGame() {
    }

    @Test
    void swapDepots() {
    }

    @Test
    void selectMarketRow() {
    }

    @Test
    void selectMarketColumn() {
    }

    @Test
    void askToStoreResourcesFromMarket() {
    }

    @Test
    void takeResourcesFromMarket() {
    }

    @Test
    void askToStoreResource() {
    }

    @Test
    void chooseWhiteMarbleConversion() {
    }

    @Test
    void askToConfirmDepot() {
    }

    @Test
    void storeResourceToWarehouse() {
    }

    @Test
    void showPlayerBoard() {
    }

    @Test
    void testShowPlayerBoard() {
    }

    @Test
    void buyDevelopmentCard() {
    }

    @Test
    void chooseDevelopmentCardSlot() {
    }

    @Test
    void chooseProductions() {
    }

    @Test
    void setPlayerIndex() {
    }

    @Override
    public void testMessage(String providedMessage){
        assertEquals(expectedMessage, providedMessage);
    }
}