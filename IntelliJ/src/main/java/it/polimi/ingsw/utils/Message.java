package it.polimi.ingsw.utils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Message{
    private final MessageType type;
    private final Map<String, String> data;

    public Message(MessageType type, Map<String, String> data) {
        this.type = type;
        this.data = new HashMap<>(data);
    }

    public Message(MessageType type) {
        this.type = type;
        this.data = new HashMap<>();
    }

    public MessageType getType() { return type; }

    public String getData(String key) { return this.data.get(key); }
    public Map<String, String> getData() { return new HashMap<>(this.data); }

    public void addData(String key, String value){ this.data.put(key, value); }
    public void addData(Map<String, String> data){ this.data.putAll(data); }

    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Message messageFromString(String serializedMessage){
        Gson gson = new Gson();
        return gson.fromJson(serializedMessage,Message.class);
    }

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

}



