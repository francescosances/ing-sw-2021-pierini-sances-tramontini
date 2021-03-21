package it.polimi.ingsw.Model;

import java.util.HashMap;
import java.util.Map;

public class Strongbox implements Storage{
    private Map<ResourceType, Integer> resources;

    public Strongbox(){
        resources = new HashMap<ResourceType, Integer>();
    }

    public void addResources(Map<ResourceType, Integer> resources){}

    @Override
    public void removeResources(Map<ResourceType, Integer> resources){}

    public int getResourcesNum(ResourceType resource){
        return this.resources.get(resource);
    }

}
