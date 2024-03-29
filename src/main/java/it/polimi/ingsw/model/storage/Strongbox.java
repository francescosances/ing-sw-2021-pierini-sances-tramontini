package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.view.ObservableFromView;
import it.polimi.ingsw.view.View;

import java.util.*;

public class Strongbox implements Storage, ObservableFromView {
    /**
     * Representation of the amount of each resource present in the strongbox.
     */
    private final Map<ResourceType, Integer> resources;
    transient private List<View> views;

    /**
     * Creates a new empty strongbox.
     */
    public Strongbox() {
        resources = new HashMap<>();
        views = new ArrayList<>();
    }

    /**
     * Adds the given resources to the strongbox.
     * @param resources the resources to be added to the strongbox, with each resource type mapped to its amount
     */
    public void addResources(Map<ResourceType, Integer> resources) {
        resources.forEach((res, num) -> this.resources.put(res, this.resources.containsKey(res) ? this.resources.get(res) + num : num));
        updateViews();
    }

    /**
     * Adds the given resource to the strongbox.
     * @param resource the resources to be added to the strongbox
     */
    public void addResource(ResourceType resource){
        resources.put(resource, resources.containsKey(resource) ? resources.get(resource) + 1 : 1);
        updateViews();
    }

    /**
     * Removes the specified resources from the storage (as long as there are) and returns the missing resources that couldn't be removed.
     * @param resources the resources to be removed from the storage
     * @return the the missing resources that couldn't be removed
     */
    @Override
    public Requirements removeResources(Requirements resources) {
        Requirements newRequirements = resources.clone();
        for (Map.Entry<Resource, Integer> res : resources) {
            int toBeRemoved = res.getValue();
            int previousValue = getResourcesNum((ResourceType) res.getKey());
            if (previousValue < toBeRemoved) {
                toBeRemoved = previousValue;
                this.resources.remove(res.getKey());
            }
            else
                this.resources.put((ResourceType) res.getKey(), previousValue - toBeRemoved);
            newRequirements.removeResourceRequirement(res.getKey(), toBeRemoved);
        }
        if (!newRequirements.equals(resources))
            updateViews();
        return newRequirements;
    }

    /**
     * Returns the amount of the specified resource present in the strongbox.
     *
     * @param resource the resource to return the amount of
     * @return the amount of the specified resource present in the strongbox
     */
    public int getResourcesNum(ResourceType resource) {
        return this.resources.getOrDefault(resource, 0);
    }

    /**
     * Returns all resources in the strongbox
     * @return all resources in the strongbox
     */
    @Override
    public Requirements getAllResources() {
        Requirements ret = new Requirements();
        resources.forEach(ret::addResourceRequirement);
        return ret;
    }


    /**
     * Adds the view to the list of views
     * @param view the view that has to be added
     */
    @Override
    public void addView(View view) {
        if (views == null)
            views = new ArrayList<>();
        views.add(view);
    }

    /**
     * Removes the view from the list of views
     * @param view the view that has to be removed
     */
    @Override
    public void removeView(View view) {
        views.remove(view);
    }

    /**
     * Notifies all views of the change
     */
    private void updateViews() {
        views.forEach(view -> view.showStrongbox(this));
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
        Strongbox strongbox = (Strongbox) o;
        return Objects.equals(resources, strongbox.resources);
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "Strongbox{" +
                "resources=" + resources +
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