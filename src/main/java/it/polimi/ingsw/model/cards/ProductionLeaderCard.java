package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Producer;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.Resource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductionLeaderCard extends LeaderCard implements Producer {
    private final Requirements productionCost;
    private final Map<Resource, Integer>  productionGain;

    public ProductionLeaderCard (int victoryPoints, Requirements requirements, Requirements productionCost) {
        super(victoryPoints, requirements);
        this.productionCost = productionCost;
        this.productionGain = new HashMap<>();
        productionGain.put(NonPhysicalResourceType.ON_DEMAND,1);
        productionGain.put(NonPhysicalResourceType.FAITH_POINT,1);
    }
    public ProductionLeaderCard (int victoryPoints, Requirements requirements, Requirements productionCost, boolean active) {
        super(victoryPoints, requirements, active);
        this.productionCost = productionCost;
        this.productionGain = new HashMap<>();
        productionGain.put(NonPhysicalResourceType.ON_DEMAND,1);
        productionGain.put(NonPhysicalResourceType.FAITH_POINT,1);
    }

    @Override
    public void produce(){}

    @Override
    public String toString() {
        return "ProductionLeaderCard: " +
                (active ? "active" : "inactive") +
                ", requirements=" + requirements +
                ", productionCost=" + productionCost +
                ", productionGain=[" + productionGain.entrySet().stream().map(entry -> entry.getValue() + " " + entry.getKey()).collect(Collectors.joining(", ")) + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof ProductionLeaderCard))
            return false;
        ProductionLeaderCard that = (ProductionLeaderCard) other;
        return this.productionCost.equals(that.productionCost) && this.requirements.equals(that.requirements)
                && this.active == that.active && this.getVictoryPoints() == that.getVictoryPoints();

    }
}
