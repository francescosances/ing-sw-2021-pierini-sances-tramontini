package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActionTokenTest {

    ActionToken actionToken;
    SoloMatch match;

    @BeforeEach
    void setUp() {
        match = new SoloMatch("test");
    }

    @AfterEach
    void tearDown() {
        match = null;
        actionToken = null;
    }

    @Test
    void showOneDevelopmentCard() {
        actionToken = new ActionToken(DevelopmentColorType.GREEN);
        Deck<DevelopmentCard> deck = new Deck<>();
        for (DevelopmentCard dev:match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, 1))
            deck.add(dev);
        deck.pop();
        deck.pop();
        actionToken.show(match);
        assertEquals(deck, match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, 1));
    }

    @Test
    void showThreeDevelopmentCards() {
        actionToken = new ActionToken(DevelopmentColorType.GREEN);
        Deck<DevelopmentCard> deck = new Deck<>();
        for (DevelopmentCard dev:match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, 2))
            deck.add(dev);
        deck.pop();
        deck.pop();
        for (int i = 0; i < 3; i++)
            actionToken.show(match);
        assertTrue(match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, 1).isEmpty());
        assertEquals(deck, match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, 2));
    }

    @Test
    void showTwoBlackCrossSpaces(){
        actionToken = new ActionToken(2);
        assertEquals(0, match.getBlackCross().getFaithMarker());
        actionToken.show(match);
        assertEquals(2, match.getBlackCross().getFaithMarker());
    }
}