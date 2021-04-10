package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.cards.Requirements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Strongbox implements Storage {
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
        for (Map.Entry<Resource, Integer> res : resources) {
            int toBeRemoved = res.getValue();
            int previousValue = getResourcesNum((ResourceType) res.getKey());
            if (previousValue < toBeRemoved) {
                toBeRemoved = previousValue;
            }
            this.resources.put((ResourceType) res.getKey(), previousValue - toBeRemoved);
            newRequirements.removeResourceRequirement((ResourceType) res.getKey(), toBeRemoved);
        }
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Strongbox strongbox = (Strongbox) o;
        return Objects.equals(resources, strongbox.resources);
    }
}
