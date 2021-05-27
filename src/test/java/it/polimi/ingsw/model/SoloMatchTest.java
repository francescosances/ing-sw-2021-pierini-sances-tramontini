package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SoloMatchTest {

    SoloMatch match;
    PlayerBoard player;

    @BeforeEach
    void setUp() {
        match = new SoloMatch("test");
        player = new PlayerBoard("test", match);
        match.addPlayer("test");
    }

    @AfterEach
    void tearDown() {
        match = null;
        player = null;
    }

    @Test
    void moveBlackCross() {
        assertEquals(0, match.getBlackCross().getFaithMarker());
        match.moveBlackCross(2);
        assertEquals(2, match.getBlackCross().getFaithMarker());
    }

    @Test
    void discardDevelopmentCards() {
        Deck<DevelopmentCard> deck = new Deck<>();
        for (DevelopmentCard card:match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, 1))
            deck.add(card);

        for (int i = 0; i < 2; i++)
            deck.pop();

        match.discardDevelopmentCards(DevelopmentColorType.GREEN);
        assertEquals(deck, match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, 1));
    }

    @Test
    void discardResource() {
        assertEquals(0, match.getBlackCross().getFaithMarker());
        match.discardResource(player);
        assertEquals(1, match.getBlackCross().getFaithMarker());
    }
}