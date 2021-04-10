package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.cards.Requirements;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Strongbox implements Storage {
    /**
     * Representation of the amount of each resource present in the strongbox.
     */
    private final Map<ResourceType, Integer> resources;

    /**
     * Creates a new empty strongbox.
     */
    public Strongbox(){
        resources = new HashMap<>();
    }

    /**
     * Adds the given resources to the strongbox.
     * @param resources the resources to be added to the strongbox, with each resource type mapped to its amount
     */
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

    /**
     * Returns the amount of the specified resource present in the strongbox.
     * @param resource the resource to return the amount of
     * @return the amount of the specified resource present in the strongbox
     */
    public int getResourcesNum(ResourceType resource){
        return this.resources.getOrDefault(resource,0);
    }

    @Override
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
