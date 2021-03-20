package it.polimi.ingsw.Model;

import java.util.Map;

public class DevelopmentCard extends Card implements Producer{

    private Requirements cost;
    private int level;
    private DevelopmentColorType color;

    private Requirements productionCost;
    private Map<Resource,Integer> productionGain;

    public Requirements getCost() {
        return cost;
    }

    public int getLevel() {
        return level;
    }

    public DevelopmentColorType getColor() {
        return color;
    }

    public Requirements getProductionCost() {
        return productionCost;
    }

    public Map<Resource, Integer> getProductionGain() {
        return productionGain;
    }

    @Override
    public void produce() {

    }
}
