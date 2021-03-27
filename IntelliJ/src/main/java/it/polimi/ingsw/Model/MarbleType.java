package it.polimi.ingsw.Model;

public enum MarbleType {
    WHITE,
    BLUE,
    GREY,
    YELLOW,
    PURPLE,
    RED;

    public Resource toResource(){
        switch (this) {
            case GREY:
                return ResourceType.STONE;
            case BLUE:
                return ResourceType.SHIELD;
            case PURPLE:
                return ResourceType.SERVANT;
            case YELLOW:
                return ResourceType.COIN;
            case RED:
                return NonPhysicalResourceType.FAITH_POINT;
            default:
                return NonPhysicalResourceType.VOID;
        }
    }
}
