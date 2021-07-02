package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Deck<DevelopmentCard>> deckList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            deckList.add(new Deck<>());
            for (DevelopmentCard card : match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, i+1))
                deckList.get(i).add(card);
        }

        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 2; i++)
                deckList.get(j/2).pop();

            match.discardDevelopmentCards(DevelopmentColorType.GREEN);
            for (int i = 0; i < 3; i++)
                assertEquals(deckList.get(i), match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, i + 1));
        }

        boolean exceptionCaught = false;
        try {
            match.discardDevelopmentCards(DevelopmentColorType.GREEN);
        } catch (EndGameException e){
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);

        for (int i = 0; i < 3; i++)
            assertTrue(match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, i + 1).isEmpty());
    }

    @Test
    void discardResource() {
        assertEquals(0, match.getBlackCross().getFaithMarker());
        match.discardResource(player);
        assertEquals(1, match.getBlackCross().getFaithMarker());
    }

    @Test
    void buyDevelopmentCard() {
        Map<ResourceType, Integer> resources = new HashMap<>();
        resources.put(ResourceType.COIN, 50);
        resources.put(ResourceType.SHIELD, 50);
        resources.put(ResourceType.SERVANT, 50);
        resources.put(ResourceType.STONE, 50);
        player.getStrongbox().addResources(resources);
        for (int i = 1; i < 3; i++) {
            while (!match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, i).isEmpty())
                match.buyDevelopmentCard(match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, i).top(), player);
        }

        boolean exceptionCaught = false;
        try {
            while (!match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, 3).isEmpty())
                match.buyDevelopmentCard(match.getDevelopmentCardDeck(DevelopmentColorType.GREEN, 3).top(), player);
        } catch (EndGameException e){
            assertTrue(match.anEmptyDeck());
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }
}