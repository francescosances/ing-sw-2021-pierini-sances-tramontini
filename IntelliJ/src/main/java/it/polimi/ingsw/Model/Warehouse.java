package it.polimi.ingsw.Model;

import java.util.ArrayList;
import java.util.Map;

public class Warehouse implements Storage{
    private ArrayList<Depot> depots;
    private ArrayList<Resource> toBeStored;

    public Warehouse(){
        depots = new ArrayList<Depot>();
        depots.add(new StandardDepot(1));
        depots.add(new StandardDepot(2));
        depots.add(new StandardDepot(3));
        toBeStored = new ArrayList<Resource>();
    }

public void addResource(int depotNumber, ResourceType res, int num) {
}

@Override
public void removeResources(Map<ResourceType, Integer> resources) {
}

public void toBeStored(Resource[] resources) {
}

public void swapDepots(int first, int second) {
}

public boolean hasResourcesToStore() {
        return !this.toBeStored.isEmpty();
}

public void popResourceToBeStored() {
}


}
