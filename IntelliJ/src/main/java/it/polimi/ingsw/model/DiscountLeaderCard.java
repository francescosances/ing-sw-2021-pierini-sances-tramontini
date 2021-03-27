package it.polimi.ingsw.model;

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
}
