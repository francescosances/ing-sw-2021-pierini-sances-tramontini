package it.polimi.ingsw.Model;

import java.util.HashMap;
import java.util.Map;

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
        if (requirements.getResources(discountResourceType) > 0) {
            Map<ResourceType, Integer> updatedRequirements = new HashMap<ResourceType, Integer>();
            //TODO: ricopiare requirements senza la risorsa scontata
            return new Requirements(updatedRequirements, null);
        } else
            return requirements;
    }
}
