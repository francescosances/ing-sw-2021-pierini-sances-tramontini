package it.polimi.ingsw.Model;

import java.util.Map;

public class DevelopmentCard extends Card implements Producer {

    public static final int MAX_LEVEL = 3;

    private final Requirements cost;
    private final int level;
    private final DevelopmentColorType color;

    private Requirements productionCost;
    private Map<Resource,Integer> productionGain;

    public DevelopmentCard(int victoryPoints, Requirements requirements, int level, DevelopmentColorType color) {
        super(victoryPoints);
        this.cost = requirements;
        this.level = level;
        this.color = color;
    }

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
