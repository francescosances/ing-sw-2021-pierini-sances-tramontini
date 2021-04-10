package it.polimi.ingsw.network.serialization;

import it.polimi.ingsw.model.ActionToken;
import it.polimi.ingsw.model.Market;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Pair;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class SerializerTest {

    @Test
    public void serializeDevelopmentCard() {
        DevelopmentCard developmentCard = new DevelopmentCard(3,new Requirements(new Pair<>(ResourceType.SHIELD,3)), 1, DevelopmentColorType.GREEN,
                new Requirements(new Pair<>(ResourceType.SERVANT,2)),new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.STONE,1));
        String json = Serializer.serializeDevelopmentCard(developmentCard);
        assertEquals(developmentCard, Serializer.deserializeDevelopmentCard(json));
    }

    @Test
    public void serializeLeaderCard(){
        LeaderCard leaderCard = new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.SERVANT,5)),ResourceType.SHIELD);
        String json = Serializer.serializeLeaderCard(leaderCard);
        assertEquals(leaderCard, Serializer.deserializeLeaderCard(json));
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
}