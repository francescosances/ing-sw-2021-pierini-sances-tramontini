package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.view.gui.scene.SceneController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class JavaFXGui extends Application {

    /**
     * Starts the Scene whit the given stage
     * @param stage the stage to be shown
     */
    @Override
    public void start(Stage stage) {

        ClientController clientController = new ClientController();

        clientController.startGui(stage);

        stage.setTitle("Masters of Renaissance");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Terminates the execution of the application
     */
    @Override
    public void stop() {
        Platform.exit();
        System.exit(0);
    }

    /**
     * Load the fxml scene's file from the resources folder
     * @param fileName the name of the fxml file
     * @param clientController the client controller of the match
     * @return a pair containing the created scene and the relative scene controller
     */
    public static Pair<Scene, SceneController> loadScene(String fileName, ClientController clientController){
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

        SceneController sceneController = loader.getController();
        sceneController.setClientController(clientController);

        return new Pair<>(scene, sceneController);
    }
}