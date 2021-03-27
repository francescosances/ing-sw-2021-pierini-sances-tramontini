package it.polimi.ingsw.Model;

import java.util.HashMap;
import java.util.Map;

public class Strongbox implements Storage{
    private final Map<ResourceType, Integer> resources;

    public Strongbox(){
        resources = new HashMap<>();
    }

    public void addResources(Map<ResourceType, Integer> resources){
        resources.forEach((res, num) -> this.resources.put(res, this.resources.containsKey(res) ? this.resources.get(res) + num : num));
    }

    @Override
    public Requirements removeResources(Requirements resources){
        Requirements newRequirements = (Requirements) resources.clone();
        resources.forEach((res) -> {
            int toBeRemoved = res.getValue();
            int previousValue = getResourcesNum(res.getKey());
            if(previousValue < toBeRemoved){
                toBeRemoved = previousValue;
            }
            this.resources.put(res.getKey(), previousValue-toBeRemoved);
            newRequirements.removeResourceRequirement(res.getKey(),toBeRemoved);
        });
        return newRequirements;
    }

    public int getResourcesNum(ResourceType resource){
        return this.resources.getOrDefault(resource,0);
    }

    public Requirements getAllResources(){
        Requirements ret = new Requirements();
        resources.forEach(ret::addResourceRequirement);
        return ret;
    }

}
