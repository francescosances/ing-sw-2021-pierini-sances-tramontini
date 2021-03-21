package it.polimi.ingsw.Model;

import java.util.HashMap;
import java.util.Map;

public class Strongbox implements Storage{
    private final Map<ResourceType, Integer> resources;

    public Strongbox(){
        resources = new HashMap<ResourceType, Integer>();
    }

    public void addResources(Map<ResourceType, Integer> resources){
        resources.forEach((res, num) -> this.resources.put(res, this.resources.containsKey(res) ? this.resources.get(res) + num : num));
    }

    @Override
    public void removeResources(Map<ResourceType, Integer> resources){
        resources.forEach((res, num) -> this.resources.put(res, this.resources.get(res) - num));
    }

    public int getResourcesNum(ResourceType resource){
        return this.resources.get(resource);
    }

}
