package it.polimi.ingsw.Model;

import java.util.HashMap;
import java.util.Map;

public class ProductionLeaderCard extends LeaderCard{
    private final Requirements productionCost;
    private final Map<Resource, Integer>  productionGain;

    public ProductionLeaderCard (int victoryPoints, Requirements requirements, Requirements productionCost) {
        super(victoryPoints, requirements);
        this.productionCost = productionCost;
        this.productionGain = new HashMap<>();
        productionGain.put(NonPhysicalResourceType.ON_DEMAND,1);
        productionGain.put(NonPhysicalResourceType.FAITH_POINT,1);
    }

    public void produce(){}

}
