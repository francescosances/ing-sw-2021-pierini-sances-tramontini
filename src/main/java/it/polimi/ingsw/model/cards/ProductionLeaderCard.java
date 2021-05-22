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
    private final Requirements productionGain;

    /**
     * Initializes a new ProductionLeaderCard
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player has to satisfy so as to play the card
     * @param productionCost cost that a player has to pay to trigger the production
     */
    public ProductionLeaderCard (String cardName,int victoryPoints, Requirements requirements, Requirements productionCost) {
        this(cardName,victoryPoints, requirements, productionCost, false);
    }

    /**
     * Initializes a new ProductionLeaderCard. Manually sets the active status
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player has to satisfy so as to play the card
     * @param productionCost cost that a player has to pay to trigger the production
     * @param active the status of the card
     */
    public ProductionLeaderCard (String cardName,int victoryPoints, Requirements requirements, Requirements productionCost, boolean active) {
        super(cardName,victoryPoints, requirements, active);
        this.productionCost = productionCost;
        this.productionGain = new Requirements();
        productionGain.addResourceRequirement(NonPhysicalResourceType.ON_DEMAND,1);
        productionGain.addResourceRequirement(NonPhysicalResourceType.FAITH_POINT,1);
    }

    @Override
    public boolean isProductionLeaderCard() {
        return true;
    }

    @Override
    public Requirements getProductionCost() {
        return productionCost;
    }

    @Override
    public Requirements getProductionGain() {
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
                ", productionGain=" + productionGain;
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

    /**
     * Returns the hash code of the object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
