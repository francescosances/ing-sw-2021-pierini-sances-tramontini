package it.polimi.ingsw.utils;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class Message{
    /**
     * Type of the message.
     */
    private final MessageType type;

    /**
     * Content of the message. Each key is mapped to the corresponding data.
     */
    private final Map<String, String> data;

    /**
     * Creates a new message with the specified type and content.
     * @param type the type of the message
     * @param data the content of the message
     */
    public Message(MessageType type, Map<String, String> data) {
        this.type = type;
        this.data = new HashMap<>(data);
    }

    /**
     * Creates a new empty message with the specified type.
     * @param type the type of the message
     */
    public Message(MessageType type) {
        this.type = type;
        this.data = new HashMap<>();
    }

    /**
     * Returns the type of the message.
     * @return the type of the message
     */
    public MessageType getType() { return type; }

    /**
     * Returns the data in the message matching the specified key.
     * @param key the key of the data to return
     * @return the data in the message matching the specified key
     */
    public String getData(String key) { return this.data.get(key); }

    /**
     * Returns the content of the message.
     * @return the content of the message
     */
    public Map<String, String> getData() { return new HashMap<>(this.data); }

    /**
     * Adds to the message the specified data with the specified key.
     * @param key the key of the data to be added
     * @param value the data to be added
     */
    public void addData(String key, String value){ this.data.put(key, value); }

    /**
     * Adds to the message the specified content, in which each key is mapped to the corresponding data.
     * @param data the content to be added
     */
    public void addData(Map<String, String> data){ this.data.putAll(data); }

    /**
     * Returns the serialized message in the form of a JSON string.
     * @return the serialized message in the form of a JSON string
     */
    public String serialize() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    /**
     * Returns the message deserialized from the specified JSON string.
     * @param serializedMessage the serialized message in the form of a JSON string
     * @return the message deserialized from the specified JSON string
     */
    public static Message messageFromString(String serializedMessage){
        Gson gson = new Gson();
        return gson.fromJson(serializedMessage,Message.class);
    }

    /**
     * All possible types of message.
     */
    public enum MessageType {
        // from server to client
        GENERIC,
        ERROR,
        LOGIN_FAILED,
        LOBBY_INFO,
        RESUME_MATCH,
        CURRENT_ACTIVE_USER,
        LIST_LEADER_CARDS,
        SHOW_PLAYER_BOARD,
        ASK_FOR_ACTION,
        SWAP_DEPOTS,
        TAKE_RESOURCES_FROM_MARKET,
        SHOW_MARKET,
        SELECT_MARKET_ROW,
        SELECT_MARKET_COLUMN,
        SHOW_RESOURCES,
        RESOURCE_TO_STORE,
        WHITE_MARBLE_CONVERSION,
        SHOW_WAREHOUSE_STATUS,
        DEVELOPMENT_CARDS_TO_BUY,
        CHOOSE_DEVELOPMENT_CARD_SLOT,
        PRODUCTION,
        START_RESOURCES,
        SHOW_PLAYER_LEADER_CARDS,
        // from client to server
        LOGIN_REQUEST,
        LOBBY_CHOICE,
        LEADER_CARDS_CHOICE,
        DISCARD_LEADER_CARD,
        ACTIVATE_LEADER_CARD,
        PERFORM_ACTION,
        ROLLBACK,
        SHOW_PLAYERS;

    }

}



