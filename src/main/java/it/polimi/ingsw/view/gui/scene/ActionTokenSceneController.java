package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.ActionToken;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ActionTokenSceneController extends Controller{

    @FXML
    protected ImageView actionTokenImg;

    @FXML
    public void initialize(ActionToken actionToken){
        String actionTokenName = "";
        if(actionToken.getBlackCrossSpaces() == null)
            actionTokenName = actionToken.getDevelopmentCard().toString();
        else
            actionTokenName = actionToken.getBlackCrossSpaces().toString();

        actionTokenImg.setImage(new Image("/images/punchboard/action_token_"+actionTokenName+".png"));
    }

}
