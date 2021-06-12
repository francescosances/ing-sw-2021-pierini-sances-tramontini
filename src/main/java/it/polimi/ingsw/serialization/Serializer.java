package it.polimi.ingsw.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.Depot;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Strongbox;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Triple;

import java.lang.reflect.Type;
import java.util.List;

public class Serializer {

    public static String serializeMatchState(Match match) {
        return new Gson().toJson(match);
    }

    public static Match deserializeMatchState(String serializedMatch) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonbuilder.registerTypeAdapter(Depot.class, new DepotCreator());
        gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());
        gsonbuilder.registerTypeAdapter(Resource.class, new ResourceCreator());
        Match ret = gsonbuilder.create().fromJson(serializedMatch, Match.class);
        for(PlayerBoard board : ret.getPlayers()){
            board.setMatch(ret);
        }
        return  ret;
    }

    public static Match deserializeSoloMatchState(String serializedMatch) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonbuilder.registerTypeAdapter(Depot.class, new DepotCreator());
        gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());
        Match ret = gsonbuilder.create().fromJson(serializedMatch, SoloMatch.class);
        for(PlayerBoard board : ret.getPlayers()){
            board.setMatch(ret);
        }
        return ret;
    }

    public static String serializeDevelopmentCard(DevelopmentCard developmentCard) {
        return new Gson().toJson(developmentCard);
    }

    public static DevelopmentCard deserializeDevelopmentCard(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Requirements.class, new RequirementsCreator()).create();
        return gson.fromJson(json, DevelopmentCard.class);
    }

    public static String serializeDevelopmentCardsDeckList(List<Deck<DevelopmentCard>> developmentCardList) {
        return new Gson().toJson(developmentCardList);
    }

    public static List<Deck<DevelopmentCard>> deserializeDevelopmentCardsDeckList(String serializedDeck) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());

        Gson gson = gsonbuilder.create();
        Type type = new TypeToken<List<Deck<DevelopmentCard>>>(){}.getType();
        return gson.fromJson(serializedDeck, type);
    }

    public static String serializeDevelopmentCardsList(List<DevelopmentCard> developmentCardList) {
        return new Gson().toJson(developmentCardList);
    }

    public static List<DevelopmentCard> deserializeDevelopmentCardsList(String serializedList) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());

        Gson gson = gsonbuilder.create();
        Type type = new TypeToken<List<DevelopmentCard>>(){}.getType();
        return gson.fromJson(serializedList, type);
    }

    public static String serializeLeaderCard(LeaderCard leaderCard){
        return new Gson().toJson(leaderCard);
    }

    public static LeaderCard deserializeLeaderCard(String serializedCard) {
        GsonBuilder gsonbuilder = new GsonBuilder().registerTypeAdapter(Requirements.class, new RequirementsCreator());
        Gson gson = gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator()).create();
        return gson.fromJson(serializedCard, LeaderCard.class);
    }

    public static String serializeLeaderCardList(LeaderCard[] list) {
        return new Gson().toJson(list);
    }

    public static String serializeLeaderCardList(List<LeaderCard> list){
        return new Gson().toJson(list);
    }


    public static List<LeaderCard> deserializeLeaderCardList(String serializedCard) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());

        Gson gson = gsonbuilder.create();
        Type type = new TypeToken<List<LeaderCard>>(){}.getType();
        return gson.fromJson(serializedCard, type);
    }

    public static Deck<LeaderCard> deserializeLeaderCardDeck(String serializedCard) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());

        Gson gson = gsonbuilder.create();
        Type type = new TypeToken<Deck<LeaderCard>>(){}.getType();
        return gson.fromJson(serializedCard, type);
    }

    public static String serializeMarket(Market market) {
        return new Gson().toJson(market);
    }

    public static Market deserializeMarket(String json){
        return new Gson().fromJson(json, Market.class);
    }

    public static String serializeActionToken(ActionToken actionToken){
        return new Gson().toJson(actionToken);
    }

    public static ActionToken deserializeActionToken(String json){
        return new Gson().fromJson(json, ActionToken.class);
    }

    public static String serializeStrongbox(Strongbox strongbox){
        return new Gson().toJson(strongbox);
    }

    public static Strongbox deserializeStrongbox(String json){
        return new Gson().fromJson(json, Strongbox.class);
    }

    public static String serializeWarehouse(Warehouse warehouse) {
        return new Gson().toJson(warehouse);
    }

    public static Warehouse deserializeWarehouse(String json) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Depot.class, new DepotCreator());
        gsonBuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());
        gsonBuilder.registerTypeAdapter(Resource.class, new ResourceCreator());
        return gsonBuilder.create().fromJson(json, Warehouse.class);
    }

    public static String serializePlayerBoard(PlayerBoard playerBoard){
        return new Gson().toJson(playerBoard);
    }

    public static PlayerBoard deserializePlayerBoard(String json){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Depot.class, new DepotCreator());
        gsonBuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());
        gsonBuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonBuilder.registerTypeAdapter(Resource.class,new ResourceCreator());
        return gsonBuilder.create().fromJson(json, PlayerBoard.class);
    }

    public static String serializeResources(Resource[] resources){
        return new Gson().toJson(resources);
    }

    public static Resource[] deserializeResources(String json){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Resource.class, new ResourceCreator());
        return gsonBuilder.create().fromJson(json, Resource[].class);
    }

    public static String serializeResource(Resource resource){
        return new Gson().toJson(resource);
    }

    public static Resource deserializeResource(String json){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Resource.class, new ResourceCreator());
        return gsonBuilder.create().fromJson(json, Resource.class);
    }

    public static String serializeDevelopmentCardSlots(DevelopmentCardSlot[] availableSlots) {
        return new Gson().toJson(availableSlots);
    }

    public static DevelopmentCardSlot[] deserializeDevelopmentCardsSlots(String slots) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());

        Gson gson = gsonBuilder.create();
        return gson.fromJson(slots, DevelopmentCardSlot[].class);
    }

    public static String serializeProducerList(List<Producer> producerList){
        return new Gson().toJson(producerList);
    }

    public static List<Producer> deserializeProducerList(String json){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonBuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());
        gsonBuilder.registerTypeAdapter(Producer.class, new ProducerCreator());

        Gson gson = gsonBuilder.create();
        Type type = new TypeToken<List<Producer>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static String serializeRequirements(Requirements requirements){
        return new Gson().toJson(requirements);
    }

    public static Requirements deserializeRequirements(String json){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonBuilder.registerTypeAdapter(Resource.class, new ResourceCreator());
        return gsonBuilder.create().fromJson(json, Requirements.class);
    }

    public static String serializeInt(int i){
        return String.valueOf(i);
    }

    public static int deserializeInt(String json){
        return Integer.parseInt(json);
    }

    public static String serializeFaithTrack(FaithTrack faithTrack) {
        return new Gson().toJson(faithTrack);
    }

    public static FaithTrack deserializeFaithTrack(String json){
        return new Gson().fromJson(json, FaithTrack.class);
    }

    public static String serializeLobbies(List<Triple<String, Integer, Integer>> lobbies){
        return new Gson().toJson(lobbies);
    }

    public static List<Triple<String, Integer, Integer>> deserializeLobbies(String json){
        Type listType = new TypeToken<List<Triple<String, Integer, Integer>>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    public static String serializeIntList(List<Integer> list) {
        return new Gson().toJson(list);
    }

    public static List<Integer> deserializeIntList(String choices) {
        Type listType = new TypeToken<List<Integer>>() {}.getType();
        return new Gson().fromJson(choices, listType);
    }

    public static String serializePlayerBoardList(List<PlayerBoard> list) {
        return new Gson().toJson(list);
    }

    public static List<PlayerBoard> deserializePlayerBoardList(String choices) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Depot.class, new DepotCreator());
        gsonBuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());
        gsonBuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonBuilder.registerTypeAdapter(Resource.class,new ResourceCreator());
        Type listType = new TypeToken<List<PlayerBoard>>() {}.getType();
        return gsonBuilder.create().fromJson(choices, listType);
    }
}

