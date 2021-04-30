package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.Resource;

import java.util.Map;

public interface Producer {
    /**
     * Returns the amount of resources to be paid in order to start the production
     * @return a Requirements object containing the amount of resources to be paid in order to start the production
     */
    Requirements getProductionCost();

    /**
     * Returns the amount of resources gained from the production
     * @return a Requirements object containing the amount of resources gained from the production
     */
    Requirements getProductionGain();

}
