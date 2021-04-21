package it.polimi.ingsw.model;

public enum
Action{
    SKIP("Go ahead / Skip"),
    MOVE_RESOURCES("Move resources"),
    DISCARD_LEADER ("Discard leader card"),
    PLAY_LEADER ("Play leader card"),

    CANCEL("Cancel"),

    TAKE_RESOURCES_FROM_MARKET("Take resources from market"),
    BUY_DEVELOPMENT_CARD ("Buy development card"),
    ACTIVATE_PRODUCTION ("Activate production");

    private final String label;

    public static final Action[] EXTRA_ACTIONS = {SKIP,MOVE_RESOURCES,DISCARD_LEADER,PLAY_LEADER};
    public static final Action[] NORMAL_ACTIONS = {TAKE_RESOURCES_FROM_MARKET,BUY_DEVELOPMENT_CARD,ACTIVATE_PRODUCTION};

    Action(String label){
        this.label = label;
    }

    @Override
    public String toString(){
        return label;
    }
}
