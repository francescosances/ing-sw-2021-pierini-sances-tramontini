package it.polimi.ingsw.network.serialization;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.*;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SerializerTest {

    @Test
    public void serializeDevelopmentCard() {
        DevelopmentCard developmentCard = new DevelopmentCard(3,new Requirements(new Pair<>(ResourceType.SHIELD,3)), 1, DevelopmentColorType.GREEN,
                new Requirements(new Pair<>(ResourceType.SERVANT,2)),new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.STONE,1));
        String json = Serializer.serializeDevelopmentCard(developmentCard);
        assertEquals(developmentCard, Serializer.deserializeDevelopmentCard(json));
    }

    @Test
    public void serializeDepotLeaderCard(){
        LeaderCard leaderCard = new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.SERVANT,5)),ResourceType.SHIELD);
        String json = Serializer.serializeLeaderCard(leaderCard);
        assertEquals(leaderCard, Serializer.deserializeLeaderCard(json));
    }

    @Test
    public void serializeDiscountLeaderCard(){
        LeaderCard leaderCard = new DiscountLeaderCard(2,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,1,1),new Triple<>(DevelopmentColorType.GREEN,1,1)),ResourceType.SERVANT);
        String json = Serializer.serializeLeaderCard(leaderCard);
        assertEquals(leaderCard, Serializer.deserializeLeaderCard(json));
    }

    @Test
    public void serializeProductionLeaderCard(){
        LeaderCard leaderCard = new ProductionLeaderCard(4,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,2,1)),new Requirements(new Pair<>(ResourceType.SHIELD,1)));
        String json = Serializer.serializeLeaderCard(leaderCard);
        assertEquals(leaderCard, Serializer.deserializeLeaderCard(json));
    }

    @Test
    public void serializeWhiteMarbleLeaderCard(){
        LeaderCard leaderCard = new WhiteMarbleLeaderCard(5,new Requirements(new Triple<>(DevelopmentColorType.GREEN,1,2),new Triple<>(DevelopmentColorType.PURPLE,1,1)),ResourceType.SHIELD);
        leaderCard.activate();
        String json = Serializer.serializeLeaderCard(leaderCard);
        System.out.println(json);
        assertEquals(leaderCard, Serializer.deserializeLeaderCard(json));
    }

    @Test
    public void serializeLeaderCardDeck(){
        List <LeaderCard> ret = new ArrayList<>();
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.COIN,5)),ResourceType.STONE));
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.STONE,5)),ResourceType.SERVANT));
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.SERVANT,5)),ResourceType.SHIELD));
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.SHIELD,5)),ResourceType.COIN));
        String json = Serializer.serializeLeaderCardDeck(ret.toArray(new LeaderCard[0]));
        assertEquals(ret, Serializer.deserializeLeaderCardDeck(json));
    }

    @Test
    public void serializeMarket(){
        Market market = new Match("test").getMarket();
        String json = Serializer.serializeMarket(market);
        assertEquals(market, Serializer.deserializeMarket(json));
    }

    @Test
    public void serializeActionToken(){
        ActionToken actionToken = new ActionToken(DevelopmentColorType.YELLOW);
        String json = Serializer.serializeActionToken(actionToken);
        assertEquals(actionToken, Serializer.deserializeActionToken(json));
        actionToken = new ActionToken(2);
        json = Serializer.serializeActionToken(actionToken);
        assertEquals(actionToken, Serializer.deserializeActionToken(json));
    }

    @Test
    public void serializeStrongbox(){
        Strongbox strongbox = new Strongbox();
        Map<ResourceType, Integer> map = new HashMap<>();
        map.put(ResourceType.STONE, 2);
        map.put(ResourceType.COIN, 1);
        strongbox.addResources(map);
        String json = Serializer.serializeStrongbox(strongbox);
        assertEquals(strongbox, Serializer.deserializeStrongbox(json));
    }

    @Test
    public void serializeWarehouse(){
        Warehouse warehouse= new Warehouse();
        warehouse.addResource(0, ResourceType.COIN, 1);
        warehouse.addResource(2, ResourceType.STONE, 2);
        LeaderCard leaderDepot = new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.COIN,5)),ResourceType.STONE);
        warehouse.addDepotLeaderCard(leaderDepot);
        String json = Serializer.serializeWarehouse(warehouse);
        assertEquals(warehouse, Serializer.deserializeWarehouse(json));
    }
}