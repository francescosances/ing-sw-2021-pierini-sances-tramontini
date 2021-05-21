package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerboardSceneController extends Controller{

    @FXML
    protected ImageView leadercard0,leadercard1;

    @FXML
    protected ImageView leaderCardDepot00,leaderCardDepot01,leaderCardDepot10,leaderCardDepot11;

    @FXML
    protected ImageView developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2,developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2,developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2;

    @FXML
    protected Button marketBtn,startProductionBtn,buyDevelopmentcardBtn;

    @FXML
    protected ImageView warehouse0,warehouse1,warehouse2,warehouse3,warehouse4,warehouse5;

    @FXML
    protected ImageView strongbox0,strongbox1,strongbox2,strongbox3;

    @FXML
    protected ImageView vaticanreport0,vaticanreport1,vaticanreport2;

    @FXML
    protected ImageView resources_supply,resources_supply_0,resources_supply_1,resources_supply_2,resources_supply_3;

    protected List<Resource> resourcesToStore;

    @FXML
    protected ImageView marker;

    @FXML
    protected StackPane root;

    @FXML
    protected Region warehouseRow0,warehouseRow1,warehouseRow2;

    @FXML
    protected ChoiceBox<String> selectUser;

    @FXML
    protected ImageView baseProductionBtn;

    protected boolean controlsEnabled = false;

    private boolean choosingProductions = false;

    protected Integer selectedWarehouseRow = null;

    private final static double[][] FAITH_TRACK_CELLS = {{22,212}, {64,212}, {106,212}, {106,170}, {106,128}, {148,128}, {190,128}, {232,128}, {276,128}, {318,128}, {318,170}, {318,212}, {360,212}, {402,212}, {446,212}, {488,212}, {530,212}, {530,170}, {530,128}, {572,128}, {614,128}, {658,128}, {700,128}, {742,128}, {784,128}};

    private PlayerBoard playerBoard;

    private final ChangeListener<? super Number> changeUserListener = (observableValue, value, index) -> {
        int intIndex = (Integer) index;
        if(intIndex < 0)return;
        String username = selectUser.getItems().get(intIndex);
        if(!username.equals(playerBoard.getUsername())) {
            clientController.showPlayerBoard(selectUser.getItems().get(intIndex));
            selectUser.setDisable(true);
            disableControls();
        }
    };

    @FXML
    public void initialize(PlayerBoard playerBoard){
        this.playerBoard = playerBoard;

        marker.setX(FAITH_TRACK_CELLS[playerBoard.getFaithTrack().getFaithMarker()][0]);
        marker.setY(FAITH_TRACK_CELLS[playerBoard.getFaithTrack().getFaithMarker()][1]);

        showLeaderCards(playerBoard.getLeaderCards());

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

        showWarehouse(playerBoard.getWarehouse());

        ImageView[] popeFavorTiles = {vaticanreport0,vaticanreport1,vaticanreport2};
        for(int i=0;i<3;i++){
            popeFavorTiles[i].setImage(new Image("/images/punchboard/pope_favor_tile_missed_"+i+".png"));
            popeFavorTiles[i].setVisible(true);
            if(playerBoard.getFaithTrack().getPopeFavorTiles()[i] == null){
                popeFavorTiles[i].setVisible(false);
            }else if(playerBoard.getFaithTrack().getPopeFavorTiles()[i].isUncovered()) {
                popeFavorTiles[i].setImage(new Image("/images/punchboard/pope_favor_tile" + i + ".png"));
                popeFavorTiles[i].setVisible(true);
            }
        }

        disableControls();
        //selectUser.setDisable(true);

        populateUserSelect();
    }

    public void populateUserSelect(){
        List<String> players = clientController.getPlayers();

        selectUser.getSelectionModel().selectedIndexProperty().removeListener(changeUserListener);

        System.out.println("players");
        System.out.println(players);

        selectUser.setItems(FXCollections.observableArrayList(players));
        selectUser.setValue((playerBoard.getUsername().equals(clientController.getUsername()))?Match.YOU_STRING: playerBoard.getUsername());

        selectUser.getSelectionModel().selectedIndexProperty().addListener(changeUserListener);
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

    public void disableControls(){
        controlsEnabled = false;
        marketBtn.setDisable(true);
        buyDevelopmentcardBtn.setDisable(true);
        startProductionBtn.setDisable(true);
        leadercard0.setDisable(true);
        leadercard1.setDisable(true);
    }

    public void enableControls(){
        if(playerBoard.getUsername().equals(clientController.getUsername())) {
            controlsEnabled = true;
            marketBtn.setDisable(false);
            buyDevelopmentcardBtn.setDisable(false);
            startProductionBtn.setDisable(false);
            leadercard0.setDisable(false);
            leadercard1.setDisable(false);
        }
        selectUser.setDisable(false);
    }

    @FXML
    public void goToMarket() {
        clientController.performAction(Action.TAKE_RESOURCES_FROM_MARKET);
    }

    @FXML
    public void startProduction() {
        disableControls();
        selectUser.setDisable(true);
        choosingProductions = true;
        clientController.performAction(Action.ACTIVATE_PRODUCTION);
    }

    @FXML
    public void buyDevelopmentCard() {
        clientController.performAction(Action.BUY_DEVELOPMENT_CARD);
    }

    public void showWarehouse(Warehouse warehouse){
        this.playerBoard.setWarehouse(warehouse);

        ImageView[][] imgWarehouse = {
                {warehouse0},
                {warehouse1,warehouse2},
                {warehouse3,warehouse4,warehouse5}
        };

        List<Depot> depots = warehouse.getDepots();
        for(int i=0;i<depots.size();i++){
            int j;
            for(j=0;j<depots.get(i).getOccupied();j++){
                imgWarehouse[i][j].setImage(new Image("/images/resources/"+depots.get(i).getResourceType().toString()+".png"));
                imgWarehouse[i][j].setVisible(true);
            }
            for(int k=j;k<depots.get(i).getSize();k++){
                imgWarehouse[i][k].setVisible(false);//TODO: debugger
            }
        }

        warehouseRow0.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && controlsEnabled) {
                warehouse0.getStyleClass().add("card-selected");
            } else if(selectedWarehouseRow == null || selectedWarehouseRow != 0){
                warehouse0.getStyleClass().remove("card-selected");
            }
        });
        warehouseRow0.setOnMouseClicked((e)->{
            if(!controlsEnabled)
                return;
            if(selectedWarehouseRow == null){
                selectedWarehouseRow = 0;
                warehouse0.getStyleClass().add("card-selected");
            }else{
                if (selectedWarehouseRow == 0) {
                    selectedWarehouseRow = null;
                    warehouse0.getStyleClass().remove("card-selected");
                }else{
                    System.out.println("SWAPPO "+selectedWarehouseRow+" e 0");
                    clientController.swapDepots(selectedWarehouseRow,0);
                    clearWarehouseSelection();
                    //TODO: aggiornare la vista
                }
            }
        });
