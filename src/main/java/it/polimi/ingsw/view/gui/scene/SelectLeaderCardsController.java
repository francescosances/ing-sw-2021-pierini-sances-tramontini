package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.cards.LeaderCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
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

    private List<LeaderCard> leaderCardList;

    private List<Boolean> cardsSelected = new ArrayList<>();

    private int cardsToChoose;

    private int numCardsSelected = 0;

    @FXML
    public void initialize(List<LeaderCard> leaderCardList, int cardsToChoose) {
        this.leaderCardList = leaderCardList;
        this.cardsToChoose = cardsToChoose;
        lbl.setText("Choose "+cardsToChoose+" cards:");
        ImageView[] imageViews = {img1,img2,img3,img4};
        for(int i=0;i<leaderCardList.size();i++){
            cardsSelected.add(false);
            imageViews[i].setImage(new Image("/images/cards/FRONT/"+leaderCardList.get(i).getCardName()+".png"));

            final int index = i;
            imageViews[index].setOnMouseClicked((e)->{
                if(cardsSelected.get(index)){
                    imageViews[index].getStyleClass().remove("card-selected");
                    numCardsSelected--;
                }else{
                    if(numCardsSelected == cardsToChoose)
                        return;
                    imageViews[index].getStyleClass().add("card-selected");
                    numCardsSelected++;
                }
                cardsSelected.set(index,!cardsSelected.get(index));
                if(numCardsSelected == cardsToChoose){
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
        if(numCardsSelected != cardsToChoose)
            return;
        LeaderCard[] cardsChosen = new LeaderCard[cardsToChoose];
        int added =0;
        for(int i=0;i<leaderCardList.size();i++){
            if(cardsSelected.get(i))
                cardsChosen[added++] = leaderCardList.get(i);
        }
        clientController.leaderCardsChoice(cardsChosen);
    }

}
