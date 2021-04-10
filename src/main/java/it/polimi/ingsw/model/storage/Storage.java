package it.polimi.ingsw.model.storage;

import it.polimi.ingsw.model.cards.Requirements;

public interface Storage {
    /**
     * Removes the specified resources from the storage (as long as there are) and returns the missing resources that couldn't be removed.
     * @param requirements the resources to be removed from the storage
     * @return the the missing resources that couldn't be removed
     */
    Requirements removeResources(Requirements requirements);

}
