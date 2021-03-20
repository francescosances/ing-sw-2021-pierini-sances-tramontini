package it.polimi.ingsw.Model;

import java.util.HashMap;
import java.util.Map;

public class Requirements {

    private Map<ResourceType,Integer> resources;

    private Map<DevelopmentColorType,Map<Integer,Integer>> developmentCards;

    public Requirements (Map<ResourceType,Integer> resources, Map<DevelopmentColorType,Map<Integer,Integer>> developmentCards){
        if (resources != null) {
            this.resources = new HashMap<>();
            this.resources.putAll(resources);
        } else
            this.resources = null;

        if (developmentCards != null){
            this.developmentCards = new HashMap<>();
            developmentCards.forEach((k, v) -> {
                HashMap x = new HashMap<Integer,Integer>();
                x.putAll(v);
                this.developmentCards.put(k, x);
            });
        } else
            this.developmentCards = null;
    }

    public boolean satisfied(PlayerBoard player){
        //TODO: controllare requisiti
        return false;
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
