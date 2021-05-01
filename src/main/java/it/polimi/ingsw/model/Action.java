package it.polimi.ingsw.model;

public enum
Action{
    /**
     * Enlists the possible actions a player can do when is their turn.
     */
    SKIP("Go ahead / Skip"),
    MOVE_RESOURCES("Move resources"),
    DISCARD_LEADER ("Discard leader card"),
    PLAY_LEADER ("Play leader card"),

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
    public static final Action[] ALL_ACTIONS = {TAKE_RESOURCES_FROM_MARKET,BUY_DEVELOPMENT_CARD,ACTIVATE_PRODUCTION,MOVE_RESOURCES,DISCARD_LEADER,PLAY_LEADER};

    /**
     * Array that contains all normal actions
     */
    public static final Action[] NORMAL_ACTIONS = {SKIP,MOVE_RESOURCES,DISCARD_LEADER,PLAY_LEADER};

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
