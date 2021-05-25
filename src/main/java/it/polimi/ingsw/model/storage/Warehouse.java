package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.cards.DepotLeaderCard;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import it.polimi.ingsw.model.storage.exceptions.UnswappableDepotsException;
import it.polimi.ingsw.utils.ObservableFromView;
import it.polimi.ingsw.view.VirtualView;

import java.util.*;

public class Warehouse implements Storage, ObservableFromView {

    /**
     * The number of StandardDepots every warehouse owns
     */
    private static final int STD_DEPOT_NUM = 3;
    /**
     * The list of the depots in the warehouse.
     */
    private final ArrayList<Depot> depots;

    /**
     * The list of resources taken from the market that need to be stored in the depots.
     */
    private final Stack<Resource> toBeStored;

    /**
     * The list containing the views to update
     */
    private transient List<VirtualView> views;

    /**
     * Creates a new warehouse with STD_DEPOT_NUMBER empty standard depots.
     */
    public Warehouse(){
        depots = new ArrayList<>();
        for (int i = 1; i <= STD_DEPOT_NUM; i++)
            depots.add(new StandardDepot(i));
        toBeStored = new Stack<>();
        views = new ArrayList<>();
    }

    /**
     * Adds the specified resources to the specified depot.
     * Notifies of the change happened.
     * @param depotNumber the number of the depot where to add the resources
     * @param res the type of the resources to be added
     * @param num the amount of the resources to be added
     * @throws IncompatibleDepotException if is already existing a standard depot with the same resource type
     */
    public void addResources(int depotNumber, ResourceType res, int num) throws IncompatibleDepotException {
        putResources(depotNumber, res, num);
        updateNonPlayingViews();
    }

    /**
     * Adds the specified resources to the specified depot.
     * @param depotNumber the number of the depot where to add the resources
     * @param res the type of the resources to be added
     * @param num the amount of the resources to be added
     * @throws IncompatibleDepotException if is already existing a standard depot with the same resource type
     */
    private void putResources(int depotNumber, ResourceType res, int num) throws IncompatibleDepotException {
        if (depotNumber < STD_DEPOT_NUM)
            for (int depotIndex = 0; depotIndex < STD_DEPOT_NUM; depotIndex++)
                if (depotIndex != depotNumber && depots.get(depotIndex).getResourceType() == res)
                    throw new IncompatibleDepotException("You can’t place the same type of Resource in two different standard depots.");
        for (int i = 0; i < num; i++)
            depots.get(depotNumber).addResource(res);
    }

    /**
     * Removes the specified resources from the storage (as long as there are) and returns the missing resources that couldn't be removed.
     * @param requirements the resources to be removed from the storage
     * @return the the missing resources that couldn't be removed
     */
    @Override
    public Requirements removeResources(Requirements requirements) {
        Requirements newRequirements = requirements.clone();
        for (Map.Entry<Resource, Integer> res : requirements) {
            int toBeRemoved = res.getValue();

            Depot depot;
            for (int i = 0; i < depots.size(); i++) {
                depot = depots.get(i);
                if (depot.getOccupied() > 0 && depot.getResourceType() == res.getKey() && toBeRemoved > 0){
                    int oldToBeRemoved = toBeRemoved;
                    toBeRemoved = removeResources(i, toBeRemoved);
                    newRequirements.removeResourceRequirement(res.getKey(), oldToBeRemoved - toBeRemoved);
                }
            }
        }
        if (!newRequirements.equals(requirements))
            updateAllViews();
        return newRequirements;
    }

    /**
     * Removes the selected resources from the selected depot if possible.
     * returns the number of resources that are still to be removed.
     * @param depotNumber the number of the depot from which the resources are to be removed
     * @param num the number of resources to remove
     * @return the number of resources that are still to be removed
     */
    private int removeResources(int depotNumber, int num) {
        int number = Math.min(num, depots.get(depotNumber).getOccupied());
        for (int i = 0; i < number; i++)
            depots.get(depotNumber).removeResource();
        return num - number;
    }

