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
    protected ImageView row0;

    @FXML
    protected ImageView row1;

    @FXML
    protected ImageView row2;

    @FXML
    protected ImageView column0;

    @FXML
    protected ImageView column1;

    @FXML
    protected ImageView column2;

    @FXML
    protected ImageView column3;

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

        column0.setOnMouseClicked((e)->columnClicked(0));
        column1.setOnMouseClicked((e)->columnClicked(1));
        column2.setOnMouseClicked((e)->columnClicked(2));
        column3.setOnMouseClicked((e)->columnClicked(3));

        row0.setOnMouseClicked((e)->rowClicked(0));
        row1.setOnMouseClicked((e)->rowClicked(1));
        row2.setOnMouseClicked((e)->rowClicked(2));
    }

    protected void columnClicked(int columnIndex){
        clientController.chooseMarketColumn(columnIndex);
    }

    protected void rowClicked(int rowIndex){
        clientController.chooseMarketRow(rowIndex);
    }


}
