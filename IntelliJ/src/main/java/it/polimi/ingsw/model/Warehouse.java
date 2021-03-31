package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class Warehouse implements Storage{
    private final ArrayList<Depot> depots;
    private final ArrayList<Resource> toBeStored;

    public Warehouse(){
        depots = new ArrayList<>();
        depots.add(new StandardDepot(1));
        depots.add(new StandardDepot(2));
        depots.add(new StandardDepot(3));
        toBeStored = new ArrayList<>();
    }

    public void addResource(int depotNumber, ResourceType res, int num) {
        for (int i = 0; i < num; i++)
            depots.get(depotNumber).addResource(res);
    }

    @Override
    public Requirements removeResources(Requirements requirements) {
        Requirements newRequirements = (Requirements) requirements.clone();
        for (Map.Entry<Resource, Integer> res : requirements) {
            int toBeRemoved = res.getValue();
            for (Depot dep : depots)
                while (dep.getOccupied() > 0 && dep.getResourceType() == res.getKey() && toBeRemoved > 0) {
                    dep.removeResource();
                    toBeRemoved--;
                    newRequirements.removeResourceRequirement((ResourceType) res.getKey(), 1);
                }
        }
        return newRequirements;
    }

    public void toBeStored(Resource[] resources) {
        toBeStored.addAll(Arrays.asList(resources));
    }

    public void swapDepots(int first, int second) {
        if (depots.get(first).getOccupied() > depots.get(second).getSize() || depots.get(second).getOccupied() > depots.get(first).getSize())
            throw new IllegalArgumentException("Unable to swap selected depots");

        ResourceType firstResourceType = depots.get(first).getResourceType();
        ResourceType secondResourceType = depots.get(second).getResourceType();
        int firstOccupied = depots.get(first).getOccupied();
        int secondOccupied = depots.get(second).getOccupied();

        for (int i = 0; i < firstOccupied; i++)
            depots.get(first).removeResource();
        for (int i = 0; i < secondOccupied; i++)
            depots.get(second).removeResource();
        for (int i = 0; i < secondOccupied; i++)
            depots.get(first).addResource(secondResourceType);
        for (int i = 0; i < firstOccupied; i++)
            depots.get(second).addResource(firstResourceType);
    }

    public boolean hasResourcesToStore() {
        return !toBeStored.isEmpty();
    }

    public Resource popResourceToBeStored(){
        return toBeStored.remove(toBeStored.size() - 1);
    }

    public Requirements getAllResources(){
        Requirements ret = new Requirements();
        for(Depot x:depots){
            if(x.getOccupied() > 0)
                ret.addResourceRequirement(x.getResourceType(),x.getOccupied());
        }
        return ret;
    }

}
