package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.storage.ResourceType;

public class DiscountLeaderCard extends LeaderCard {
    private final ResourceType discountResourceType;
    private final int discount;


    public DiscountLeaderCard (int victoryPoints, Requirements requirements, ResourceType resourceType) {
        this (victoryPoints, requirements, resourceType, 1);
    }


    public DiscountLeaderCard (int victoryPoints, Requirements requirements, ResourceType resourceType, int discount) {
        super(victoryPoints, requirements);
        this.discountResourceType = resourceType;
        this.discount = discount;

    }

    @Override
    public Requirements recalculateRequirements(Requirements requirements) {
        requirements = super.recalculateRequirements(requirements);
        if(isActive())
            requirements.removeResourceRequirement(discountResourceType,discount);
        return requirements;
    }


    @Override
    public String toString() {
        return "DiscountLeaderCard{" +
                "discountResourceType=" + discountResourceType +
                ", discount=" + discount +
                ", active=" + active +
                ", requirements=" + requirements +
                '}';
    }
}