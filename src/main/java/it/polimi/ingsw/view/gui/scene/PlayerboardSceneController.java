package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.DevelopmentCardSlot;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class PlayerboardSceneController extends Controller{

    @FXML
    protected ImageView leadercard0;

    @FXML
    protected ImageView leadercard1;

    @FXML
    protected ImageView developmentcardslot0_0;

    @FXML
    protected ImageView developmentcardslot0_1;

    @FXML
    protected ImageView developmentcardslot0_2;

    @FXML
    protected ImageView developmentcardslot1_0;

    @FXML
    protected ImageView developmentcardslot1_1;

    @FXML
    protected ImageView developmentcardslot1_2;

    @FXML
    protected ImageView developmentcardslot2_0;

    @FXML
    protected ImageView developmentcardslot2_1;

    @FXML
    protected ImageView developmentcardslot2_2;

    @FXML
    protected Button marketBtn;

    @FXML
    protected Button startProductionBtn;

    @FXML
    protected Button buyDevelopmentcardBtn;

    private PlayerBoard playerBoard;

    @FXML
    public void initialize(PlayerBoard playerBoard){
        this.playerBoard = playerBoard;

        this.leadercard0.setImage(new Image("/images/cards/FRONT/"+playerBoard.getLeaderCards().get(0).getCardName()+".png"));
        this.leadercard1.setImage(new Image("/images/cards/FRONT/"+playerBoard.getLeaderCards().get(1).getCardName()+".png"));

        leadercard0.setOnMouseClicked((e)-> leaderCardClicked(0));
        leadercard1.setOnMouseClicked((e)-> leaderCardClicked(1));

        ImageView[][] slots =
                {{developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2},
                        {developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2},
                        {developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2}};

        int slotIndex = 0,cardIndex;

        for(DevelopmentCardSlot slot : playerBoard.getDevelopmentCardSlots()){
            cardIndex = 0;
            for(Card card : slot){
                slots[slotIndex][cardIndex].setImage(new Image("/images/cards/FRONT/"+card.getCardName()+".png"));
                slots[slotIndex][cardIndex++].setVisible(true);
            }
            slotIndex++;
        }
    }

    private void leaderCardClicked(int cardIndex){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Choose action");

        Pane pane = new Pane();
        VBox vbox = new VBox();

        Button activate = new Button("Activate");
        activate.setOnAction((e)->{
            dialog.setResult("activate");
            dialog.close();
        });
        vbox.getChildren().add(activate);

        Button discard = new Button("Discard");
        discard.setOnAction((e)->{
            dialog.setResult("discard");
            dialog.close();
        });
        vbox.getChildren().add(discard);

        pane.getChildren().add(vbox);
        dialog.getDialogPane().setContent(pane);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
        closeButton.managedProperty().bind(closeButton.visibleProperty());

        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()){
            if(result.get().equals("activate")){

            }else if(result.get().equals("discard")){

            }
        }
    }

    @FXML
    public void goToMarket() {
    }

    @FXML
    public void startProduction() {
    }

    @FXML
    public void buyDevelopmentCard() {
    }
}
