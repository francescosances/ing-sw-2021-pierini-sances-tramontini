package it.polimi.ingsw.view.gui.scene;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ServerSetupSceneController{

    @FXML
    private TextField txtSrvAddress;

    @FXML
    private TextField txtSrvPort;

    @FXML
    public void initialize() {

    }

    @FXML
    public void connect(){
        System.out.println("Mi connetto a ");
        System.out.println(txtSrvAddress.getText());
        System.out.println(txtSrvPort.getText());
    }


}