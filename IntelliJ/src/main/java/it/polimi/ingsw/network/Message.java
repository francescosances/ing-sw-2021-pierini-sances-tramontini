package it.polimi.ingsw.network;

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

    public Message(String serializedMessage) {
        // TODO constructor from serialized message
        this.type = null;
        this.data = null;
    }

    public MessageType getType() { return type; }

    public String getData(String key) { return this.data.get(key); }
    public Map<String, String> getData() { return new HashMap<>(this.data); }

    public void addData(String key, String value){ this.data.put(key, value); }
    public void addData(Map<String, String> data){ this.data.putAll(data); }

    public String serialize() {
        return ""; // TODO serialize this
    }
}