    /**
     * Adds the specified resources to the toBeStored storage, that will need to be stored in the depots or discarded.
     * @param resources the resources to be added
     */
    public void toBeStored(Resource[] resources) {
        toBeStored.addAll(Arrays.asList(resources));
    }

    /**
     * Adds the resource on top of the toBeStored Stack
     * @param resource the resource that is pushed
     */
    public void pushResourceToBeStored(Resource resource){
        toBeStored.push(resource);
    }

    /**
     * Swaps the content of the two specified depots.
     * @param first the number of the first depot
     * @param second the number of the second depot
     */
    public void swapDepots(int first, int second) throws IncompatibleDepotException {
        if (first == second || first >= depots.size() || second >= depots.size()
                || depots.get(first).getOccupied() > depots.get(second).getSize()
                || depots.get(second).getOccupied() > depots.get(first).getSize())
            throw new UnswappableDepotsException("Unable to swap selected depots");

        ResourceType firstResourceType = depots.get(first).getResourceType();
        ResourceType secondResourceType = depots.get(second).getResourceType();
        int firstOccupied = depots.get(first).getOccupied();
        int secondOccupied = depots.get(second).getOccupied();

        if (firstOccupied == secondOccupied && firstOccupied == 0)
            return;

        try {
            removeResources(first, firstOccupied);
            removeResources(second, secondOccupied);
            if (secondResourceType != null)
                putResources(first, secondResourceType, secondOccupied);
            if (firstResourceType != null)
                putResources(second, firstResourceType, firstOccupied);
        } catch (IncompatibleDepotException e) {
            removeResources(first, depots.get(first).getOccupied());
            removeResources(second, depots.get(second).getOccupied());
            addResources(first, firstResourceType, firstOccupied);
            addResources(second, secondResourceType, secondOccupied);
            throw new UnswappableDepotsException("Unable to swap selected depots, you chose a Depot Leader Card which couldn't be used");
        }

        updateAllViews();
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
        updateToBeStored();
        return toBeStored.pop();
    }

    /**
     * Returns a Requirements containing all resources in the warehouse
     * @return a Requirements containing all resources in the warehouse
     */
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

    /**
     * Adds a DepotLeaderCard to warehouse's available depots
     * @param depotLeaderCard the LeaderCard to add
     */
    public void addDepotLeaderCard(DepotLeaderCard depotLeaderCard){
        depots.add(depotLeaderCard);
    }

    /**
     * Adds the view to the list of views
     * @param view the view that has to be added
     */
    @Override
    public void addView(VirtualView view) {
        if (views == null)
            views = new ArrayList<>();
        views.add(view);
    }

    /**
     * Removes the view from the list of views
     * @param view the view that has to be removed
     */
    @Override
    public void removeView(VirtualView view) {
        views.remove(view);
    }

    /**
     * Notifies all views but the currently active player of the change
     */
    private void updateNonPlayingViews() {
        for (VirtualView view:views) {
            if (!view.getCurrentActiveUser().equals(view.getUsername()))
                view.showWarehouse(this);
        }
    }

    /**
     * Notifies all views of the change
     */
    private void updateAllViews() {
        for (VirtualView view:views)
            view.showWarehouse(this);
    }

    /**
     * Notifies all views of the change
     */
    private void updateToBeStored() {
        views.forEach(view -> view.showResourcesGainedFromMarket(toBeStored.toArray(new Resource[0])));
    }

    /**
     * Indicates whether some other object is equal to this one
     * @param o that is confronted
     * @return true if o is equal to the object, false elsewhere
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warehouse warehouse = (Warehouse) o;
        return Objects.equals(depots, warehouse.depots) && Objects.equals(toBeStored, warehouse.toBeStored);
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Warehouse{" +
                "depots=" + depots +
                ", toBeStored=" + toBeStored +
                '}';
    }

    /**
     * Returns the hash code of the object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
