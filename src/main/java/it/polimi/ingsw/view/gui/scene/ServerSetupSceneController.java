package it.polimi.ingsw.view.gui.scene;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

public class ServerSetupSceneController extends Controller{

    @FXML
    private TextField txtSrvAddress;

    @FXML
    private TextField txtSrvPort;

    @FXML
    public void initialize() {

    }

    @FXML
    public void connect(){
        try {
            String serverIP = (txtSrvAddress.getText() != null && !txtSrvAddress.getText().equals(""))?txtSrvAddress.getText():"127.0.0.1";
            int serverPort = Integer.parseInt((txtSrvPort.getText() != null && !txtSrvPort.getText().equals(""))?txtSrvPort.getText():"8000");
            clientController.connect(serverIP,serverPort);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING, "An error has occurred. Try again");
            alert.show();
        }
    }


}