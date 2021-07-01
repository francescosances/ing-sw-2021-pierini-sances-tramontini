package it.polimi.ingsw.view.gui.scene;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginSceneController extends SceneController {

    @FXML
    protected TextField txtUsername;

    @FXML
    public void login(){
        if(txtUsername.getText().isEmpty())
            return;
        clientController.login(txtUsername.getText());
    }

}
