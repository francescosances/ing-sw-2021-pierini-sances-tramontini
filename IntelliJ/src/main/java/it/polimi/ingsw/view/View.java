package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Match;

public interface View {

    public void resumeMatch(Match match);

    public void yourTurn();

    public void userConnected(String username);
    public void userDisconnected(String username);

    public void askUsername();

}
