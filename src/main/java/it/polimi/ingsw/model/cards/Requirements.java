package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class Requirements implements Cloneable, Iterable<Map.Entry<Resource, Integer>> {

    private Map<Resource, Integer> resources;

    /**
     * Stores the development card the player needs to have to satisfy the requirement
     * DevelopmentColorType, level (0 if not specified), quantity
     */
    private Map<DevelopmentColorType,Map<Integer,Integer>> developmentCards;

    /**
     * Sets a new empty requirements object
     */
    public Requirements(){
        resources = new HashMap<>();
        developmentCards = new HashMap<>();
    }

    /**
     * Sets a new Requirements object, needing development card so as to be satisfied
     * @param developmentCards the DevelopmentCards the player must own so as to satisfy the Requirements
     */
    public Requirements(Triple<DevelopmentColorType,Integer,Integer> ... developmentCards){
        this();
        for(Triple<DevelopmentColorType,Integer,Integer> x: developmentCards){
            this.developmentCards.putIfAbsent(x.getFirst(),new HashMap<>());
            this.developmentCards.get(x.getFirst()).put(x.getSecond(),x.getThird());
        }
    }

    /**
     * Sets a new Requirements object, needing resources so as to be satisfied
     * @param resources the resources needed
     */
    public Requirements(Pair<Resource,Integer>... resources){
        this();
        for(Pair<Resource,Integer> x: resources){
            this.resources.put(x.fst,x.snd);
        }
    }

    /**
     * Sets a new Requirements object needing both development cards and resources so as to be satisfied
     * @param resources the resources needed
     * @param developmentCards the development cards needed
     */
    public Requirements (Map<Resource,Integer> resources, Map<DevelopmentColorType,Map<Integer,Integer>> developmentCards){
        this();
        if (resources != null)
            this.resources.putAll(resources);

        if (developmentCards != null)
            developmentCards.forEach((k, v) -> {
                Map<Integer,Integer> x = new HashMap<>(v);
                this.developmentCards.put(k, x);
            });
    }

    /**
     * Checks if the player satisfies the requirements
     * @param player the player who owns the cards or the resources
     * @return true if the requirements are satisfied, false elsewhere
     */
    public boolean satisfied(PlayerBoard player){
        //Resources check
        Requirements playerResources = player.getAllResources();
        for(Map.Entry<Resource,Integer> x : this.resources.entrySet()){
            if(x.getValue() > playerResources.getResources(x.getKey())) {
                return false;
            }
        }

        //DevelopmentCards check
        for(Map.Entry<DevelopmentColorType,Map<Integer,Integer>> entry: developmentCards.entrySet()){
            Map<Integer,Integer> temp = entry.getValue();
            for(Map.Entry<Integer,Integer> x : temp.entrySet()) {
                int toBeFound = x.getValue();
                for(DevelopmentCardSlot slot : player.getDevelopmentCardSlots())
                    toBeFound -= slot.getCardsNum(entry.getKey(), x.getKey());
                if(toBeFound > 0)
                    return false;
            }
        }
        return true;
    }

    /**
     * Returns how many units of the resource specified the requirements needs to satisfy
     * @param resource the resource whose units requested are asked
     * @return the number of the units asked
     */
    public int getResources(Resource resource){
        return resources.getOrDefault(resource, 0);
    }

    /**
     * Returns the sum of the two Requirements
     * @param first one of the two Requirements to sum
     * @param second the other Requirements to sum
     * @return the sum of the two Requirements
     */
    public static Requirements sum(Requirements first,Requirements second){
        Requirements ret = (Requirements) first.clone();
        second.resources.forEach(ret::addResourceRequirement);
        second.developmentCards.forEach((colorType,k)-> k.forEach((level, num)-> ret.addDevelopmentCardRequirement(colorType,level,num)));
        return ret;
    }

    /**
     * Adds a resource to the Requirements
     * @param resource the ResourceType to add
     * @param quantity the quantity of the resource to add
     */
    public void addResourceRequirement(Resource resource, int quantity){
        resources.put(resource, resources.getOrDefault(resource,0)+quantity);
    }

    /**
     * Removes a resource to the Requirements
     * @param resource the ResourceType to remove
     * @param quantity the quantity of the resource to remove
     */
    public void removeResourceRequirement(ResourceType resource,int quantity){
        resources.put(resource,resources.getOrDefault(resource,quantity)-quantity);
        if(resources.get(resource) <= 0)
            resources.remove(resource);
    }

    /**
     * adds a development card to the requirements
     * @param type the DevelopmentColorType of the card
     * @param level the level of the card
     * @param quantity the quantity of the cards needed
     */
    public void addDevelopmentCardRequirement(DevelopmentColorType type,int level,int quantity){
        developmentCards.computeIfAbsent(type, k -> new HashMap<>());
        developmentCards.get(type).put(level,quantity);
    }

    /**
     * Returns how many DevelopmentCards with the specified DevelopmentColorType and the specified level are needed so as to satisfy the requirements
     * @param color the DevelopmentColorType of the card
     * @param level the level of the card
     * @return the number needed
     */
    public int getDevelopmentCards(DevelopmentColorType color,int level){
        Map<Integer,Integer> temp = developmentCards.get(color);
        if(temp == null || temp.get(level) == null)
            return 0;
        return temp.get(level);
    }

    /**
     * Indicates whether some other object is equal to this one
     * @param other that is confronted
     * @return true if o is equal to the object, false elsewhere
     */
    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (other instanceof Requirements) {
            Requirements otherRequirements = (Requirements) other;
            return this.resources.equals(otherRequirements.resources) && this.developmentCards.equals(otherRequirements.developmentCards);
        }
        return false;
    }

    /**
     * Returns a clone of the object
     * @return a clone of the object
     */
    @Override
    public Requirements clone(){
        Requirements ris = new Requirements();
        ris.resources.putAll(this.resources);
        for(Map.Entry<DevelopmentColorType,Map<Integer,Integer>> entry: developmentCards.entrySet()){
            Map<Integer,Integer> temp = new HashMap<>();
            for(Map.Entry<Integer,Integer> x : entry.getValue().entrySet()) {
                temp.put(x.getKey(),x.getValue());
            }
            ris.developmentCards.put(entry.getKey(),temp);
        }
        return ris;
    }

    /**
     * Returns an Iterator of the Requirements class
     * @return an Iterator of the Requirements class
     */
    @Override
    public Iterator<Map.Entry<Resource, Integer>> iterator() {
        return resources.entrySet().iterator();
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        String res = "resources=(" +
                resources.entrySet().stream().map(entry -> entry.getValue() + " " + entry.getKey()).collect(Collectors.joining(", "))
                + ")";
        String devCards = "developmentCards=(" +
                developmentCards.entrySet().stream().map(entry1 -> entry1.getValue().entrySet().stream().map(entry2 -> entry2.getValue() + (entry2.getKey()==0 ? " any lv." : (" lv. " + entry2.getKey())) + " " + entry1.getKey()).collect(Collectors.joining(", "))).collect(Collectors.joining(", "))
                + ")";

        return "[" +
                (resources.isEmpty() ? (developmentCards.isEmpty() ? "" : devCards) : (developmentCards.isEmpty() ? res : res + ", " + devCards))
                + ']';
    }
}
