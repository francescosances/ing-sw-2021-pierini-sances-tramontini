package it.polimi.ingsw.controller;

public enum GameStatus {
    ADDING_PLAYERS,SETUP;

    private static GameStatus[] vals = values();

    public GameStatus next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }
}
