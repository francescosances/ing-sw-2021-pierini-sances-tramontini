package it.polimi.ingsw.Model;

import java.util.Map;

public class Requirements {

    private Map<ResourceType,Integer> resources;

    private Map<DevelopmentColorType,Map<Integer,Integer>> developmentCards;

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
