package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.network.ClientSocket;
import it.polimi.ingsw.view.gui.scene.Controller;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaFXGui extends Application {

    @Override
    public void start(Stage stage) {

        ClientController clientController = new ClientController();

        clientController.startGui(stage);

        stage.setTitle("Masters of Renaissance");
        stage.setResizable(false);
        stage.show();
    }

    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    public static Scene loadScene(String fileName, ClientController clientController){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(JavaFXGui.class.getResource("/fxml/"+fileName+".fxml"));
        Parent rootLayout = null;
        try {
            rootLayout = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Scene scene = new Scene(rootLayout);

        Controller controller = loader.getController();
        controller.setClientController(clientController);

        return scene;
    }
}