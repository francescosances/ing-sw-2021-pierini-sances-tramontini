package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.model.cards.Requirements;
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
    }

    @AfterEach
    void tearDown() {
        playerController = null;
    }

    @Test
    void activate() {
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
    void startTurn() {
    }

    @Test
    void turnEnded() {
    }

    @Test
    void getVirtualView() {
    }

    @Test
    void setVirtualView() {
    }

    @Test
    void getCurrentStatus() {
    }

    @Test
    void setup() {
    }

    @Test
    void defaultSetup() {
    }

    @Test
    void listLeaderCards() {
    }

    @Test
    void chooseLeaderCards() {
    }

    @Test
    void discardLeaderCard() {
    }

    @Test
    void activateLeaderCard() {
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