package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.storage.ResourceType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SelectWhiteMarbleConversionSceneController extends SceneController {

    @FXML
    protected ImageView imgcoin, imgservant, imgshield,imgstone;

    @FXML
    protected Button btnChoose;

    private int selectedResource = -1;

    @FXML
    public void initialize(ResourceType ... values) {
        ImageView[] imageViews = {imgcoin,imgservant,imgshield,imgstone};
        ResourceType[] resourceTypes = {ResourceType.COIN,ResourceType.SERVANT,ResourceType.SHIELD,ResourceType.STONE};

        imgcoin.setVisible(Arrays.stream(values).anyMatch(resourceType -> resourceType == ResourceType.COIN));
        imgservant.setVisible(Arrays.stream(values).anyMatch(resourceType -> resourceType == ResourceType.SERVANT));
        imgshield.setVisible(Arrays.stream(values).anyMatch(resourceType -> resourceType == ResourceType.SHIELD));
        imgstone.setVisible(Arrays.stream(values).anyMatch(resourceType -> resourceType == ResourceType.STONE));

        List<ImageView> activeImageViews = new ArrayList<>();
        for (ResourceType value : values) {
            for (int j = 0; j < resourceTypes.length; j++) {
                if (resourceTypes[j] == value)
                    activeImageViews.add(imageViews[j]);
            }
        }

        for(int i=0;i<values.length;i++){
            final int index = i;
            activeImageViews.get(index).setOnMouseClicked((e)->{
                if(selectedResource == index){
                    activeImageViews.get(index).getStyleClass().remove("card-selected");
                    selectedResource = -1;
                }else{
                    if (selectedResource != -1)
                        return;
                    activeImageViews.get(index).getStyleClass().add("card-selected");
                    selectedResource = index;
                }
                if(selectedResource != -1){
                    btnChoose.getStyleClass().remove("btn-disabled");
                    btnChoose.getStyleClass().add("btn-active");
                }else{
                    btnChoose.getStyleClass().add("btn-disabled");
                    btnChoose.getStyleClass().remove("btn-active");
                }
            });
        }
    }

    @FXML
    public void choose(){
        if(selectedResource == -1)
            return;
        clientController.chooseWhiteMarbleConversion(selectedResource);
        Stage stage = (Stage) btnChoose.getScene().getWindow();
        stage.close();
    }

}
