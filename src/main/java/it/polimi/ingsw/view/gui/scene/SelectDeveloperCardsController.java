package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import java.util.List;

public class SelectDeveloperCardsController extends Controller{

    @FXML
    protected ImageView card0_0,card1_0,card2_0,card3_0,card4_0,card5_0,card6_0,card7_0,card8_0,card9_0,card10_0,card11_0;

    @FXML
    public void initialize(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard userBoard){

    }

}
