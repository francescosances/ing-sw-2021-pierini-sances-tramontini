package it.polimi.ingsw.model;

import it.polimi.ingsw.model.storage.Resource;

import java.util.Map;

public interface Producer {
    /**
     * Returns the amount of resources to be paid in order to start the production
     * @return a Requirements object containing the amount of resources to be paid in order to start the production
     */
    Map<Resource, Integer> getProductionCost();

    /**
     * Returns the amount of resources gained from the production
     * @return a Requirements object containing the amount of resources gained from the production
     */
    Map<Resource, Integer> getProductionGain();
}
