package it.polimi.ingsw.controller;

public enum PlayerStatus {
    YOUR_TURN,WAITING;

    private static PlayerStatus[] vals = values();

    public PlayerStatus next()
    {
        return vals[(this.ordinal()+1) % vals.length];
    }
}
