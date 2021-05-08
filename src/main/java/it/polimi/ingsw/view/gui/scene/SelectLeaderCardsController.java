package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.cards.LeaderCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.List;

public class SelectLeaderCardsController extends Controller{

    @FXML
    protected ImageView img1;

    @FXML
    protected ImageView img2;

    @FXML
    protected ImageView img3;

    @FXML
    protected ImageView img4;

    @FXML
    protected Label lbl;

    @FXML
    protected Button btnChoose;

    @FXML
    public void choose(){

    }

    private List<LeaderCard> leaderCardList;

    private List<Boolean> cardsStatus;

    private int cardsToChoose;

    @FXML
    public void initialize(List<LeaderCard> leaderCardList, int cardsToChoose) {
        this.leaderCardList = leaderCardList;
        this.cardsToChoose = cardsToChoose;
        lbl.setText("Choose "+cardsToChoose+" cards:");
        ImageView[] imageViews = {img1,img2,img3,img4};
        for(int i=0;i<leaderCardList.size();i++){
            imageViews[i].setImage(new Image("../images/cards/"+leaderCardList.get(i).getCardName()+".png"));
        }
    }



}
