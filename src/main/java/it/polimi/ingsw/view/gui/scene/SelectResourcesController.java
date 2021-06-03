package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.cards.Requirements;
import it.polimi.ingsw.model.storage.Resource;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class SelectResourcesController extends Controller{

    @FXML
    protected ImageView imgcoin, imgservant, imgshield,imgstone;

    @FXML
    protected Label lbl;

    @FXML
    protected Button btnChoose;

    @FXML
    protected Button coin_minus,coin_plus,servant_minus,servant_plus,shield_minus,shield_plus,stone_minus,stone_plus;

    @FXML
    protected TextField coin_input,servant_input,shield_input,stone_input;

    private int resourcesToChoose;

    private List<Boolean> resourcesSelected = new ArrayList<>();

    private int numResourcesSelected = 0;

    private Resource[] resources;


    @FXML
    public void initialize(Resource[] values, int resourcesToChoose) {

        Button[] minusButtons = {coin_minus,servant_minus,shield_minus,stone_minus};
        TextField[] textFields = {coin_input,servant_input,shield_input,stone_input};
        Button[] plusButtons = {coin_plus,servant_plus,shield_plus,stone_plus};

        numResourcesSelected = 0;

        for(int i=0;i<minusButtons.length;i++){
            final int index = i;
            minusButtons[i].setOnMouseClicked((e)->{
                String value = textFields[index].getText();
                int intValue = Integer.parseInt(value);
                int newValue = intValue-1;
                if(newValue < 0)
                    newValue = 0;
                else {
                    numResourcesSelected--;
                    if(numResourcesSelected < resourcesToChoose){
                        btnChoose.getStyleClass().add("btn-disabled");
                        btnChoose.getStyleClass().remove("btn-active");
                    }
                }
                textFields[index].setText(String.valueOf(newValue));
            });
            plusButtons[i].setOnMouseClicked((e)->{
                String value = textFields[index].getText();
                int intValue = Integer.parseInt(value);
                int newValue = intValue+1;
                if(numResourcesSelected+1 > resourcesToChoose)
                    newValue = intValue;
                else {
                    numResourcesSelected++;
                    if(numResourcesSelected == resourcesToChoose){
                        btnChoose.getStyleClass().remove("btn-disabled");
                        btnChoose.getStyleClass().add("btn-active");
                    }
                }
                textFields[index].setText(String.valueOf(newValue));
            });
        }

/*
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
        }*/
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
