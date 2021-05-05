package it.polimi.ingsw.utils;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.serialization.Serializer;

import java.io.*;
import java.util.List;

public class FileManager {

    private static FileManager instance;

    private static final String ROOT_FOLDER_NAME = "storage";
    private static final String MATCHES_FOLDER_NAME = "matches";

    private FileManager(){}

    public static FileManager getInstance(){
        if(instance == null)
            instance = new FileManager();
        return instance;
    }

    private void checkFolders(){
        new File("./"+ROOT_FOLDER_NAME+"/"+MATCHES_FOLDER_NAME).mkdirs();
    }

    public synchronized void writeMatchesList(List<GameController> matches) throws IOException {
        checkFolders();
        File matchesFile = new File(ROOT_FOLDER_NAME+"/matches.json");
        matchesFile.createNewFile();
        
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
}
