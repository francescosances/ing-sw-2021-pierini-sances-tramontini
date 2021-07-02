package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.serialization.Serializer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.net.UnknownHostException;

public class ServerSetupSceneController extends SceneController {

    /**
     * The TextField used to insert the server address
     */
    @FXML
    private TextField txtSrvAddress;

    /**
     * The TextField used to insert the server port
     */
    @FXML
    private TextField txtSrvPort;

    @FXML
    public void initialize() {

    }

    /**
     * Tell the client controller to connect to the server
     */
    @FXML
    public void connect(){
        try {
            String serverIP = (txtSrvAddress.getText() != null && !txtSrvAddress.getText().equals("")) ? txtSrvAddress.getText() : "127.0.0.1";
            int serverPort = Serializer.deserializeInt((txtSrvPort.getText() != null && !txtSrvPort.getText().equals("")) ? txtSrvPort.getText() : "8000");
            clientController.connect(serverIP, serverPort);
        }catch (UnknownHostException e){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Unable to connect");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.WARNING, "An error has occurred. Try again");
            alert.show();
        }
    }


}