package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.Producer;
import it.polimi.ingsw.model.cards.Requirements;

import java.util.List;

public interface ProductionChooser {

    void chooseResource(List<Producer> producers, Requirements requirements);

}
