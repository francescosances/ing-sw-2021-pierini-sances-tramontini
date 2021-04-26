package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.DevelopmentCardSlot;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class Requirements implements Cloneable, Iterable<Map.Entry<Resource, Integer>> {

    private Map<Resource, Integer> resources;

    /**
     * tipo, livello (0 se qualsiasi), quantita richiesta
     */
    private Map<DevelopmentColorType,Map<Integer,Integer>> developmentCards;

    public Requirements(){
        resources = new HashMap<>();
        developmentCards = new HashMap<>();
    }

    public Requirements(Triple<DevelopmentColorType,Integer,Integer> ... developmentCards){
        this();
        for(Triple<DevelopmentColorType,Integer,Integer> x: developmentCards){
            this.developmentCards.putIfAbsent(x.getFirst(),new HashMap<>());
            this.developmentCards.get(x.getFirst()).put(x.getSecond(),x.getThird());
        }
    }

    public Requirements(Pair<Resource,Integer>... resources){
        this();
        for(Pair<Resource,Integer> x: resources){
            this.resources.put(x.fst,x.snd);
        }
    }

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

    public int getResources(Resource resource){
        return resources.getOrDefault(resource, 0);
    }

    public static Requirements sum(Requirements first,Requirements second){
        Requirements ret = (Requirements) first.clone();
        second.resources.forEach(ret::addResourceRequirement);
        second.developmentCards.forEach((colorType,k)-> k.forEach((level, num)-> ret.addDevelopmentCardRequirement(colorType,level,num)));
        return ret;
    }

    public void addResourceRequirement(Resource resource, int quantity){
        resources.put(resource, resources.getOrDefault(resource,0)+quantity);
    }

    public void removeResourceRequirement(ResourceType resource,int quantity){
        resources.put(resource,resources.getOrDefault(resource,quantity)-quantity);
        if(resources.get(resource) <= 0)
            resources.remove(resource);
    }

    public void addDevelopmentCardRequirement(DevelopmentColorType type,int level,int quantity){
        developmentCards.computeIfAbsent(type, k -> new HashMap<>());
        developmentCards.get(type).put(level,quantity);
    }

    public int getDevelopmentCards(DevelopmentColorType color,int level){
        Map<Integer,Integer> temp = developmentCards.get(color);
        if(temp == null || temp.get(level) == null)
            return 0;
        return temp.get(level);
    }

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

    @Override
    public Object clone(){
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

    @Override
    public Iterator<Map.Entry<Resource, Integer>> iterator() {
        return resources.entrySet().iterator();
    }

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
