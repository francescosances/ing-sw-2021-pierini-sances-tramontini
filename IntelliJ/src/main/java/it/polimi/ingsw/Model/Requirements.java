package it.polimi.ingsw.Model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Requirements {

    private Map<ResourceType,Integer> resources;

    /**
     * tipo, livello, quantita richiesta
     */
    private Map<DevelopmentColorType,Map<Integer,Integer>> developmentCards;

    protected Requirements(){
        resources = new HashMap<>();
        developmentCards = new HashMap<>();
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

    public int getDevelopmentCards(DevelopmentColorType color,int level){
        Map<Integer,Integer> temp = developmentCards.get(color);
        if(temp == null)
            return 0;
        return temp.get(level);
    }


}
