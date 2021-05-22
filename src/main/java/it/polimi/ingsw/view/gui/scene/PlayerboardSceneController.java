package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.Producer;
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
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Arrays;
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
    protected Button marketBtn,startProductionBtn,buyDevelopmentcardBtn,rollbackBtn;

    @FXML
    protected ImageView warehouse0,warehouse1,warehouse2,warehouse3,warehouse4,warehouse5,warehouse_void_0,warehouse_void_1,warehouse_void_2;

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

    protected Integer selectedWarehouseRow = null;

    private final static double[][] FAITH_TRACK_CELLS = {{22,212}, {64,212}, {106,212}, {106,170}, {106,128}, {148,128}, {190,128}, {232,128}, {276,128}, {318,128}, {318,170}, {318,212}, {360,212}, {402,212}, {446,212}, {488,212}, {530,212}, {530,170}, {530,128}, {572,128}, {614,128}, {658,128}, {700,128}, {742,128}, {784,128}};

    private PlayerBoard playerBoard;

    private List<Producer> selectedProducers = new ArrayList<>();

    private Runnable onRollbackAction = ()->{};

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
       // if(playerBoard.getUsername().equals(clientController.getUsername()))
             this.playerBoard = playerBoard;

        marker.setX(FAITH_TRACK_CELLS[playerBoard.getFaithTrack().getFaithMarker()][0]);
        marker.setY(FAITH_TRACK_CELLS[playerBoard.getFaithTrack().getFaithMarker()][1]);
        marker.setVisible(true);

        showLeaderCards(playerBoard.getLeaderCards());

        showDevelopmentCards(playerBoard.getDevelopmentCardSlots());

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
        selectUser.setDisable(false);

        populateUserSelect();
    }

    @FXML
    public void rollback(){
        onRollbackAction.run();
        clientController.rollback();
    }

    protected void showDevelopmentCards(DevelopmentCardSlot[] developmentCardSlots) {
        ImageView[][] slots =
                        {{developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2},
                        {developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2},
                        {developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2}};

        int slotIndex = 0,cardIndex;

        for(DevelopmentCardSlot slot : developmentCardSlots){
            cardIndex = 0;
            for(Card card : slot){
                slots[slotIndex][cardIndex].setImage(new Image("/images/cards/FRONT/"+card.getCardName()+".png"));
                slots[slotIndex][cardIndex++].setVisible(true);
            }
            slotIndex++;
        }
    }

    public void populateUserSelect(){
        List<String> players = clientController.getPlayers();

        if(players == null)
            return;

        selectUser.getSelectionModel().selectedIndexProperty().removeListener(changeUserListener);


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
        if (selectedProducers.isEmpty()) {
            disableControls();
            selectUser.setDisable(true);
            clientController.performAction(Action.ACTIVATE_PRODUCTION);
        }else{
            chooseResourcesForProduction();
        }
    }

    @FXML
    public void buyDevelopmentCard() {
        clientController.performAction(Action.BUY_DEVELOPMENT_CARD);
    }

    public void showWarehouse(Warehouse warehouse){
        this.playerBoard.setWarehouse(warehouse);

        ImageView[][] imgWarehouse = {
                {warehouse_void_0},
                {warehouse_void_1},
                {warehouse_void_2}
        };

        List<Depot> depots = warehouse.getDepots();

        for (ImageView[] imageViews : imgWarehouse) {
            for (ImageView imageView : imageViews) imageView.setVisible(false);
        }

        for(int i=0;i<depots.size();i++){
            int j;
            for(j=0;j<depots.get(i).getOccupied();j++){
                imgWarehouse[i][j].setImage(new Image("/images/resources/"+depots.get(i).getResourceType().toString()+".png"));
                imgWarehouse[i][j].setVisible(true);
            }
        }

        ImageView warehouse_voids[] = {warehouse_void_0,warehouse_void_1,warehouse_void_2};

        warehouse_void_0.setVisible(true);
        warehouse_void_0.setDisable(false);

        warehouse_void_1.setVisible(true);
        warehouse_void_1.setDisable(false);

        warehouse_void_2.setVisible(true);
        warehouse_void_2.setDisable(false);

        warehouseRow0.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && controlsEnabled) {
                warehouse_void_0.getStyleClass().add("selected");
                warehouse0.getStyleClass().add("selected");
            } else if(selectedWarehouseRow == null || selectedWarehouseRow != 0){
                warehouse_void_0.getStyleClass().remove("selected");
                warehouse0.getStyleClass().remove("selected");
            }
        });
        warehouseRow0.setOnMouseClicked((e)->{
            if(!controlsEnabled)
                return;
            if(selectedWarehouseRow == null){
                selectedWarehouseRow = 0;
                warehouse_void_0.getStyleClass().add("selected");
            }else{
                if (selectedWarehouseRow == 0) {
                    selectedWarehouseRow = null;
                }else{
                    System.out.println("SWAPPO "+selectedWarehouseRow+" e 0");
                    clientController.swapDepots(selectedWarehouseRow,0);
                    warehouse_voids[selectedWarehouseRow].getStyleClass().remove("selected");
                    clearWarehouseSelection();
                    //TODO: aggiornare la vista
                }
                warehouse_void_0.getStyleClass().remove("selected");
            }
        });
