package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.controller.ClientController;

public abstract class SceneController {

    protected ClientController clientController;

    public ClientController getClientController() {
        return clientController;
    }

    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }
}
