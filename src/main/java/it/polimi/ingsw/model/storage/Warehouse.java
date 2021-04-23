package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.model.storage.exceptions.UnswappableDepotsException;

import java.util.*;

public class Warehouse implements Storage {
    /**
     * The list of the depots in the warehouse.
     */
    private final ArrayList<Depot> depots;

    /**
     * The list of resources taken from the market that need to be stored in the depots.
     */
    private final ArrayList<Resource> toBeStored;

    /**
     * Creates a new warehouse with 3 empty standard depots.
     */
    public Warehouse(){
        depots = new ArrayList<>();
        depots.add(new StandardDepot(1));
        depots.add(new StandardDepot(2));
        depots.add(new StandardDepot(3));
        toBeStored = new ArrayList<>();
    }

    /**
     * Adds the specified resources to the specified depot.
     * @param depotNumber the number of the depot where to add the resources
     * @param res the type of the resources to be added
     * @param num the amount of the resources to be added
     * @throws IncompatibleDepotException if is already existing a depot with the same resource type
     */
    public void addResource(int depotNumber, ResourceType res, int num) throws IncompatibleDepotException {
        int depotIndex = 0;
        for(Depot depot : depots)
            if(depotIndex++ != depotNumber && depot.getOccupied() > 0 && depot.getResourceType() == res)
                throw new IncompatibleDepotException("You can’t place the same type of Resource in two different depots.");
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

    /**
     * Adds the specified resources to the toBeStored storage, that will need to be stored in the depots or discarded.
     * @param resources the resources to be added
     */
    public void toBeStored(Resource[] resources) {
        toBeStored.addAll(Arrays.asList(resources));
    }

    //TODO: javadoc
    public void pushResourceToBeStored(Resource resource){
        toBeStored.add(0,resource);
    }

    /**
     * Swaps the content of the two specified depots.
     * @param first the number of the first depot
     * @param second the number of the second depot
     */
    public void swapDepots(int first, int second) throws IncompatibleDepotException {
        if (first == second || depots.get(first).getOccupied() > depots.get(second).getSize() || depots.get(second).getOccupied() > depots.get(first).getSize())
            throw new UnswappableDepotsException("Unable to swap selected depots");

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

    /**
     * Returns true if the toBeStored storage contains some resources that need to be stored in the depots or discarded.
     * @return true if the toBeStored storage contains some resources that need to be stored in the depots or discarded
     */
    public boolean hasResourcesToStore() {
        return !toBeStored.isEmpty();
    }

    /**
     * Returns and removes the first resource in the toBeStored storage that needs to be stored in a depot or discarded.
     * @return the first resource in the toBeStored storage that needs to be stored in a depot or discarded
     */
    public Resource popResourceToBeStored(){
        return toBeStored.remove(toBeStored.size() - 1);
    }

    @Override
    public Requirements getAllResources(){
        Requirements ret = new Requirements();
        for(Depot x:depots){
            if(x.getOccupied() > 0)
                ret.addResourceRequirement(x.getResourceType(),x.getOccupied());
        }
        return ret;
    }

    /**
     * Returns the list of depots that compose the warehouse
     * @return the list of depots that compose the warehouse
     */
    public List<Depot> getDepots(){
        return this.depots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warehouse warehouse = (Warehouse) o;
        return Objects.equals(depots, warehouse.depots) && Objects.equals(toBeStored, warehouse.toBeStored);
    }

    public void addDepotLeaderCard(LeaderCard leaderCard){
        Depot depotLeaderCard = (Depot) leaderCard;
        depots.add(depotLeaderCard);
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "depots=" + depots +
                ", toBeStored=" + toBeStored +
                '}';
    }
}
