package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.view.VirtualView;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTest {

    PlayerController playerController;

    @BeforeEach
    void setUp() {
        Match match = new Match("Test");
        PlayerBoard playerBoard = new PlayerBoard("Test", match);
        playerController = new PlayerController(playerBoard.getUsername(), playerBoard, null);
        playerController.active = true;
    }

    @AfterEach
    void tearDown() {
        playerController = null;
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
        for (int i = 0; i < playerController.getPlayerBoard().getDevelopmentCardSlots().length; i++) {
            playerController.activate();
            Requirements cost = new Requirements();
            DevelopmentCard developmentCard = new DevelopmentCard("", 1, cost, 1, DevelopmentColorType.GREEN, null, (Requirements) null);
            try {
                playerController.buyDevelopmentCard(developmentCard);
            } catch (NullPointerException ignored) {
            } //virtual view is null
            playerController.deactivate();
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
        try {
            for (int i = 0; i <= 3; i++) {
                playerController.setPlayerIndex(i);
                playerController.setup();
                assertEquals(i / 2, playerController.getPlayerBoard().getFaithTrack().getFaithMarker());
            }
        } catch (NullPointerException ignored){} //virtualView is null
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
    void listLeaderCards() {
    }

    @Test
    void chooseLeaderCards() {
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
        try{
            playerController.activateLeaderCard(0);
        } catch (NullPointerException ignored) {} //virtualView is null
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
}