package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.cards.Requirements;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class StrongboxTest {

    Strongbox strongbox;

    @BeforeEach
    void setUp() {
        strongbox = new Strongbox();
    }

    @AfterEach
    void tearDown() {
        strongbox = null;
    }

    @Test
    void addResources() {
        Map<ResourceType, Integer> map =  new HashMap<>();
        map.put(ResourceType.COIN, 2);
        map.put(ResourceType.STONE, 3);
        strongbox.addResources(map);
        assertEquals(2, strongbox.getResourcesNum(ResourceType.COIN));
        assertEquals(3, strongbox.getResourcesNum(ResourceType.STONE));
        Map <Resource, Integer> temp = new HashMap<>();
        for (ResourceType rt:map.keySet())
            temp.put(rt, map.get(rt));
        Requirements requirements = new Requirements(temp);
        assertEquals(requirements, strongbox.getAllResources());
    }

    @Test
    void addResource() {
        for (int i = 0; i < 2; i++)
            strongbox.addResource(ResourceType.COIN);
        for (int i = 0; i < 3; i++)
            strongbox.addResource(ResourceType.STONE);
        assertEquals(2, strongbox.getResourcesNum(ResourceType.COIN));
        assertEquals(3, strongbox.getResourcesNum(ResourceType.STONE));
        Map <Resource, Integer> temp = new HashMap<>();
        temp.put(ResourceType.COIN, 2);
        temp.put(ResourceType.STONE, 3);
        Requirements requirements = new Requirements(temp);
        assertEquals(requirements, strongbox.getAllResources());
    }

    @Test
    void removeResources() {
        Map<ResourceType, Integer> map =  new HashMap<>();
        map.put(ResourceType.COIN, 2);
        map.put(ResourceType.STONE, 3);
        strongbox.addResources(map);

        Map <Resource, Integer> temp = new HashMap<>();
        temp.put(ResourceType.COIN, 1);
        temp.put(ResourceType.STONE, 4);
        temp.put(ResourceType.SERVANT, 1);
        Requirements requirements = new Requirements(temp);
        Requirements remaining = strongbox.removeResources(requirements);

        Map <Resource, Integer> confront = new HashMap<>();
        confront.put(ResourceType.STONE, 1);
        confront.put(ResourceType.SERVANT, 1);
        assertEquals(new Requirements(confront), remaining);
        temp = new HashMap<>();
        temp.put(ResourceType.COIN, 1);
        requirements = new Requirements(temp);
        assertEquals(requirements, strongbox.getAllResources());
    }
}