package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.utils.Triple;

import java.util.List;

public interface View {

    // show generic message
    void showMessage(String message);

    void showErrorMessage(String message);

    void listLobbies(List<Triple<String, Integer, Integer>> availableMatches);

    void resumeMatch(Match match);

    void yourTurn();

    void init();

    void askLogin();

    void userConnected(String username);

    void userDisconnected(String username);

}
