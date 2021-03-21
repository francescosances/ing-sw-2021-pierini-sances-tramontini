package it.polimi.ingsw.Model;

import java.util.ArrayList;
import java.util.Map;

public class Warehouse implements Storage{
    private final ArrayList<Depot> depots;
    private final ArrayList<Resource> toBeStored;

    public Warehouse(){
        depots = new ArrayList<Depot>();
        depots.add(new StandardDepot(1));
        depots.add(new StandardDepot(2));
        depots.add(new StandardDepot(3));
        toBeStored = new ArrayList<Resource>();
    }

    public void addResource(int depotNumber, ResourceType res, int num) {
        for (int i = 0; i < num; i++)
            depots.get(depotNumber).addResource(res);
    }

    @Override
    public void removeResources(Map<ResourceType, Integer> resources) {
            resources.forEach((res, num) -> {
                int i = num;
                for (Depot dep : depots)
                    while (dep.getOccupied() > 0 && dep.getResourceType() == res && i > 0){
                        dep.removeResource();
                        i--;
                    }
            });
    }

    public void toBeStored(Resource[] resources) {
        for (Resource res : resources)
            toBeStored.add((ResourceType) res);
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

}
