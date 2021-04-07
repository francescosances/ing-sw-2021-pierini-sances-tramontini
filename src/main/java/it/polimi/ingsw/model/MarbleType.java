package it.polimi.ingsw.model;

import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;

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
