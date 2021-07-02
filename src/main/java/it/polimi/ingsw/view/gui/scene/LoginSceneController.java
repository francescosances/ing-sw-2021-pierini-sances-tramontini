package it.polimi.ingsw.view.gui.scene;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginSceneController extends SceneController {

    /**
     * The textfield that let the user prompts his username
     */
    @FXML
    protected TextField txtUsername;

    /**
     * Perform the login with the prompted username
     */
    @FXML
    public void login(){
        if(txtUsername.getText().isEmpty())
            return;
        clientController.login(txtUsername.getText());
    }

}
