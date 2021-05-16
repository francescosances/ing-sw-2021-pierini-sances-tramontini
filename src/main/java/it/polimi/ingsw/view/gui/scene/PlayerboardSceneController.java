package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.Card;
import it.polimi.ingsw.model.cards.DevelopmentCardSlot;
import it.polimi.ingsw.model.storage.Depot;
import it.polimi.ingsw.utils.Triple;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @FXML
    protected ImageView warehouse0;

    @FXML
    protected ImageView warehouse1;

    @FXML
    protected ImageView warehouse2;

    @FXML
    protected ImageView warehouse3;

    @FXML
    protected ImageView warehouse4;

    @FXML
    protected ImageView warehouse5;

    @FXML
    protected ImageView strongbox0;

    @FXML
    protected ImageView strongbox1;

    @FXML
    protected ImageView strongbox2;

    @FXML
    protected ImageView strongbox3;

    @FXML
    protected ImageView vaticanreport0;

    @FXML
    protected ImageView vaticanreport1;

    @FXML
    protected ImageView vaticanreport2;

    @FXML
    protected ImageView marker;

    @FXML
    protected StackPane root;

    @FXML
    protected ChoiceBox<String> selectUser;

    private final double[][] FAITH_TRACK_CELLS = {
            {22,212},
            {64,212},
            {106,212},
            {106,170},
            {106,128},
            {148,128},
            {190,128},
            {232,128},
            {276,128},
            {318,128},
            {318,170},
            {318,212},
            {360,212},
            {402,212},
            {446,212},
            {488,212},
            {530,212},
            {530,170},
            {530,128},
            {572,128},
            {614,128},
            {658,128},
            {700,128},
            {742,128},
            {784,128}
    };

    private PlayerBoard playerBoard;

    @FXML
    public void initialize(PlayerBoard playerBoard){
        this.playerBoard = playerBoard;

        marker.setX(FAITH_TRACK_CELLS[playerBoard.getFaithTrack().getFaithMarker()][0]);
        marker.setY(FAITH_TRACK_CELLS[playerBoard.getFaithTrack().getFaithMarker()][1]);

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

        ImageView[][] warehouse = {
                {warehouse0},
                {warehouse1,warehouse2},
                {warehouse3,warehouse4,warehouse5}
        };

        List<Depot> depots = playerBoard.getWarehouse().getDepots();
        for(int i=0;i<depots.size();i++){
            int j;
            for(j=0;j<depots.get(i).getOccupied();j++){
                warehouse[i][j].setImage(new Image("/images/resources/"+depots.get(i).getResourceType().toString()+".png"));
                warehouse[i][j].setVisible(true);
            }
            for(int k=j;k<depots.get(i).getSize();k++){
                warehouse[i][k].setVisible(false);
            }
        }

        ImageView[] popeFavorTiles = {vaticanreport0,vaticanreport1,vaticanreport2};
        for(int i=0;i<3;i++){
            if(playerBoard.getFaithTrack().getPopeFavorTiles()[i].isUncovered())
                popeFavorTiles[i].setVisible(true);
        }

        List<String> players = clientController.getPlayers();
        selectUser.setItems(FXCollections.observableArrayList(players));
        selectUser.setValue(playerBoard.getUsername());

        selectUser.getSelectionModel().selectedIndexProperty().addListener((observableValue, value, index) -> {
            int intIndex = (Integer) index;
            if(intIndex < 0)return;
            String username = selectUser.getItems().get(intIndex);
            if(!username.equals(clientController.getUsername())) {
                clientController.showPlayerBoard(selectUser.getItems().get(intIndex));

            }
            System.out.println(selectUser.getItems().get(intIndex));
        });
    }

    private void leaderCardClicked(int cardIndex){
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Choose action");

        Pane pane = new Pane();

        pane.setMinWidth(200);

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

        Optional result = dialog.showAndWait();
        if(result.isPresent()){
            if(result.get().equals("activate")){
                clientController.activateLeaderCard(cardIndex);
            }else if(result.get().equals("discard")){
                clientController.discardLeaderCard(cardIndex);
            }
        }
    }

    protected void disableControls(){
        marketBtn.setDisable(true);
        buyDevelopmentcardBtn.setDisable(true);
        startProductionBtn.setDisable(true);
        leadercard0.setDisable(true);
        leadercard1.setDisable(true);
    }

    @FXML
    public void goToMarket() {
        clientController.performAction(Action.TAKE_RESOURCES_FROM_MARKET);
    }

    @FXML
    public void startProduction() {
    }

    @FXML
    public void buyDevelopmentCard() {
    }

}
