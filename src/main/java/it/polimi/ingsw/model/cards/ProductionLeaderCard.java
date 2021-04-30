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
    /**
     * The cost that a player has to pay to trigger the production
     */
    private final Requirements productionCost;
    /**
     * What the player gains if the production is triggered
     */
    private final Map<Resource, Integer> productionGain;

    /**
     * Initializes a new ProductionLeaderCard
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player has to satisfy so as to play the card
     * @param productionCost cost that a player has to pay to trigger the production
     */
    public ProductionLeaderCard (int victoryPoints, Requirements requirements, Requirements productionCost) {
        super(victoryPoints, requirements);
        this.productionCost = productionCost;
        this.productionGain = new HashMap<>();
        productionGain.put(NonPhysicalResourceType.ON_DEMAND,1);
        productionGain.put(NonPhysicalResourceType.FAITH_POINT,1);
    }

    /**
     * Initializes a new ProductionLeaderCard. Manually sets the active status
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player has to satisfy so as to play the card
     * @param productionCost cost that a player has to pay to trigger the production
     * @param active the status of the card
     */
    public ProductionLeaderCard (int victoryPoints, Requirements requirements, Requirements productionCost, boolean active) {
        super(victoryPoints, requirements, active);
        this.productionCost = productionCost;
        this.productionGain = new HashMap<>();
        productionGain.put(NonPhysicalResourceType.ON_DEMAND,1);
        productionGain.put(NonPhysicalResourceType.FAITH_POINT,1);
    }

    @Override
    public boolean isProductionLeaderCards() {
        return true;
    }

    @Override
    public Map<Resource, Integer> getProductionCost() {
        return productionCost.getResourcesMap();
    }

    @Override
    public Map<Resource, Integer> getProductionGain() {
        return productionGain;
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "ProductionLeaderCard: " +
                (active ? "active" : "inactive") +
                ", requirements=" + requirements +
                ", productionCost=" + productionCost +
                ", productionGain=[" + productionGain.entrySet().stream().map(entry -> entry.getValue() + " " + entry.getKey()).collect(Collectors.joining(", ")) + "]";
    }

    /**
     * Indicates whether some other object is equal to this one
     * @param other that is confronted
     * @return true if o is equal to the object, false elsewhere
     */
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
