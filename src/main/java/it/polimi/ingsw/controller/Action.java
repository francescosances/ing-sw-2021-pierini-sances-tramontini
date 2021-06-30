package it.polimi.ingsw.controller;

public enum
Action{
    /**
     * Enlists the possible actions a player can do when is their turn.
     */
    SKIP("Go ahead / Skip"),
    MOVE_RESOURCES("Move resources"),
    PLAY_LEADER ("Activate or discard a leader card"),
    SHOW_PLAYER_BOARD("Show a player board"),

    CANCEL("Cancel"),

    TAKE_RESOURCES_FROM_MARKET("Take resources from market"),
    BUY_DEVELOPMENT_CARD ("Buy development card"),
    ACTIVATE_PRODUCTION ("Activate production");

    /**
     * The label to print when asking for actions
     */
    private final String label;

    /**
     * Array that contains all extra actions
     */
    public static final Action[] ALL_ACTIONS = {TAKE_RESOURCES_FROM_MARKET,BUY_DEVELOPMENT_CARD,ACTIVATE_PRODUCTION,MOVE_RESOURCES,PLAY_LEADER, SHOW_PLAYER_BOARD};

    /**
     * Array that contains all normal actions
     */
    public static final Action[] NORMAL_ACTIONS = {SKIP,MOVE_RESOURCES,PLAY_LEADER, SHOW_PLAYER_BOARD};

    /**
     * Initialize a new Label Object with its custom label
     * @param label the label of the action
     */
    Action(String label){
        this.label = label;
    }

    /**
     * Returns the label of the action
     * @return the label of the action
     */
    @Override
    public String toString(){
        return label;
    }
}
