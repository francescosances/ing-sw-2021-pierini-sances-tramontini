package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.controller.ClientController;

public abstract class SceneController {

    /**
     * The client controller associated to the scene
     */
    protected ClientController clientController;

    /**
     * Returns the client controller associated to the scene
     * @return the client controller associated to the scene
     */
    public ClientController getClientController() {
        return clientController;
    }

    /**
     * Set the client controller associated to the scene
     * @param clientController the client controller associated to the scene
     */
    public void setClientController(ClientController clientController) {
        this.clientController = clientController;
    }
}
