package it.polimi.ingsw.network;

import com.google.gson.*;
import it.polimi.ingsw.model.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Serializer {

    public void serializeMatchState(Match match, File file) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
        //TODO: formattare (manualmente?) il formato del file json
        //serializeDevelopmentCardDecks(match, writer);

        //serializeMarket(match, writer);
        writer.close();
    }

    public void serializeDevelopmentCard(DevelopmentCard developmentCard, File file) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
        serializeDevelopmentCard(developmentCard, writer);
        writer.close();
    }

    private void serializeDevelopmentCard(DevelopmentCard developmentCard, Writer writer) {
        Gson gson = new Gson();
        gson.toJson(developmentCard, writer);
    }

    public void serializeDevelopmentCardDecks(Match match, File file) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
        serializeDevelopmentCardDecks(match, writer);
        writer.close();
    }

    private void serializeDevelopmentCardDecks(Match match, Writer writer){
        List<Deck<DevelopmentCard>> list = new ArrayList<>();
        for (int level = 1; level <= DevelopmentCard.MAX_LEVEL; level++) {
            for (DevelopmentColorType color : DevelopmentColorType.values()) {
                Deck<DevelopmentCard> deck = match.getDevelopmentCardDeck(color, level);
                list.add(deck);
            }
        }

        new Gson().toJson(list, writer);
    }

    private void serializeMarket(Match match, Writer writer) {
        Market market = match.getMarket();

    }

    public void serializeLeaderCard(LeaderCard leaderCard, File file) throws IOException {
        Writer writer = Files.newBufferedWriter(Paths.get(file.getPath()));
        serializeLeaderCard(leaderCard, writer);
        writer.close();
    }

    private void serializeLeaderCard(LeaderCard leaderCard, Writer writer){
        Gson gson = new Gson();
        gson.toJson(leaderCard, writer);
    }


    public DevelopmentCard deserializeDevelopmentCard(File file) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(file.getPath()));
        RequirementsCreator requirementsCreator = new RequirementsCreator();
        Gson gson = new GsonBuilder().registerTypeAdapter(Requirements.class, requirementsCreator).create();

        DevelopmentCard card = gson.fromJson(reader, DevelopmentCard.class);
        reader.close();

        return card;
    }

    public List<Deck<DevelopmentCard>> deserializeDevelopmentCardDecks(File file) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(file.getPath()));
        RequirementsCreator requirementsCreator = new RequirementsCreator();
        Gson gson = new GsonBuilder().registerTypeAdapter(Requirements.class, requirementsCreator).create();

        List<Deck<DevelopmentCard>> deckList = Arrays.asList(gson.fromJson(reader, Deck[].class));
        reader.close();

        return deckList;
    }


    public LeaderCard deserializeLeaderCard(File file) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(file.getPath()));
        GsonBuilder gsonbuilder = new GsonBuilder().registerTypeAdapter(Requirements.class, new RequirementsCreator());
        Gson gson = gsonbuilder.registerTypeAdapter(LeaderCard.class, new LeaderCardCreator()).create();

        LeaderCard leaderCard = gson.fromJson(reader, LeaderCard.class);
        reader.close();

        return leaderCard;
    }


}

