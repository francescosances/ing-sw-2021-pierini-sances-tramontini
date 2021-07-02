package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.cards.LeaderCard;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.List;

public class SelectLeaderCardsSceneController extends SceneController {

    /**
     * The imageView of the first leaderCard shown
     */
    @FXML
    protected ImageView img1;

    /**
     * The imageView of the second leaderCard shown
     */
    @FXML
    protected ImageView img2;

    /**
     * The imageView of the third leaderCard shown
     */
    @FXML
    protected ImageView img3;

    /**
     * The imageView of the fourth leaderCard shown
     */
    @FXML
    protected ImageView img4;

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
     * The list of leaderCards shown
     */
    private List<LeaderCard> leaderCardList;

    /**
     * The currently chosen cards
     */
    private List<Boolean> cardsSelected = new ArrayList<>();

    /**
     * The number of cards to choose
     */
    private int cardsToChoose;

    /**
     * The number of cards currentlySelected
     */
    private int numCardsSelected = 0;

    /**
     * Set the imageView references to the given leadercards
     * @param leaderCardList the leadercards to show
     * @param cardsToChoose the number of cards to be chosen
     */
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

    /**
     * Send to the clientController the chosen leaderCards
     */
    @FXML
    public void choose(){
        if(numCardsSelected != cardsToChoose)
            return;
        List<Integer> cardsChosen = new ArrayList<>();
        for(int i=0;i<leaderCardList.size();i++){
            if(cardsSelected.get(i))
                cardsChosen.add(i);
        }
        clientController.leaderCardsChoice(cardsChosen);

    }

}
