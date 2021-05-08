package it.polimi.ingsw.model.cards;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

class DevelopmentCardSlotTest {

    DevelopmentCardSlot developmentCardSlot;

    @BeforeEach
    void setUp() {
        developmentCardSlot = new DevelopmentCardSlot();
    }

    @AfterEach
    void tearDown() {
        developmentCardSlot = null;
    }

    @Test
    void getTopCard() {
        DevelopmentCard developmentCard = new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard);
        assertEquals(developmentCard, developmentCardSlot.getTopCard());
        developmentCard = new DevelopmentCard("",1, null, 2, DevelopmentColorType.BLUE, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard);
        assertEquals(developmentCard, developmentCardSlot.getTopCard());
        developmentCard = new DevelopmentCard("",1, null, 3, DevelopmentColorType.YELLOW, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard);
        assertEquals(developmentCard, developmentCardSlot.getTopCard());
    }

    @Test
    void accepts() {
        DevelopmentCard developmentCard = new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null);
        Assertions.assertTrue(developmentCardSlot.accepts(developmentCard));
        developmentCardSlot.addCard(developmentCard);
        Assertions.assertFalse(developmentCardSlot.accepts(developmentCard));
        developmentCard = new DevelopmentCard("",1, null, 2, DevelopmentColorType.GREEN, null, (Requirements) null);
        Assertions.assertTrue(developmentCardSlot.accepts(developmentCard));
        developmentCard = new DevelopmentCard("",1, null, 3, DevelopmentColorType.GREEN, null, (Requirements) null);
        Assertions.assertFalse(developmentCardSlot.accepts(developmentCard));
        developmentCard = new DevelopmentCard("",1, null, 2, DevelopmentColorType.GREEN, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard);
        developmentCard = new DevelopmentCard("",1, null, 3, DevelopmentColorType.GREEN, null, (Requirements) null);
        Assertions.assertTrue(developmentCardSlot.accepts(developmentCard));
        developmentCard = new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null);
        Assertions.assertFalse(developmentCardSlot.accepts(developmentCard));
        developmentCard = new DevelopmentCard("",1, null, 3, DevelopmentColorType.GREEN, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard);
        developmentCard = new DevelopmentCard("",1, null, 4, DevelopmentColorType.GREEN, null, (Requirements) null);
        Assertions.assertFalse(developmentCardSlot.accepts(developmentCard));
    }

    @Test
    void getCardsNum() {
        DevelopmentCard developmentCard = new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard);
        developmentCard = new DevelopmentCard("",1, null, 2, DevelopmentColorType.GREEN, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard);
        assertEquals(1, developmentCardSlot.getCardsNum(DevelopmentColorType.GREEN, 2));
        assertEquals(1, developmentCardSlot.getCardsNum(DevelopmentColorType.GREEN, 1));
        assertEquals(0, developmentCardSlot.getCardsNum(DevelopmentColorType.GREEN, 3));
        assertEquals(2, developmentCardSlot.getCardsNum(DevelopmentColorType.GREEN, 0));
        assertEquals(0, developmentCardSlot.getCardsNum(DevelopmentColorType.BLUE, 0));
    }

    @Test
    void getFromLevel() {
        DevelopmentCard developmentCard1 = new DevelopmentCard("",1, null, 1, DevelopmentColorType.GREEN, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard1);
        DevelopmentCard developmentCard2 = new DevelopmentCard("",1, null, 2, DevelopmentColorType.BLUE, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard2);
        DevelopmentCard developmentCard3 = new DevelopmentCard("",1, null, 3, DevelopmentColorType.YELLOW, null, (Requirements) null);
        developmentCardSlot.addCard(developmentCard3);
        assertEquals(developmentCard1, developmentCardSlot.getFromLevel(1));
        assertEquals(developmentCard2, developmentCardSlot.getFromLevel(2));
        assertEquals(developmentCard3, developmentCardSlot.getFromLevel(3));
    }

    @Test
    void getVictoryPoints() {
        DevelopmentCard developmentCard;
        assertEquals(0, developmentCardSlot.getVictoryPoints());
        for (int i = 1; i < 4; i++){
            developmentCard = new DevelopmentCard("",1, null, i,DevelopmentColorType.GREEN, null, (Requirements) null);
            developmentCardSlot.addCard(developmentCard);
            assertEquals(i, developmentCardSlot.getVictoryPoints());

        }
    }
}