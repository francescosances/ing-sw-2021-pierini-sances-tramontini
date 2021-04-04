package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Producer;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.utils.Pair;

public class DevelopmentCard extends Card implements Producer {

    public static final int MAX_LEVEL = 3;

    private Requirements cost;
    private int level;
    private DevelopmentColorType color;

    private Requirements productionCost;
    private Requirements productionGain;

    private DevelopmentCard(int victoryPoints){
        super(victoryPoints);
    }

    public DevelopmentCard(int victoryPoints, Requirements cost, int level, DevelopmentColorType color, Requirements productionCost, Pair<Resource,Integer> ... productionGain) {
        super(victoryPoints);
        this.cost = cost;
        this.level = level;
        this.color = color;
        this.productionCost = productionCost;
        this.productionGain = new Requirements();
        for(Pair<Resource,Integer> x : productionGain){
            this.productionGain.addResourceRequirement(x.fst,x.snd);
        }
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

    public Requirements getProductionGain() {
        return productionGain;
    }

    @Override
    public void produce() {

    }
}
