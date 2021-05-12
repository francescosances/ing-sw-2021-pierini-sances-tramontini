package it.polimi.ingsw.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.serialization.Serializer;

import java.io.*;
import java.util.List;
import java.util.Map;

public class FileManager {

    private static FileManager instance;

    protected static final String ROOT_FOLDER_NAME = "storage";
    protected static final String MATCHES_FOLDER_NAME = "matches";

    private FileManager(){}

    public static FileManager getInstance(){
        if(instance == null)
            instance = new FileManager();
        return instance;
    }

    private void checkFolders(){
        new File("./"+ROOT_FOLDER_NAME+"/"+MATCHES_FOLDER_NAME).mkdirs();
    }

    public synchronized Map<String,List<String>> readMatchesList() throws IOException {
        File matchFile = new File(ROOT_FOLDER_NAME+"/matches.json");
        BufferedReader reader = new BufferedReader(new FileReader(matchFile));
        return new Gson().fromJson(reader.readLine(),new TypeToken<Map<String,List<String>>>(){}.getType());
    }

    public synchronized void writeMatchesList(Map<String,List<String>> matches) throws IOException {
        checkFolders();
        File matchesFile = new File(ROOT_FOLDER_NAME+"/matches.json");
        matchesFile.createNewFile();
        FileWriter writer = new FileWriter(matchesFile);
        writer.write(new Gson().toJson(matches));
        writer.close();
    }

    public synchronized void writeMatchStatus(Match match) throws IOException {
        checkFolders();
        File matchFile = new File(ROOT_FOLDER_NAME+"/"+MATCHES_FOLDER_NAME+"/"+match.getMatchName()+".json");//TODO:pulire il nome del file
        matchFile.createNewFile();
        FileWriter writer = new FileWriter(matchFile);
        writer.write(Serializer.serializeMatchState(match));
        writer.close();
    }

    public synchronized Match readMatchStatus(String matchName) throws IOException {
        File matchFile = new File(ROOT_FOLDER_NAME+"/"+MATCHES_FOLDER_NAME+"/"+matchName+".json");
        BufferedReader reader = new BufferedReader(new FileReader(matchFile));
        String matchJSON = reader.readLine();
        Match ret = Serializer.deserializeMatchState(matchJSON);//TODO: controllare la creazione di match all'interno del metodo, clonare all'esterno
        if(ret.getMaxPlayersNumber() == 1)
            ret= Serializer.deserializeSoloMatchState(matchJSON);
        return ret;
    }

    public List<Deck<DevelopmentCard>> readDevelopmentCardsDecks() throws IOException {
        File cardsFile = new File(ROOT_FOLDER_NAME+"/development_cards.json");
        BufferedReader reader = new BufferedReader(new FileReader(cardsFile));
        return Serializer.deserializeDevelopmentCardsDeckList(reader.readLine());
    }

    public Deck<LeaderCard> readLeaderCards() throws IOException {
        File cardsFile = new File(ROOT_FOLDER_NAME+"/leader_cards.json");
        BufferedReader reader = new BufferedReader(new FileReader(cardsFile));
        return new Deck<>(Serializer.deserializeLeaderCardList(reader.readLine()));
    }
}