//TODO: impedire swap depositi altrui
        warehouseRow1.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && controlsEnabled) {
                warehouse_void_1.getStyleClass().add("selected");
            } else if(selectedWarehouseRow == null || selectedWarehouseRow != 1){
                warehouse_void_1.getStyleClass().remove("selected");
            }
        });
        warehouseRow1.setOnMouseClicked((e)->{
            if(!controlsEnabled)
                return;
            if(selectedWarehouseRow == null){
                selectedWarehouseRow = 1;
                warehouse_void_1.getStyleClass().add("selected");
            }else{
                if (selectedWarehouseRow == 1) {
                    selectedWarehouseRow = null;
                }else{
                    System.out.println("SWAPPO "+selectedWarehouseRow+" e 1");
                    clientController.swapDepots(selectedWarehouseRow,1);
                    warehouse_voids[selectedWarehouseRow].getStyleClass().remove("selected");
                    clearWarehouseSelection();
                    //TODO: aggiornare la vista
                }
                warehouse_void_1.getStyleClass().remove("selected");
            }
        });

        warehouseRow2.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && controlsEnabled) {
                warehouse_void_2.getStyleClass().add("selected");
            } else if(selectedWarehouseRow == null || selectedWarehouseRow != 2){
                warehouse_void_2.getStyleClass().remove("selected");
            }
        });

        warehouseRow2.setOnMouseClicked((e)->{
            if(!controlsEnabled)
                return;
            if(selectedWarehouseRow == null){
                selectedWarehouseRow = 2;
                warehouse_void_2.getStyleClass().add("selected");
            }else{
                if (selectedWarehouseRow == 2) {
                    selectedWarehouseRow = null;
                }else{
                    System.out.println("SWAPPO "+selectedWarehouseRow+" e 2");
                    clientController.swapDepots(selectedWarehouseRow,2);
                    //TODO: aggiornare la vista
                    warehouse_voids[selectedWarehouseRow].getStyleClass().remove("selected");
                    clearWarehouseSelection();
                }
                warehouse_void_2.getStyleClass().remove("selected");
            }
        });
    }

    private void clearWarehouseSelection(){
        warehouse_void_0.getStyleClass().remove("selected");
        warehouse_void_1.getStyleClass().remove("selected");
        warehouse_void_2.getStyleClass().remove("selected");
        warehouse0.getStyleClass().remove("selected");
        warehouse1.getStyleClass().remove("selected");
        warehouse2.getStyleClass().remove("selected");
        warehouse3.getStyleClass().remove("selected");
        warehouse4.getStyleClass().remove("selected");
        warehouse5.getStyleClass().remove("selected");
        selectedWarehouseRow = null;
    }

    public void showLeaderCards(List<LeaderCard> leaderCardList) {
        this.playerBoard.setLeaderCards(leaderCardList);

        ImageView[] leaderCards = {leadercard0,leadercard1};

        Arrays.stream(leaderCards).forEach(card->card.setVisible(false));

        for(int i=0;i<leaderCardList.size();i++){
            LeaderCard card = leaderCardList.get(i);
            leaderCards[i].setImage(new Image("/images/cards/FRONT/" + card.getCardName() + ".png"));
            final int index = i;

            if(card.isActive()) {
                playerBoard.activateLeaderCard(card);
                leaderCards[i].getStyleClass().remove("leadercard");
                leaderCards[i].getStyleClass().add("active-leadercard");
                leaderCards[i].setOnMouseClicked((e)->{});
            }else {
                leaderCards[i].getStyleClass().add("leadercard");
                leaderCards[i].getStyleClass().remove("active-leadercard");
                leaderCards[i].setOnMouseClicked((e) -> leaderCardClicked(index));
            }
            leaderCards[i].setVisible(true);
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

    private void addListenerToProducer(ImageView imageView, Producer producer){
        imageView.setDisable(false);
        imageView.setOnMouseClicked((e)->{
            if(selectedProducers.contains(producer)){
                selectedProducers.remove(producer);
                imageView.getStyleClass().remove("selected");
            }else{
                selectedProducers.add(producer);
                imageView.getStyleClass().add("selected");
            }
            startProductionBtn.setDisable(selectedProducers.isEmpty());
        });
    }

    public void askProductionsToStart(List<Producer> availableProductions) {
        selectUser.setDisable(true);
        selectedProducers = new ArrayList<>();

        marketBtn.setVisible(false);
        buyDevelopmentcardBtn.setVisible(false);
        rollbackBtn.setVisible(true);
        startProductionBtn.setVisible(true);

        if(availableProductions.contains(DevelopmentCard.getBaseProduction())) {
            baseProductionBtn.getStyleClass().add("btnProduction");
            addListenerToProducer(baseProductionBtn, DevelopmentCard.getBaseProduction());
        }

        ImageView[] leaderCardsImg = {leadercard0,leadercard1};

        int index = 0;
        for(LeaderCard leaderCard:playerBoard.getLeaderCards()){
            if(leaderCard.isActive() && leaderCard.isProductionLeaderCards() && availableProductions.contains(leaderCard)){
                ProductionLeaderCard productionLeaderCard = (ProductionLeaderCard) leaderCard;
                leaderCardsImg[index].getStyleClass().add("selectable");
                addListenerToProducer(leaderCardsImg[index],productionLeaderCard);
            }
            index++;
        }

        ImageView[][] slots =
                        {{developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2},
                        {developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2},
                        {developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2}};

        index = 0;
        for(DevelopmentCardSlot slot:playerBoard.getDevelopmentCardSlots()){
            if(!slot.isEmpty() && availableProductions.contains(slot.getTopCard())){
                DevelopmentCard card = slot.getTopCard();
                slots[index][slot.getSize()-1].getStyleClass().add("selectable");
                addListenerToProducer(slots[index][slot.getSize()-1],card);
            }
            index++;
        }

        onRollbackAction = this::cancelProductionSelection;
    }

    public void cancelProductionSelection(){
        selectedProducers = new ArrayList<>();

        startProductionBtn.setVisible(true);
        marketBtn.setVisible(true);
        buyDevelopmentcardBtn.setVisible(true);
        rollbackBtn.setVisible(false);

        baseProductionBtn.getStyleClass().remove("btnProduction");
        baseProductionBtn.getStyleClass().remove("selected");
        baseProductionBtn.setDisable(true);

        ImageView[] leaderCardsImg = {leadercard0,leadercard1};

        int index = 0;
        for(LeaderCard leaderCard:playerBoard.getLeaderCards()){
            if(leaderCard.isActive() && leaderCard.isProductionLeaderCards()){
                leaderCardsImg[index].getStyleClass().remove("selectable");
                leaderCardsImg[index].getStyleClass().remove("selected");
                leaderCardsImg[index].setDisable(true);
            }
            index++;
        }

        ImageView[][] slots =
                {{developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2},
                        {developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2},
                        {developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2}};

        index = 0;
        for(DevelopmentCardSlot slot:playerBoard.getDevelopmentCardSlots()){
            if(!slot.isEmpty()){
                slots[index][slot.getSize()-1].getStyleClass().remove("selectable");
                slots[index][slot.getSize()-1].getStyleClass().remove("selected");
                slots[index][slot.getSize()-1].setDisable(true);
            }
            index++;
        }
    }

    private void chooseResourcesForProduction() {
    }

    public void chooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard) {
        disableControls();

        ImageView[][] slotsImg =
                {{developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2},
                        {developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2},
                        {developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2}};

        buyDevelopmentcardBtn.setVisible(false);
        marketBtn.setVisible(false);
        startProductionBtn.setVisible(false);

        int index = 0;
        for(DevelopmentCardSlot slot:slots){
            System.out.println(slot);
            if(slot.accepts(developmentCard)){
                slotsImg[index][slot.getSize()-1].getStyleClass().add("selectable");
                final int slotIndex = index;
                slotsImg[index][slot.getSize()-1].setOnMouseClicked((e)->{
                    clientController.chooseDevelopmentCardsSlot(slotIndex);
                });
            }
            index++;
        }
    }

    public void resetControls() {
        this.populateUserSelect();
        this.enableControls();
        startProductionBtn.setVisible(true);
        marketBtn.setVisible(true);
        buyDevelopmentcardBtn.setVisible(true);

        ImageView[][] slotsImg =
                {{developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2},
                        {developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2},
                        {developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2}};

        for (ImageView[] imageViews : slotsImg) {
            for (ImageView imageView : imageViews) {
                imageView.getStyleClass().clear();
                imageView.getStyleClass().add("card");
                imageView.getStyleClass().add("developmentcard");
                imageView.setOnMouseClicked((e) -> {
                });
            }
        }

    }
}
