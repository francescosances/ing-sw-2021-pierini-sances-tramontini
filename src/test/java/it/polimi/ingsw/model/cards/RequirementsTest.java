package it.polimi.ingsw.model.cards;
import it.polimi.ingsw.model.PlayerBoard;

import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class RequirementsTest {
    private PlayerBoard player;
    private Requirements req1, req2, req3;

    @BeforeEach
    void setUp() {
        player = new PlayerBoard("Test", null);
        req1 = new Requirements();
        req2 = new Requirements();
        req3 = new Requirements();
    }

    @AfterEach
    void tearDown() {
        player = null;
        req1 = null;
        req2 = null;
    }

    @Test
    void satisfied_Resources() throws IncompatibleDepotException {
        player.getWarehouse().addResource(0,ResourceType.STONE,1);
        player.getWarehouse().addResource(1,ResourceType.COIN,1);

        player.getStrongbox().addResources(new HashMap<ResourceType, Integer>(){{
            put(ResourceType.STONE, 4);
            put(ResourceType.SERVANT, 9);
        }});

        req1.addResourceRequirement(ResourceType.COIN, 1);
        req1.addResourceRequirement(ResourceType.STONE, 5);
        req1.addResourceRequirement(ResourceType.SERVANT, 3);

        assertTrue(req1.satisfied(player));

        req2.addResourceRequirement(ResourceType.SHIELD, 1);

        assertFalse(req2.satisfied(player));
    }

    @Test
    void satisfied_DevelopmentCards() {
        player.getDevelopmentCardSlots()[2].addCard(new DevelopmentCard(0, new Requirements(), 1, DevelopmentColorType.BLUE, null));
        player.getDevelopmentCardSlots()[2].addCard(new DevelopmentCard(0, new Requirements(), 2, DevelopmentColorType.GREEN, null));
        player.getDevelopmentCardSlots()[2].addCard(new DevelopmentCard(0, new Requirements(), 3, DevelopmentColorType.YELLOW, null));

        req1.addDevelopmentCardRequirement(DevelopmentColorType.GREEN, 0, 1);
        req1.addDevelopmentCardRequirement(DevelopmentColorType.GREEN, 2, 1);
        req1.addDevelopmentCardRequirement(DevelopmentColorType.YELLOW, 3, 1);

        assertTrue(req1.satisfied(player));

        req2.addDevelopmentCardRequirement(DevelopmentColorType.BLUE, 2, 1);

        assertFalse(req2.satisfied(player));

        req3.addDevelopmentCardRequirement(DevelopmentColorType.BLUE, 1, 1);
        req3.addDevelopmentCardRequirement(DevelopmentColorType.PURPLE, 1, 1);

        assertFalse(req3.satisfied(player));
    }

    @Test
    void satisfied_ResourcesAndDevelopmentCards() throws IncompatibleDepotException {
        player.getWarehouse().addResource(0,ResourceType.COIN,1);
        player.getWarehouse().addResource(1,ResourceType.SERVANT,2);
        player.getWarehouse().addResource(2,ResourceType.STONE,3);

        player.getStrongbox().addResources(new HashMap<ResourceType, Integer>(){{
            put(ResourceType.COIN, 2);
            put(ResourceType.SHIELD, 3);
            put(ResourceType.SERVANT, 5);
        }});

        player.getDevelopmentCardSlots()[0].addCard(new DevelopmentCard(0, new Requirements(), 1, DevelopmentColorType.GREEN, null));
        player.getDevelopmentCardSlots()[0].addCard(new DevelopmentCard(0, new Requirements(), 2, DevelopmentColorType.BLUE, null));
        player.getDevelopmentCardSlots()[1].addCard(new DevelopmentCard(0, new Requirements(), 1, DevelopmentColorType.BLUE, null));
        player.getDevelopmentCardSlots()[2].addCard(new DevelopmentCard(0, new Requirements(), 1, DevelopmentColorType.BLUE, null));
        player.getDevelopmentCardSlots()[2].addCard(new DevelopmentCard(0, new Requirements(), 2, DevelopmentColorType.GREEN, null));
        player.getDevelopmentCardSlots()[2].addCard(new DevelopmentCard(0, new Requirements(), 3, DevelopmentColorType.YELLOW, null));

        req1.addResourceRequirement(ResourceType.SHIELD, 3);
        req1.addResourceRequirement(ResourceType.COIN, 3);
        req1.addResourceRequirement(ResourceType.SERVANT, 3);
        req1.addResourceRequirement(ResourceType.STONE, 3);

        req1.addDevelopmentCardRequirement(DevelopmentColorType.BLUE, 0, 3);
        req1.addDevelopmentCardRequirement(DevelopmentColorType.GREEN, 2, 1);
        req1.addDevelopmentCardRequirement(DevelopmentColorType.YELLOW, 3, 1);

        assertTrue(req1.satisfied(player));

        req2.addResourceRequirement(ResourceType.COIN, 4);

        assertFalse(req2.satisfied(player));

        req3.addDevelopmentCardRequirement(DevelopmentColorType.PURPLE, 1, 1);

        assertFalse(req3.satisfied(player));
    }

}