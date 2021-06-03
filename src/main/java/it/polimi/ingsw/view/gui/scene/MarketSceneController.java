package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.Market;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MarketSceneController extends Controller{

    @FXML
    protected ImageView slide;

    @FXML
    protected ImageView marble0,marble1,marble2,marble3,marble4,marble5,marble6,marble7,marble8,marble9,marble10,marble11;

    @FXML
    protected ImageView row0,row1,row2;

    @FXML
    protected ImageView column0,column1,column2,column3;

    @FXML
    public void initialize(Market market,boolean controlsEnabled){

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

        ImageView[] columns = {column0,column1,column2,column3};
        ImageView[] rows = {row0,row1,row2};

        if(controlsEnabled) {
            for(int i=0;i<columns.length;i++){
                final int index = i;
                columns[i].setOnMouseClicked((e) -> columnClicked(index));
                columns[i].setVisible(true);
            }

            for(int i=0;i<rows.length;i++){
                final int index = i;
                rows[i].setOnMouseClicked((e) -> rowClicked(index));
                rows[i].setVisible(true);
            }
        }else{
            for (ImageView column : columns) {
                column.setVisible(false);
            }

            for (ImageView row : rows) {
                row.setVisible(false);
            }
        }
    }

    protected void columnClicked(int columnIndex){
        clientController.chooseMarketColumn(columnIndex);
        Stage stage = (Stage) row0.getScene().getWindow();
        stage.close();
    }

    protected void rowClicked(int rowIndex){
        clientController.chooseMarketRow(rowIndex);
        Stage stage = (Stage) row0.getScene().getWindow();
        stage.close();
    }


}
