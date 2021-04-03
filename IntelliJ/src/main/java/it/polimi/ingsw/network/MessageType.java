package it.polimi.ingsw.network;


public enum MessageType {
    // from server to client
    GENERIC,
    LOGIN_FAILED,
    LOBBY_INFO,
    MATCH_FULL_STATUS,

    // from client to server
    LOGIN_REQUEST,
    LOBBY_CHOICE;
}