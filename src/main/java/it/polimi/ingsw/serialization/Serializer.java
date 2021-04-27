package it.polimi.ingsw.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.Depot;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Strongbox;
import it.polimi.ingsw.model.storage.Warehouse;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        return gsonbuilder.create().fromJson(serializedMatch, Match.class);
    }

    public static Match deserializeSoloMatchState(String serializedMatch) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonbuilder.registerTypeAdapter(Depot.class, new DepotCreator());
        gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());
        return gsonbuilder.create().fromJson(serializedMatch, SoloMatch.class);
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

    public static String serializeDevelopmentCardsList(List<DevelopmentCard> developmentCardList) {
        return new Gson().toJson(developmentCardList);
    }

    public static List<Deck<DevelopmentCard>> deserializeDevelopmentCardsDeckList(String serializedDeck) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());

        Gson gson = gsonbuilder.create();
        Type type = new TypeToken<List<Deck<DevelopmentCard>>>(){}.getType();
        return gson.fromJson(serializedDeck, type);
    }

    public static List<DevelopmentCard> deserializeDevelopmentCardsList(String serializedList) {
        System.out.println("QUI");
        System.out.println(serializedList);
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

    public static String serializeLeaderCardDeck(LeaderCard[] list) {
        return new Gson().toJson(list);
    }

    public static List<LeaderCard> deserializeLeaderCardDeck(String serializedCard) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());
        gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator());

        Gson gson = gsonbuilder.create();
        Type type = new TypeToken<List<LeaderCard>>(){}.getType();
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

    public static String serializeDevelopmentCardSlots(List<DevelopmentCardSlot> availableSlots) {
        return new Gson().toJson(availableSlots);
    }

    public static List<DevelopmentCardSlot> deserializaDevelopmentCardsSlots(String slots) {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.registerTypeAdapter(Requirements.class, new RequirementsCreator());

        Gson gson = gsonbuilder.create();
        Type type = new TypeToken<List<DevelopmentCardSlot>>(){}.getType();
        return gson.fromJson(slots, type);
    }
}

