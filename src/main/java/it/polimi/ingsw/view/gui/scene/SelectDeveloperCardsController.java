package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DiscountLeaderCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.ResourceType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class SelectDeveloperCardsController extends Controller{

    @FXML
    protected AnchorPane rootPane;

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

    private void showDevelopmentCard(DevelopmentCard developmentCard, ImageView imageView, ResourceType[] discounts){
        imageView.setImage(new Image("/images/cards/FRONT/"+developmentCard.getCardName()+".png"));

        if(discounts.length > 0) {
            ImageView discount1 = new ImageView(new Image("/images/buttons/"+discounts[0].toString()+"_discount.png"));
            discount1.setPreserveRatio(true);
            discount1.setFitHeight(106);
            discount1.setFitWidth(101);
            discount1.getStyleClass().add("discount");
            discount1.setX(imageView.getLayoutX() + 45);
            discount1.setY(imageView.getLayoutY() - 44);

            rootPane.getChildren().add(discount1);

            if(discounts.length > 1) {

                ImageView discount2 = new ImageView(new Image("/images/buttons/"+discounts[1].toString()+"_discount.png"));
                discount2.setPreserveRatio(true);
                discount2.setFitHeight(106);
                discount2.setFitWidth(101);
                discount2.getStyleClass().add("discount");
                discount2.setX(discount1.getX() + 40);
                discount2.setY(imageView.getLayoutY() - 44);

                rootPane.getChildren().add(discount2);
            }
        }
    }

    @FXML
    public void initialize(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard playerBoard){
        this.cardsToChoose = cardsToChoose;

        ImageView[][] imgs = {
                {card8_0,card8_1,card8_2},{card4_0,card4_1,card4_2},{card0_0,card0_1,card0_2},
                {card9_0,card9_1,card9_2},{card5_0,card5_1,card5_2},{card1_0,card1_1,card1_2},
                {card10_0,card10_1,card10_2},{card6_0,card6_1,card6_2},{card2_0,card2_1,card2_2},
                {card11_0,card11_1,card11_2},{card7_0,card7_1,card7_2},{card3_0,card3_1,card3_2}
                };

        for(int i=0;i<developmentCardList.size();i++){
            if(developmentCardList.get(i).isEmpty()){
                imgs[i][0].setVisible(false);
                imgs[i][1].setVisible(false);
                imgs[i][2].setVisible(false);
                continue;
            }

            DevelopmentCard topCard = developmentCardList.get(i).top();

            showDevelopmentCard(topCard,imgs[i][0],playerBoard.getLeaderCards().stream().filter(card->card.isActive() && card.isDiscountLeaderCard()).map(card -> (DiscountLeaderCard)card).map(DiscountLeaderCard::getDiscountResourceType).filter(discountResourceType -> topCard.getCost().getResourcesMap().containsKey(discountResourceType)).toArray(ResourceType[]::new));

            if(developmentCardList.get(i).size() > 1)
                showDevelopmentCard(developmentCardList.get(i).get(1),imgs[i][1],new ResourceType[0]);
            else
                imgs[i][1].setVisible(false);

            if(developmentCardList.get(i).size() > 2)
                showDevelopmentCard(developmentCardList.get(i).get(2),imgs[i][2],new ResourceType[0]);
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
