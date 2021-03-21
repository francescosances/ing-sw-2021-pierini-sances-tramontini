package it.polimi.ingsw.Model;

import Utils.Triple;
import com.sun.tools.javac.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class Requirements implements Cloneable{

    private Map<ResourceType,Integer> resources;

    /**
     * tipo, livello, quantita richiesta
     */
    private Map<DevelopmentColorType,Map<Integer,Integer>> developmentCards;

    protected Requirements(){
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

    public Requirements(Pair<ResourceType,Integer> ... resources){
        this();
        for(Pair<ResourceType,Integer> x: resources){
            this.resources.put(x.fst,x.snd);
        }
    }

    public Requirements (Map<ResourceType,Integer> resources, Map<DevelopmentColorType,Map<Integer,Integer>> developmentCards){
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
        //TODO: controllare requisiti risorse

        //DevelopmentCards check
        for(Map.Entry<DevelopmentColorType,Map<Integer,Integer>> entry: developmentCards.entrySet()){
            Map<Integer,Integer> temp = entry.getValue();
            for(Map.Entry<Integer,Integer> x : temp.entrySet()) {
                int toBeFound = x.getValue();
                for(DevelopmentCardSlot slot : player.getDevelopmentCardSlots()){
                    try {
                        if(slot.getFromLevel(x.getKey()).getColor() == entry.getKey())
                            toBeFound--;
                    }catch (IllegalStateException ignored){}
                }
                if(toBeFound > 0)
                    return false;
            }
        }
        return true;
    }

    public int getResources(ResourceType resource){
        return resources.get(resource);
    }

    public void addResourceRequirement(ResourceType resource,int quantity){
        resources.put(resource,resources.getOrDefault(resource,0)+quantity);
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
        if(temp == null)
            return 0;
        return temp.get(level);
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

}
