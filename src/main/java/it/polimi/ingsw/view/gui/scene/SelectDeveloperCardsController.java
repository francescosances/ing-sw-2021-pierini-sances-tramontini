package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.exceptions.IncompatibleDepotException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SelectDeveloperCardsController extends Controller{

    @FXML
    protected ImageView card0_0,card1_0,card2_0,card3_0,card4_0,card5_0,card6_0,card7_0,card8_0,card9_0,card10_0,card11_0;

    @FXML
    protected ImageView card0_1,card1_1,card2_1,card3_1,card4_1,card5_1,card6_1,card7_1,card8_1,card9_1,card10_1,card11_1;

    @FXML
    protected ImageView card0_2,card1_2,card2_2,card3_2,card4_2,card5_2,card6_2,card7_2,card8_2,card9_2,card10_2,card11_2;

    private int cardsToChoose;

    @FXML
    private Button btnChoose;

    private List<DevelopmentCard> chosenCards = new ArrayList<>();


    @FXML
    public void initialize(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard playerBoard){
        this.cardsToChoose = cardsToChoose;

        try {
            playerBoard.getWarehouse().getDepots().get(2).addResource(ResourceType.COIN);
            playerBoard.getWarehouse().getDepots().get(2).addResource(ResourceType.COIN);
            playerBoard.getWarehouse().getDepots().get(2).addResource(ResourceType.COIN);
            playerBoard.getWarehouse().getDepots().get(1).addResource(ResourceType.SERVANT);
            playerBoard.getWarehouse().getDepots().get(1).addResource(ResourceType.SERVANT);
            playerBoard.getWarehouse().getDepots().get(0).addResource(ResourceType.SHIELD);
        } catch (IncompatibleDepotException e) {
            e.printStackTrace();
        }

        ImageView[][] imgs = {{card0_0,card0_1,card0_2},{card1_0,card1_1,card1_2},{card2_0,card2_1,card2_2},{card3_0,card3_1,card3_2},{card4_0,card4_1,card4_2},{card5_0,card5_1,card5_2},{card6_0,card6_1,card6_2},{card7_0,card7_1,card7_2},{card8_0,card8_1,card8_2},{card9_0,card9_1,card9_2},{card10_0,card10_1,card10_2},{card11_0,card11_1,card11_2}};

        for(int i=0;i<developmentCardList.size();i++){
            if(developmentCardList.get(i).isEmpty()){
                imgs[i][0].setVisible(false);
                imgs[i][1].setVisible(false);
                imgs[i][2].setVisible(false);
                continue;
            }

            DevelopmentCard topCard = developmentCardList.get(i).top();
            imgs[i][0].setImage(new Image("/images/cards/FRONT/"+topCard.getCardName()+".png"));

            if(developmentCardList.get(i).size() > 1)
                imgs[i][1].setImage(new Image("/images/cards/FRONT/"+developmentCardList.get(i).get(1).getCardName()+".png"));
            else
                imgs[i][1].setVisible(false);

            if(developmentCardList.get(i).size() > 2)
                imgs[i][2].setImage(new Image("/images/cards/FRONT/"+developmentCardList.get(i).get(2).getCardName()+".png"));
            else
                imgs[i][2].setVisible(false);

            if(playerBoard.acceptsDevelopmentCard(topCard) && topCard.getCost().satisfied(playerBoard)) {
                imgs[i][0].getStyleClass().add("selectable");
                final int index = i;
                imgs[i][0].setOnMouseClicked((e)->{
                    if(chosenCards.contains(topCard)){
                        imgs[index][0].getStyleClass().remove("card-selected");
                        chosenCards.remove(topCard);
                    }else{
                        if(chosenCards.size() == cardsToChoose)
                            return;
                        imgs[index][0].getStyleClass().add("card-selected");
                        chosenCards.add(topCard);
                    }
                    if(chosenCards.size() == cardsToChoose){
                        btnChoose.getStyleClass().remove("btn-disabled");
                        btnChoose.getStyleClass().add("btn-active");
                    }else{
                        btnChoose.getStyleClass().add("btn-disabled");
                        btnChoose.getStyleClass().remove("btn-active");
                    }
                });
            }else
                imgs[i][0].getStyleClass().remove("selectable");
        }
    }

    @FXML
    public void choose(){
        if(cardsToChoose != chosenCards.size())
            return;
        clientController.chooseDevelopmentCards(chosenCards.toArray(new DevelopmentCard[0]));
        Stage stage = (Stage) btnChoose.getScene().getWindow();
        stage.close();
    }

}
