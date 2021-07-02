package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.Producer;
import it.polimi.ingsw.model.cards.Requirements;

import java.util.List;

/**
 * Strategy pattern - This interface is needed to specify the action to perform when the user has chosen the producers
 */
public interface ProductionChooser {

    /**
     * Let the user choose the resources after he has chosen the producers
     * @param producers the producers chosen by the user
     * @param requirements the requirements to be satisfied
     */
    void chooseResource(List<Producer> producers, Requirements requirements);

}
