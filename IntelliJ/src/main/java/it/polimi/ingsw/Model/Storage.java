package it.polimi.ingsw.Model;

import java.util.Map;

public interface Storage {
    public void removeResources(Map<ResourceType, Integer> resources);
}
