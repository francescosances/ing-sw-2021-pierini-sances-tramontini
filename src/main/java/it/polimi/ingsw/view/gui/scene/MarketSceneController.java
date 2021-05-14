package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.Market;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MarketSceneController extends Controller{

    @FXML
    protected ImageView slide;

    @FXML
    protected ImageView marble0;

    @FXML
    protected ImageView marble1;

    @FXML
    protected ImageView marble2;

    @FXML
    protected ImageView marble3;

    @FXML
    protected ImageView marble4;

    @FXML
    protected ImageView marble5;

    @FXML
    protected ImageView marble6;

    @FXML
    protected ImageView marble7;

    @FXML
    protected ImageView marble8;

    @FXML
    protected ImageView marble9;

    @FXML
    protected ImageView marble10;

    @FXML
    protected ImageView marble11;

    @FXML
    public void initialize(Market market){

        ImageView[][] tray = {
                {marble0,marble1,marble2,marble3},
                {marble4,marble5,marble6,marble7},
                {marble8,marble9,marble10,marble11}
        };

        for(int r=0;r<Market.ROWS;r++){
            for(int c=0;c<Market.COLUMNS;c++){
                tray[r][c].setImage(new Image("/images/marbles/"+market.getMarble(r,c).toString()+".png"));
            }
        }

        slide.setImage(new Image("/images/marbles/"+market.getSlideMarble().toString()+".png"));

    }


}
