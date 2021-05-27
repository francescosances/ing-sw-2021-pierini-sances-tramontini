package it.polimi.ingsw.serialization;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.*;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class SerializerTest {

    @Test
    public void serializeDevelopmentCard() {
        DevelopmentCard developmentCard = new DevelopmentCard("",3, new Requirements(new Pair<>(ResourceType.SHIELD, 3)), 1, DevelopmentColorType.GREEN,
                new Requirements(new Pair<>(ResourceType.SERVANT, 2)), new Pair<>(ResourceType.COIN, 1), new Pair<>(ResourceType.SHIELD, 1), new Pair<>(ResourceType.STONE, 1));
        String json = Serializer.serializeDevelopmentCard(developmentCard);
        assertEquals(developmentCard, Serializer.deserializeDevelopmentCard(json));
    }

    @Test
    public void serializeDevelopmentCardsDeckList(){
        List<Deck <DevelopmentCard>> deckList = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            deckList.add(new Deck<>());
        deckList.get(0).add(new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 2)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        deckList.get(0).add(new DevelopmentCard("",2, new Requirements(new Pair<>(ResourceType.SERVANT, 2)), 1, DevelopmentColorType.YELLOW, new Requirements(new Pair<>(ResourceType.SHIELD, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        deckList.get(0).add(new DevelopmentCard("",5, new Requirements(new Pair<>(ResourceType.SHIELD, 4)), 2, DevelopmentColorType.BLUE, new Requirements(new Pair<>(ResourceType.STONE, 3)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1), new Pair<>(ResourceType.SERVANT, 1)));
        deckList.get(1).add(new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.STONE, 2)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.SERVANT, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        deckList.get(1).add(new DevelopmentCard("",2, new Requirements(new Pair<>(ResourceType.COIN, 2)), 3, DevelopmentColorType.BLUE, new Requirements(new Pair<>(ResourceType.SHIELD, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        deckList.get(2).add(new DevelopmentCard("",5, new Requirements(new Pair<>(ResourceType.SERVANT, 4)), 2, DevelopmentColorType.PURPLE, new Requirements(new Pair<>(ResourceType.COIN, 3)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1), new Pair<>(ResourceType.SHIELD, 1)));
        String json = Serializer.serializeDevelopmentCardsDeckList(deckList);
        assertEquals(deckList,Serializer.deserializeDevelopmentCardsDeckList(json));
    }

    @Test
    public void serializeDepotLeaderCard() {
        LeaderCard leaderCard = new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.SERVANT, 5)), ResourceType.SHIELD);
        String json = Serializer.serializeLeaderCard(leaderCard);
        assertEquals(leaderCard, Serializer.deserializeLeaderCard(json));
    }

    @Test
    public void serializeDiscountLeaderCard() {
        LeaderCard leaderCard = new DiscountLeaderCard("",2, new Requirements(new Triple<>(DevelopmentColorType.YELLOW, 1, 1), new Triple<>(DevelopmentColorType.GREEN, 1, 1)), ResourceType.SERVANT);
        String json = Serializer.serializeLeaderCard(leaderCard);
        assertEquals(leaderCard, Serializer.deserializeLeaderCard(json));
    }

    @Test
    public void serializeProductionLeaderCard() {
        LeaderCard leaderCard = new ProductionLeaderCard("",4, new Requirements(new Triple<>(DevelopmentColorType.YELLOW, 2, 1)), new Requirements(new Pair<>(ResourceType.SHIELD, 1)));
        String json = Serializer.serializeLeaderCard(leaderCard);
        assertEquals(leaderCard, Serializer.deserializeLeaderCard(json));
    }

    @Test
    public void serializeWhiteMarbleLeaderCard() {
        LeaderCard leaderCard = new WhiteMarbleLeaderCard("",5, new Requirements(new Triple<>(DevelopmentColorType.GREEN, 1, 2), new Triple<>(DevelopmentColorType.PURPLE, 1, 1)), ResourceType.SHIELD);
        String json = Serializer.serializeLeaderCard(leaderCard);
        assertEquals(leaderCard, Serializer.deserializeLeaderCard(json));
    }

    @Test
    public void serializeLeaderCardList() {
        List<LeaderCard> ret = new ArrayList<>();
        ret.add(new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.COIN, 5)), ResourceType.STONE));
        ret.add(new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.STONE, 5)), ResourceType.SERVANT));
        ret.add(new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.SERVANT, 5)), ResourceType.SHIELD));
        ret.add(new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.SHIELD, 5)), ResourceType.COIN));
        String json = Serializer.serializeLeaderCardList(ret.toArray(new LeaderCard[0]));
        assertEquals(ret, Serializer.deserializeLeaderCardList(json));
        json = Serializer.serializeLeaderCardList(ret);
        assertEquals(ret, Serializer.deserializeLeaderCardList(json));
    }

    @Test
    public void serializeDevelopmentCardsList() {
        List<DevelopmentCard> cardsList = new ArrayList<>();
        cardsList.add(new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 2)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        cardsList.add(new DevelopmentCard("",2, new Requirements(new Pair<>(ResourceType.SERVANT, 2)), 1, DevelopmentColorType.YELLOW, new Requirements(new Pair<>(ResourceType.SHIELD, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        cardsList.add(new DevelopmentCard("",5, new Requirements(new Pair<>(ResourceType.SHIELD, 4)), 2, DevelopmentColorType.BLUE, new Requirements(new Pair<>(ResourceType.STONE, 3)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1), new Pair<>(ResourceType.SERVANT, 1)));
        String json = Serializer.serializeDevelopmentCardsList(cardsList);
        assertEquals(cardsList, Serializer.deserializeDevelopmentCardsList(json));
    }

    @Test
    public void serializeMarket() {
        Market market = new Match("test").getMarket();
        String json = Serializer.serializeMarket(market);
        assertEquals(market, Serializer.deserializeMarket(json));
    }

    @Test
    public void serializeActionToken() {
        ActionToken actionToken = new ActionToken(DevelopmentColorType.YELLOW);
        String json = Serializer.serializeActionToken(actionToken);
        assertEquals(actionToken, Serializer.deserializeActionToken(json));
        actionToken = new ActionToken(2);
        json = Serializer.serializeActionToken(actionToken);
        assertEquals(actionToken, Serializer.deserializeActionToken(json));
    }

    @Test
    public void serializeStrongbox() {
        Strongbox strongbox = new Strongbox();
        Map<ResourceType, Integer> map = new HashMap<>();
        map.put(ResourceType.STONE, 2);
        map.put(ResourceType.COIN, 1);
        strongbox.addResources(map);
        String json = Serializer.serializeStrongbox(strongbox);
        assertEquals(strongbox, Serializer.deserializeStrongbox(json));
    }

    @Test
    public void serializeWarehouse() throws IncompatibleDepotException {
        Warehouse warehouse = new Warehouse();
        warehouse.addResources(0, ResourceType.COIN, 1);
        warehouse.addResources(2, ResourceType.STONE, 2);
        LeaderCard leaderDepot = new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.COIN, 5)), ResourceType.STONE);
        warehouse.addDepotLeaderCard((DepotLeaderCard) leaderDepot);
        String json = Serializer.serializeWarehouse(warehouse);
        assertEquals(warehouse, Serializer.deserializeWarehouse(json));
    }

    @Test
    public void serializePlayerBoard() throws IncompatibleDepotException {
        Match match = new Match("Match Test");
        PlayerBoard playerBoard = new PlayerBoard("PlayerBoard Test", match);
        playerBoard.gainFaithPoints(15);
        PopeFavorTile[] popeFavorTiles = playerBoard.getFaithTrack().getPopeFavorTiles();
        popeFavorTiles[0].uncover();
        playerBoard.getWarehouse().addResources(0, ResourceType.COIN, 1);
        LeaderCard leaderCard = new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.STONE, 5)), ResourceType.SERVANT, true);
        playerBoard.addLeaderCard(leaderCard);
        playerBoard.getWarehouse().addDepotLeaderCard((DepotLeaderCard) leaderCard);
        playerBoard.getWarehouse().addResources(3, ResourceType.SERVANT, 2);
        Map<ResourceType, Integer> resourcesMap = new HashMap<>();
        resourcesMap.put(ResourceType.SHIELD, 3);
        resourcesMap.put(ResourceType.STONE, 7);
        playerBoard.getStrongbox().addResources(resourcesMap);
        DevelopmentCard developmentCard = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 2)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1));
        DevelopmentCardSlot[] developmentCardSlots = playerBoard.getDevelopmentCardSlots();
        developmentCardSlots[1].addCard(developmentCard);

        String json = Serializer.serializePlayerBoard(playerBoard);

        PlayerBoard deserialized = Serializer.deserializePlayerBoard(json);

        assertEquals(playerBoard.getUsername(), deserialized.getUsername());
        assertEquals(playerBoard.getFaithTrack(), deserialized.getFaithTrack());
        assertArrayEquals(playerBoard.getDevelopmentCardSlots(), deserialized.getDevelopmentCardSlots());
        assertEquals(playerBoard.getWarehouse(), deserialized.getWarehouse());
        assertEquals(playerBoard.getStrongbox(), deserialized.getStrongbox());
        assertEquals(playerBoard.getLeaderCards(), deserialized.getLeaderCards());

        assertEquals(playerBoard, deserialized);

    }

    @Test
    public void serializeMatchState() throws IncompatibleDepotException {
        Match match = new Match("Match Test");
        PlayerBoard playerBoard = new PlayerBoard("PlayerBoard Test", match);
        match.addPlayer(playerBoard.getUsername());
        playerBoard.gainFaithPoints(15);
        PopeFavorTile[] popeFavorTiles = playerBoard.getFaithTrack().getPopeFavorTiles();
        popeFavorTiles[0].uncover();
        playerBoard.getWarehouse().addResources(0, ResourceType.COIN, 1);
        LeaderCard leaderCard = new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.STONE, 5)), ResourceType.SERVANT, true);
        playerBoard.addLeaderCard(leaderCard);
        playerBoard.getWarehouse().addDepotLeaderCard((DepotLeaderCard) leaderCard);
        playerBoard.getWarehouse().addResources(3, ResourceType.SERVANT, 2);
        Map<ResourceType, Integer> resourcesMap = new HashMap<>();
        resourcesMap.put(ResourceType.SHIELD, 3);
        resourcesMap.put(ResourceType.STONE, 7);
        playerBoard.getStrongbox().addResources(resourcesMap);
        DevelopmentCard developmentCard = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 2)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1));
        DevelopmentCardSlot[] developmentCardSlots = playerBoard.getDevelopmentCardSlots();
        developmentCardSlots[1].addCard(developmentCard);
        String json = Serializer.serializeMatchState(match);
        assertEquals(match, Serializer.deserializeMatchState(json));
    }

    @Test
    public void serializeProducerList(){
        List<Producer> producers = new ArrayList<>();
        producers.add(new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 2)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        producers.add(new DevelopmentCard("",2, new Requirements(new Pair<>(ResourceType.SERVANT, 2)), 1, DevelopmentColorType.YELLOW, new Requirements(new Pair<>(ResourceType.SHIELD, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        producers.add(new DevelopmentCard("",5, new Requirements(new Pair<>(ResourceType.SHIELD, 4)), 2, DevelopmentColorType.BLUE, new Requirements(new Pair<>(ResourceType.STONE, 3)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1), new Pair<>(ResourceType.SERVANT, 1)));
        producers.add(new ProductionLeaderCard("",4, new Requirements(new Triple<>(DevelopmentColorType.YELLOW, 2, 1)), new Requirements(new Pair<>(ResourceType.SHIELD, 1))));
        String json = Serializer.serializeProducerList(producers);
        assertEquals(producers, Serializer.deserializeProducerList(json));
    }

    @Test
    public void serializeSoloMatchState() throws IncompatibleDepotException {
        SoloMatch match = new SoloMatch("Match Test");
        PlayerBoard playerBoard = new PlayerBoard("PlayerBoard Test", match);
        match.addPlayer(playerBoard.getUsername());
        playerBoard.gainFaithPoints(15);
        PopeFavorTile[] popeFavorTiles = playerBoard.getFaithTrack().getPopeFavorTiles();
        popeFavorTiles[0].uncover();
        playerBoard.getWarehouse().addResources(0, ResourceType.COIN, 1);
        LeaderCard leaderCard = new DepotLeaderCard("",3, new Requirements(new Pair<>(ResourceType.STONE, 5)), ResourceType.SERVANT, true);
        playerBoard.addLeaderCard(leaderCard);
        playerBoard.getWarehouse().addDepotLeaderCard((DepotLeaderCard) leaderCard);
        playerBoard.getWarehouse().addResources(3, ResourceType.SERVANT, 2);
        Map<ResourceType, Integer> resourcesMap = new HashMap<>();
        resourcesMap.put(ResourceType.SHIELD, 3);
        resourcesMap.put(ResourceType.STONE, 7);
        playerBoard.getStrongbox().addResources(resourcesMap);
        DevelopmentCard developmentCard = new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 2)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1));
        DevelopmentCardSlot[] developmentCardSlots = playerBoard.getDevelopmentCardSlots();
        developmentCardSlots[1].addCard(developmentCard);
        match.moveBlackCross(10);
        String json = Serializer.serializeMatchState(match);
        assertEquals(match, Serializer.deserializeSoloMatchState(json));

    }

    @Test
    public void serializeResources() {
        Resource resourceOne = NonPhysicalResourceType.VOID;
        String jsonOne = Serializer.serializeResource(resourceOne);
        assertEquals(resourceOne, Serializer.deserializeResource(jsonOne));

        Resource resourceTwo = ResourceType.SERVANT;
        String jsonTwo = Serializer.serializeResource(resourceTwo);
        assertEquals(resourceTwo, Serializer.deserializeResource(jsonTwo));

        Resource [] resources = new Resource[2];
        resources [0] = resourceOne;
        resources [1] = resourceTwo;
        String json = Serializer.serializeResources(resources);
        assertArrayEquals(resources, Serializer.deserializeResources(json));

    }

    @Test
    public void serializeRequirements(){
        Requirements resourceRequirements = new Requirements(new Pair<>(ResourceType.STONE, 5));
        Requirements cardRequirements = new Requirements(new Triple<>(DevelopmentColorType.GREEN, 1, 2), new Triple<>(DevelopmentColorType.PURPLE, 1, 1));
        String json = Serializer.serializeRequirements(resourceRequirements);
        String json2 = Serializer.serializeRequirements(cardRequirements);
        assertEquals(resourceRequirements, Serializer.deserializeRequirements(json));
        assertEquals(cardRequirements, Serializer.deserializeRequirements(json2));
    }

    @Test
    public void serializeFaithTrack(){
        Match match = new Match("Test");
        FaithTrack faithTrack = new FaithTrack(match, "test");
        for (int i = 0; i<3; i++)
            faithTrack.moveMarker();
        String json = Serializer.serializeFaithTrack(faithTrack);
        assertEquals(faithTrack, Serializer.deserializeFaithTrack(json));
    }

    @Test
    public void serializeDevelopmentCardSlots(){
        DevelopmentCardSlot[] developmentCardSlots = new DevelopmentCardSlot[3];
        for (int i = 0; i < developmentCardSlots.length; i++)
            developmentCardSlots[i] = new DevelopmentCardSlot();
        developmentCardSlots[1].addCard(new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.SHIELD, 2)), 1, DevelopmentColorType.GREEN, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(NonPhysicalResourceType.FAITH_POINT, 1)));
        developmentCardSlots[1].addCard(new DevelopmentCard("",5, new Requirements(new Pair<>(ResourceType.COIN, 2)), 2, DevelopmentColorType.BLUE, new Requirements(new Pair<>(ResourceType.SHIELD, 2)), new Pair<>(ResourceType.STONE, 1)));
        developmentCardSlots[2].addCard(new DevelopmentCard("",1, new Requirements(new Pair<>(ResourceType.STONE, 1)), 1, DevelopmentColorType.YELLOW, new Requirements(new Pair<>(ResourceType.COIN, 1)), new Pair<>(ResourceType.SERVANT, 1)));
        String json = Serializer.serializeDevelopmentCardSlots(developmentCardSlots);
        assertArrayEquals(developmentCardSlots, Serializer.deserializeDevelopmentCardsSlots(json));
    }

    @Test
    public void serializeInt(){
        String json = Serializer.serializeInt(10);
        assertEquals(10, Serializer.deserializeInt(json));
    }

    @Test
    public void serializeLobbies(){
        List<Triple<String, Integer, Integer>> lobbies = new ArrayList<>();
        lobbies.add(new Triple<>("Test", 1,2));
        String json = Serializer.serializeLobbies(lobbies);
        List<Triple<String, Integer, Integer>> lobbies1 = Serializer.deserializeLobbies(json);
        assertEquals(lobbies.get(0).getFirst(), lobbies1.get(0).getFirst());
        assertEquals(lobbies.get(0).getSecond(), lobbies1.get(0).getSecond());
        assertEquals(lobbies.get(0).getThird(), lobbies1.get(0).getThird());

    }
}