//TODO: impedire swap depositi altrui
        warehouseRow1.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && controlsEnabled) {
                warehouse1.getStyleClass().add("card-selected");
                warehouse2.getStyleClass().add("card-selected");
            } else if(selectedWarehouseRow == null || selectedWarehouseRow != 1){
                warehouse1.getStyleClass().remove("card-selected");
                warehouse2.getStyleClass().remove("card-selected");
            }
        });
        warehouseRow1.setOnMouseClicked((e)->{
            if(!controlsEnabled)
                return;
            if(selectedWarehouseRow == null){
                selectedWarehouseRow = 1;
                warehouse1.getStyleClass().add("card-selected");
                warehouse2.getStyleClass().add("card-selected");
            }else{
                if (selectedWarehouseRow == 1) {
                    selectedWarehouseRow = null;
                    warehouse1.getStyleClass().remove("card-selected");
                    warehouse2.getStyleClass().remove("card-selected");
                }else{
                    System.out.println("SWAPPO "+selectedWarehouseRow+" e 1");
                    clientController.swapDepots(selectedWarehouseRow,1);
                    clearWarehouseSelection();
                    //TODO: aggiornare la vista
                }
            }
        });

        warehouseRow2.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && controlsEnabled) {
                warehouse3.getStyleClass().add("card-selected");
                warehouse4.getStyleClass().add("card-selected");
                warehouse5.getStyleClass().add("card-selected");
            } else if(selectedWarehouseRow == null || selectedWarehouseRow != 2){
                warehouse3.getStyleClass().remove("card-selected");
                warehouse4.getStyleClass().remove("card-selected");
                warehouse5.getStyleClass().remove("card-selected");
            }
        });

        warehouseRow2.setOnMouseClicked((e)->{
            if(!controlsEnabled)
                return;
            if(selectedWarehouseRow == null){
                selectedWarehouseRow = 2;
                warehouse3.getStyleClass().add("card-selected");
                warehouse4.getStyleClass().add("card-selected");
                warehouse5.getStyleClass().add("card-selected");
            }else{
                if (selectedWarehouseRow == 2) {
                    selectedWarehouseRow = null;
                    warehouse3.getStyleClass().remove("card-selected");
                    warehouse4.getStyleClass().remove("card-selected");
                    warehouse5.getStyleClass().remove("card-selected");
                }else{
                    System.out.println("SWAPPO "+selectedWarehouseRow+" e 2");
                    clientController.swapDepots(selectedWarehouseRow,2);
                    clearWarehouseSelection();
                    //TODO: aggiornare la vista
                }
            }
        });
    }

    private void clearWarehouseSelection(){
        warehouse0.getStyleClass().remove("card-selected");
        warehouse1.getStyleClass().remove("card-selected");
        warehouse2.getStyleClass().remove("card-selected");
        warehouse3.getStyleClass().remove("card-selected");
        warehouse4.getStyleClass().remove("card-selected");
        warehouse5.getStyleClass().remove("card-selected");
        selectedWarehouseRow = null;
    }

    public void askProductionsToStart(){
        baseProductionBtn.getStyleClass().add("btnProduction");
    }

    public void showLeaderCards(List<LeaderCard> leaderCardList) {
        this.playerBoard.setLeaderCards(leaderCardList);

        ImageView[] leaderCards = {leadercard0,leadercard1};

        //TODO: quando mostri le leadercard di un altro coprirle

        for(int i=0;i<leaderCardList.size();i++){
            LeaderCard card = leaderCardList.get(i);
            leaderCards[i].setImage(new Image("/images/cards/FRONT/" + card.getCardName() + ".png"));
            final int index = i;
            leaderCards[i].setOnMouseClicked((e) -> leaderCardClicked(index));
            if(card.isActive()) {
                leaderCards[i].getStyleClass().remove("leadercard");
                leaderCards[i].getStyleClass().add("active-leadercard");
            }
        }
    }

    public void storeResourcesFromMarket(Resource[] resources){
        disableControls();
        marketBtn.setVisible(false);
        startProductionBtn.setVisible(false);
        buyDevelopmentcardBtn.setVisible(false);

        ImageView[] imgs = {resources_supply_0,resources_supply_1,resources_supply_2,resources_supply_3};

        resourcesToStore = new ArrayList<>();

        for(int i=0;i<resources.length;i++){
            if(resources[i] == NonPhysicalResourceType.VOID)
                imgs[i].setImage(new Image("/images/marbles/white.png"));
            else
                imgs[i].setImage(new Image("/images/resources/"+resources[i].toString()+".png"));
            imgs[i].setVisible(true);
            resourcesToStore.add(resources[i]);
        }

        resources_supply.setVisible(true);
    }

    public void askToStoreResource(Resource resource, Warehouse warehouse) {
        showWarehouse(warehouse);

        ImageView[] imgs = {resources_supply_0,resources_supply_1,resources_supply_2,resources_supply_3};

        for(int i=0;i<resourcesToStore.size();i++){
            if(resourcesToStore.get(i).equals(resource)){
                imgs[i].getStyleClass().add("selected");
                break;
            }
        }

    }
}
