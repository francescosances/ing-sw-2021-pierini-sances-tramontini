package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.storage.ResourceType;

public class DiscountLeaderCard extends LeaderCard {
    /**
     * The resource the card discounts
     */
    private final ResourceType discountResourceType;
    /**
     * The units the card discounts
     */
    private final int discount;

    //TODO: tutti i javadoc delle carte leader vanno aggiornati aggiungendo il parametro cardName
    /**
     * Initializes a new DiscountLeaderCardObject. Automatically sets the discount to 1 unit.
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player has to satisfy if they want to activate the card
     * @param resourceType the Resource the card discounts you a unit of
     */
    public DiscountLeaderCard (String cardName,int victoryPoints, Requirements requirements, ResourceType resourceType) {
        this (cardName,victoryPoints, requirements, resourceType, 1);
    }

    /**
     * Initializes a new DiscountLeaderCardObject
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player has to satisfy if they want to activate the card
     * @param resourceType the Resource the card discounts you a unit of
     * @param discount the number of units the card lets you discount
     */
    public DiscountLeaderCard (String cardName,int victoryPoints, Requirements requirements, ResourceType resourceType, int discount) {
        super(cardName,victoryPoints, requirements);
        this.discountResourceType = resourceType;
        this.discount = discount;

    }

    /**
     * Initializes a new DiscountLeaderCardObject.
     * Should be called by Serializer class only.
     * @param victoryPoints the victory points associated with the card
     * @param requirements the requirements the player has to satisfy if they want to activate the card
     * @param resourceType the Resource the card discounts you a unit of
     * @param discount the number of units the card lets you discount
     * @param active manually sets the status of the card
     */
    public DiscountLeaderCard (String cardName,int victoryPoints, Requirements requirements, ResourceType resourceType, int discount, boolean active) {
        super(cardName,victoryPoints, requirements, active);
        this.discountResourceType = resourceType;
        this.discount = discount;
    }

    /**
     * Gets the requirements and returns the associated requirements applying its discount on top of it
     * @param requirements the requirements on which the discount has to be applied
     * @return the discounted requirements
     */
    @Override
    public Requirements recalculateRequirements(Requirements requirements) {
        requirements = super.recalculateRequirements(requirements);
        if(isActive())
            requirements.removeResourceRequirement(discountResourceType,discount);
        return requirements;
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "DiscountLeaderCard: " +
                (active ? "active" : "inactive") +
                ", " + discount + " " + discountResourceType + " discount" +
                ", requirements=" + requirements;
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
        if (!(other instanceof DiscountLeaderCard))
            return false;
        DiscountLeaderCard that = (DiscountLeaderCard) other;
        return this.discount == that.discount && this.discountResourceType.equals(that.discountResourceType) && this.requirements.equals(that.requirements)
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
