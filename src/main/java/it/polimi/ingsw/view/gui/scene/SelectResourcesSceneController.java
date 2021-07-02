package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.view.gui.GUI;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class SelectResourcesSceneController extends SceneController {

    /**
     * The imageViews of the shown resources
     */
    @FXML
    protected ImageView imgcoin, imgservant, imgshield,imgstone;

    /**
     * The label used as description to tell the user what he has to do
     */
    @FXML
    protected Label lbl;

    /**
     * The button used to confirm the selection
     */
    @FXML
    protected Button btnChoose;

    /**
     * The buttons used to modify the amount of the resource
     */
    @FXML
    protected Button coin_minus,coin_plus,servant_minus,servant_plus,shield_minus,shield_plus,stone_minus,stone_plus;

    /**
     * The input text used to input the amount of the resource
     */
    @FXML
    protected TextField coin_input,servant_input,shield_input,stone_input;

    /**
     * The number of resources to choose
     */
    private int resourcesToChoose;

    /**
     * The currently selected resources
     */
    private int numResourcesSelected = 0;

    /**
     * The action to be executed when the resource has been choosen
     */
    private ResourcesChooser onAction;

    /**
     * The type of resource to be chosen
     */
    String type;

    @FXML
    public void initialize(int resourcesToChoose,ResourcesChooser onAction, String type) {

        if(resourcesToChoose==0){
            onAction.choose(new Resource[0]);
            return;
        }

        this.type = type;

        Button[] minusButtons = {coin_minus,servant_minus,shield_minus,stone_minus};
        TextField[] textFields = {coin_input,servant_input,shield_input,stone_input};
        Button[] plusButtons = {coin_plus,servant_plus,shield_plus,stone_plus};
        lbl.setText("Choose " + resourcesToChoose + " resources to " + type + ':');

        numResourcesSelected = 0;
        this.resourcesToChoose = resourcesToChoose;

        this.onAction = onAction;

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
    }

    @FXML
    public void choose(){
        if(numResourcesSelected != resourcesToChoose)
            return;

        Resource[] resourcesChosen = new Resource[resourcesToChoose];

        int coin    = Integer.parseInt(coin_input.getText());
        int servant = Integer.parseInt(servant_input.getText());
        int shield  = Integer.parseInt(shield_input.getText());
        int stone   = Integer.parseInt(stone_input.getText());

        int added = 0;
        for(int i=0;i<coin;i++)
            resourcesChosen[added++] = ResourceType.COIN;

        for(int i=0;i<servant;i++)
            resourcesChosen[added++] = ResourceType.SERVANT;

        for(int i=0;i<shield;i++)
            resourcesChosen[added++] = ResourceType.SHIELD;

        for(int i=0;i<stone;i++)
            resourcesChosen[added++] = ResourceType.STONE;

        onAction.choose(resourcesChosen);

        if(!type.equals(GUI.START)) {
            Stage stage = (Stage) btnChoose.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Interface used to perform a custom action after the user has chosen the resources
     */
    public interface ResourcesChooser{

        void choose(Resource[] resourcesChosen);

    }
}
