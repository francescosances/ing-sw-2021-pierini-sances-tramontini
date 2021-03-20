package it.polimi.ingsw.Model;

import java.util.HashMap;
import java.util.Map;

public class ProductionLeaderCard extends LeaderCard{
    private final Requirements productionCost;
    private final Map<Resource, Integer>  productionGain;

    public ProductionLeaderCard (int victoryPoints, Requirements requirements, Requirements productionCost, Map<Resource, Integer> productionGain) {
        super(victoryPoints, requirements);
        this.productionCost = productionCost;
        this.productionGain = new HashMap<Resource, Integer> ();
        this.productionGain.putAll(productionGain);
    }

    public void produce(){}

}
