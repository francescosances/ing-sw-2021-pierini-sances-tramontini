package it.polimi.ingsw.serialization;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Serializer {

    public static String serializeMatchState(Match match) {
        throw new IllegalStateException("Not implemented yet");
    }

    public static Match deserializeMatchState(String serializedMatch) {
        throw new IllegalStateException("Not implemented yet");
    }


    public static void serializeMatchState(Match match, File file) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
        //TODO: formattare (manualmente?) il formato del file json
        //serializeDevelopmentCardDecks(match, writer);

        //serializeMarket(match, writer);
        writer.close();
    }

    public static void serializeDevelopmentCard(DevelopmentCard developmentCard, File file) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
        serializeDevelopmentCard(developmentCard, writer);
        writer.close();
    }

    private static void serializeDevelopmentCard(DevelopmentCard developmentCard, Writer writer) {
        Gson gson = new Gson();
        gson.toJson(developmentCard, writer);
    }

    public static void serializeDevelopmentCardDecks(Match match, File file) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
        serializeDevelopmentCardDecks(match, writer);
        writer.close();
    }

    private static void serializeDevelopmentCardDecks(Match match, Writer writer){
        List<Deck<DevelopmentCard>> list = new ArrayList<>();
        for (int level = 1; level <= DevelopmentCard.MAX_LEVEL; level++) {
            for (DevelopmentColorType color : DevelopmentColorType.values()) {
                Deck<DevelopmentCard> deck = match.getDevelopmentCardDeck(color, level);
                list.add(deck);
            }
        }

        new Gson().toJson(list, writer);
    }

    private static void serializeMarket(Match match, Writer writer) {
        Market market = match.getMarket();

    }


    private static String serializeLeaderCard(LeaderCard leaderCard){
        Gson gson = new Gson();
        return gson.toJson(leaderCard);
    }

    public static DevelopmentCard deserializeDevelopmentCard(File file) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(file.getPath()));
        RequirementsCreator requirementsCreator = new RequirementsCreator();
        Gson gson = new GsonBuilder().registerTypeAdapter(Requirements.class, requirementsCreator).create();

        DevelopmentCard card = gson.fromJson(reader, DevelopmentCard.class);
        reader.close();

        return card;
    }

    public static List<Deck<DevelopmentCard>> deserializeDevelopmentCardDecks(File file) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(file.getPath()));
        RequirementsCreator requirementsCreator = new RequirementsCreator();
        Gson gson = new GsonBuilder().registerTypeAdapter(Requirements.class, requirementsCreator).create();

        List<Deck<DevelopmentCard>> deckList = Arrays.asList(gson.fromJson(reader, Deck[].class));
        reader.close();

        return deckList;
    }


    public static LeaderCard deserializeLeaderCard(String serializedCard) {
        GsonBuilder gsonbuilder = new GsonBuilder().registerTypeAdapter(Requirements.class, new RequirementsCreator());
        Gson gson = gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator()).create();
        return gson.fromJson(serializedCard, LeaderCard.class);
    }

    public static List<LeaderCard> deserializeLeaderCards(String serializedCard) {
        GsonBuilder gsonbuilder = new GsonBuilder().registerTypeAdapter(Requirements.class, new RequirementsCreator());
        Gson gson = gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator()).create();
        Type type = new TypeToken<List<LeaderCard>>(){}.getType();
        return gson.fromJson(serializedCard, type);
    }

}

