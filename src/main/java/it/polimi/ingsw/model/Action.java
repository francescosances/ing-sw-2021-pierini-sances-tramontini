package it.polimi.ingsw.model;

public enum Action {
    SKIP("Go ahead / Skip"),
    MOVE_RESOURCES("Move resources"),
    DISCARD_LEADER ("Discard leader card"),
    PLAY_LEADER ("Play leader card"),
    CANCEL("Cancel");

    private final String label;

    Action(String label){
        this.label = label;
    }

    @Override
    public String toString(){
        return label;
    }
}
