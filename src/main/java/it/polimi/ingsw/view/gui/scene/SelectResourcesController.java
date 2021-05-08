package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.storage.Resource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class SelectResourcesController extends Controller{

    @FXML
    protected ImageView imgcoin;

    @FXML
    protected ImageView imgservant;

    @FXML
    protected ImageView imgshield;

    @FXML
    protected ImageView imgstone;

    @FXML
    protected Label lbl;

    @FXML
    protected Button btnChoose;

    private int resourcesToChoose;

    private List<Boolean> resourcesSelected = new ArrayList<>();

    private int numResourcesSelected = 0;

    private Resource[] resources;

    @FXML
    public void initialize(Resource[] values, int resourcesToChoose) {
        this.resourcesToChoose = resourcesToChoose;
        this.resources = values;
        lbl.setText("Choose "+resourcesToChoose+" resources:");
        ImageView[] imageViews = {imgcoin,imgservant,imgshield,imgstone};
        for(int i=0;i<values.length;i++){
            resourcesSelected.add(false);

            final int index = i;
            imageViews[index].setOnMouseClicked((e)->{
                if(resourcesSelected.get(index)){
                    imageViews[index].getStyleClass().remove("card-selected");
                    numResourcesSelected--;
                }else{
                    if (numResourcesSelected == resourcesToChoose)
                        return;
                    imageViews[index].getStyleClass().add("card-selected");
                    numResourcesSelected++;
                }
                resourcesSelected.set(index,!resourcesSelected.get(index));
                if(numResourcesSelected == resourcesToChoose){
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
        if(numResourcesSelected != resourcesToChoose)
            return;
        Resource[] resourcesChosen = new Resource[resourcesToChoose];
        int added =0;
        for(int i=0;i<resources.length;i++){
            if(resourcesSelected.get(i))
                resourcesChosen[added++] = resources[i];
        }
        clientController.chooseStartResources(resourcesChosen);

    }
}
