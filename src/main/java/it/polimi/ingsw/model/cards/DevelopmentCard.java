package it.polimi.ingsw.model.cards;

import it.polimi.ingsw.model.Producer;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.utils.Pair;

public class DevelopmentCard extends Card implements Producer {

    public static final int MAX_LEVEL = 3;

    /**
     * The requirements to be met in order to buy this card
     */
    private Requirements cost;
    /**
     * The level of this card
     */
    private int level;
    /**
     * The color of this card
     */
    private DevelopmentColorType color;

    /**
     * The amount of resources to be paid in order to start the production
     */
    private Requirements productionCost;
    /**
     * The amount of resources gained from the production
     */
    private Requirements productionGain;

    /**
     * Initializes a new DevelopmentCard
     * @param victoryPoints the victory points associated with the card
     * @param cost the cost the player has to pay if they want to buy the card
     * @param level the level of the card
     * @param color the color of the card
     * @param productionCost the cost to pay so as to activate the production
     * @param productionGain what the card returns when production is activated
     */
    public DevelopmentCard(int victoryPoints, Requirements cost, int level, DevelopmentColorType color, Requirements productionCost, Pair<Resource,Integer> ... productionGain) {
        super(victoryPoints);
        this.cost = cost;
        this.level = level;
        this.color = color;
        this.productionCost = productionCost;
        this.productionGain = new Requirements();
        for(Pair<Resource,Integer> x : productionGain)
            this.productionGain.addResourceRequirement(x.fst,x.snd);

    }

    /**
     * Returns the amount of resources to be paid in order to start the production
     * @return a Requirements object containin the amount of resources to be paid in order to start the production
     */
    public Requirements getCost() {
        return cost;
    }

    /**
     * Returns the level of this card
     * @return the level of this card
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the color of this card
     * @return a DevelopmentColorType representing the color of the card
     */
    public DevelopmentColorType getColor() {
        return color;
    }

    /**
     * Returns the amount of resources to be paid in order to start the production
     * @return a Requirements object containing the amount of resources to be paid in order to start the production
     */
    public Requirements getProductionCost() {
        return productionCost;
    }

    /**
     * Returns the amount of resources gained from the production
     * @return a Requirements object containing the amount of resources gained from the production
     */
    public Requirements getProductionGain() {
        return productionGain;
    }

    /**
     * Activates the card production
     */
    @Override
    public void produce() {

    }

    /**
     * Indicates whether some other object is equal to this one
     * @param other that is confronted
     * @return true if o is equal to the object, false elsewhere
     */
    @Override
    public boolean equals(Object other){
        if (this == other) return true;
        if (!(other instanceof DevelopmentCard))
            return false;
        DevelopmentCard o = (DevelopmentCard) other;
        return this.color.equals(o.color) && this.cost.equals(o.cost) && this.productionCost.equals(o.productionCost)
                && this.productionGain.equals(o.productionGain) && this.level == o.level;
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "DevelopmentCard: " +
                color +
                " lv." + level +
                ", cost=" + cost +
                ", productionCost=" + productionCost +
                ", productionGain=" + productionGain +
                '}';
    }
}
