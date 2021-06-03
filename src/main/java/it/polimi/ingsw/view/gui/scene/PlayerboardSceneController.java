package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.*;

public class PlayerboardSceneController extends Controller{

    @FXML
    protected ImageView leadercard0,leadercard1;

    @FXML
    protected ImageView leaderCardDepot00,leaderCardDepot01,leaderCardDepot10,leaderCardDepot11;

    @FXML
    protected ImageView developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2,developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2,developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2,desk0,desk1,desk2;

    @FXML
    protected Button marketBtn,startProductionBtn,buyDevelopmentcardBtn,rollbackBtn,discardResourceBtn,skipBtn,swapDepotsBtn;

    @FXML
    protected ImageView warehouse0,warehouse1,warehouse2,warehouse3,warehouse4,warehouse5,warehouse_void_0,warehouse_void_1,warehouse_void_2;

    @FXML
    protected ImageView strongbox0,strongbox1,strongbox2,strongbox3;

    @FXML
    protected ImageView vaticanreport0,vaticanreport1,vaticanreport2;

    @FXML
    protected ImageView resources_supply,resources_supply_0,resources_supply_1,resources_supply_2,resources_supply_3,currentResource,boxCurrentResource;

    @FXML
    protected Label lblStoring,lblCoinStrongbox,lblStoneStrongbox,lblShieldStrongbox,lblServantStrongbox;

    @FXML
    protected ImageView marker,blackCross;

    @FXML
    protected StackPane root;

    @FXML
    protected Region warehouseRow0,warehouseRow1,warehouseRow2;

    @FXML
    protected ChoiceBox<String> selectUser;

    @FXML
    protected ImageView baseProductionBtn;

    private boolean controlsEnabled = false;

    private final static double[][] FAITH_TRACK_CELLS = {{22,212}, {64,212}, {106,212}, {106,170}, {106,128}, {148,128}, {190,128}, {232,128}, {276,128}, {318,128}, {318,170}, {318,212}, {360,212}, {402,212}, {446,212}, {488,212}, {530,212}, {530,170}, {530,128}, {572,128}, {614,128}, {658,128}, {700,128}, {742,128}, {784,128}};

    private PlayerBoard playerBoard;

    private List<Producer> selectedProducers = new ArrayList<>();

    private Runnable onRollbackAction = ()->{};

    private final List<Integer> selectedWarehouseRows = new ArrayList<>();

    private final Runnable defaultWarehouseAction = ()->{
        if(selectedWarehouseRows.size() == 2){
            clientController.swapDepots(selectedWarehouseRows.get(0),selectedWarehouseRows.get(1));
            clearWarehouseSelection();
        }
    };

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
        showPlayerBoard(playerBoard);

        disableControls();

        selectUser.setDisable(false);

        populateUserSelect();
    }

    @FXML
    public void rollback(){
        onRollbackAction.run();
        clientController.rollback();
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
                clientController.activateLeaderCard(playerBoard.getLeaderCards().get(0).isActive()?0:cardIndex);
            }else if(result.get().equals("discard")){
                clientController.discardLeaderCard(playerBoard.getLeaderCards().get(0).isActive()?0:cardIndex);
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
                {warehouse0},
                {warehouse1,warehouse2},
                {warehouse3,warehouse4,warehouse5}
        };

        List<Depot> depots = warehouse.getDepots();

        System.out.println(depots);

        for (ImageView[] imageViews : imgWarehouse) {
            for (ImageView imageView : imageViews) imageView.setVisible(false);
        }

        for(int i=0;i<3;i++){
            int j;
            for(j=0;j<depots.get(i).getOccupied();j++){
                imgWarehouse[i][j].setImage(new Image("/images/resources/"+depots.get(i).getResourceType().toString()+".png"));
                imgWarehouse[i][j].setVisible(true);
            }
        }

        final ImageView[][] leaderCardsDepotImg = {
                {leaderCardDepot00,leaderCardDepot01},
                {leaderCardDepot10,leaderCardDepot11}
        };

        Arrays.stream(leaderCardsDepotImg).forEach(card-> Arrays.stream(card).forEach(item->item.setVisible(false)));

        List<LeaderCard> leaderCards = playerBoard.getLeaderCards();
        int depotIndex = 3;
        for(int i=0;i<leaderCards.size();i++) {
            if (leaderCards.get(i).isActive() && leaderCards.get(i).isDepotLeaderCard()) {
                for (int j = 0; j < depots.get(depotIndex).getOccupied(); j++) {
                    leaderCardsDepotImg[i][j].setImage(new Image("/images/resources/" + depots.get(depotIndex).getResourceType().toString() + ".png"));
                    leaderCardsDepotImg[i][j].setVisible(true);
                }
                depotIndex++;
            }
        }

        enableWarehouseSelection(defaultWarehouseAction);
    }

    private void warehouseSelected(int rowIndex,Runnable action){

        final ImageView[][] imgWarehouse = {
                {warehouse0},
                {warehouse1,warehouse2},
                {warehouse3,warehouse4,warehouse5}
        };

        final ImageView[] warehouseVoids = {warehouse_void_0,warehouse_void_1,warehouse_void_2};

        final ImageView[] leaderCardsImgs = {leadercard0,leadercard1};

        if(selectedWarehouseRows.contains(rowIndex)){
            selectedWarehouseRows.removeIf(i -> i == rowIndex);
            //clearWarehouseSelection(); attivare
            if(rowIndex<3){
                warehouseVoids[rowIndex].getStyleClass().remove("selected");
            }else{
                Arrays.stream(leaderCardsImgs).forEach(img->img.getStyleClass().remove("selected"));
            }
            for(ImageView imageView:imgWarehouse[rowIndex])//TODO: verificare perché può verificarsi index out of bound (quando si seleziona due volte stessa carta leader)
                imageView.getStyleClass().remove("selected");
        }else{
            selectedWarehouseRows.add(rowIndex);
            if(rowIndex < 3) {
                warehouseVoids[rowIndex].getStyleClass().add("selected");
                for (ImageView imageView : imgWarehouse[rowIndex])
                    imageView.getStyleClass().add("selected");
            }else{
                List<LeaderCard> leaderCards = playerBoard.getLeaderCards();
                int cardIndex = rowIndex-3;
                for(int i=0;i<leaderCards.size();i++){
                    if(leaderCards.get(i).isActive() && leaderCards.get(i).isDepotLeaderCard()){
                        if(cardIndex == i)
                            leaderCardsImgs[cardIndex].getStyleClass().add("selected");
                        cardIndex--;
                    }
                }
            }
            action.run();
        }
    }

    private void enableWarehouseSelection(Runnable onDepotSelectedAction){

        final ImageView[][] imgWarehouse = {
                {warehouse0},
                {warehouse1,warehouse2},
                {warehouse3,warehouse4,warehouse5}
        };

        final ImageView[] warehouseVoids = {warehouse_void_0,warehouse_void_1,warehouse_void_2};

        final Region[] warehouseRows = {warehouseRow0,warehouseRow1,warehouseRow2};

        for(int i=0;i<warehouseRows.length;i++) {
            final int rowIndex = i;
            warehouseRows[i].hoverProperty().addListener((observable,oldvalue,newvalue)->{
                if(newvalue && controlsEnabled){
                    warehouseVoids[rowIndex].getStyleClass().add("selected");
                    for(ImageView imageView:imgWarehouse[rowIndex])
                        imageView.getStyleClass().add("selected");
                }else{
                    warehouseVoids[rowIndex].getStyleClass().remove("selected");
                    for(ImageView imageView:imgWarehouse[rowIndex])
                        imageView.getStyleClass().remove("selected");
                }
            });
            warehouseRows[i].setOnMouseClicked((e)->{
                if(!controlsEnabled)
                    return;
                warehouseSelected(rowIndex,onDepotSelectedAction);
            });
        }

        final ImageView[] leaderCardsImg = {leadercard0,leadercard1};

       List<LeaderCard> leaderCards = playerBoard.getLeaderCards();
        int depotIndex = 3;
        for(int i=0;i<leaderCards.size();i++){
             if(leaderCards.get(i).isActive() && leaderCards.get(i).isDepotLeaderCard()){
                 final int cardIndex = i,depotIndexFinal = depotIndex;
                 leaderCardsImg[i].hoverProperty().addListener((observable,oldvalue,newvalue)->{
                     if(newvalue && controlsEnabled){
                         leaderCardsImg[cardIndex].getStyleClass().add("selected");
                     }else{
                         leaderCardsImg[cardIndex].getStyleClass().remove("selected");
                     }
                 });
                 leaderCardsImg[i].setOnMouseClicked((e)->{
                     if(!controlsEnabled)
                         return;
                        warehouseSelected(depotIndexFinal,onDepotSelectedAction);
                 });
                 leaderCardsImg[i].setDisable(false);
                 depotIndex++;
             }
        }
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

        final ImageView[] leaderCardsImg = {leadercard0,leadercard1};

        List<LeaderCard> leaderCards = playerBoard.getLeaderCards();
        for(int i=0;i<leaderCards.size();i++){
            if(leaderCards.get(i).isActive() && leaderCards.get(i).isDepotLeaderCard()){
                leaderCardsImg[i].getStyleClass().remove("selected");
            }
        }

        selectedWarehouseRows.clear();
    }

    public void storeResourcesFromMarket(Resource[] resources){
        marketBtn.setVisible(false);
        startProductionBtn.setVisible(false);
        buyDevelopmentcardBtn.setVisible(false);

        ImageView[] imgs = {resources_supply_0,resources_supply_1,resources_supply_2,resources_supply_3};

        Arrays.stream(imgs).forEach(imageView -> imageView.setVisible(false));

        for(int i=0;i<resources.length;i++){
            if(resources[i] == NonPhysicalResourceType.VOID)
                imgs[i].setImage(new Image("/images/marbles/white.png"));
            else
                imgs[i].setImage(new Image("/images/resources/"+resources[i].toString()+".png"));
            imgs[i].setVisible(true);
        }

        resources_supply.setVisible(true);
    }

    public void askToStoreResource(Resource resource, Warehouse warehouse) {

        if(resource == null)
            return;

        currentResource.setImage(new Image("/images/resources/"+resource.toString()+".png"));

        discardResourceBtn.setVisible(true);
        currentResource.setVisible(true);
        boxCurrentResource.setVisible(true);
        lblStoring.setVisible(true);

        showWarehouse(warehouse);

        enableWarehouseSelection(()->{
            if(selectedWarehouseRows.size() == 1){
                clientController.chooseDepot(selectedWarehouseRows.get(0));
                System.out.println("Seleziono il deposito "+selectedWarehouseRows.get(0));
                clearWarehouseSelection();
            }
        });

      //  swapDepotsBtn.setVisible(true);
        discardResourceBtn.setVisible(true);
    }

    public void discardResource() {
        clientController.chooseDepot(playerBoard.getWarehouse().getDepots().size() + 1);
        System.out.println("discard");
    }

    private void clearResourceSupply(){
        resources_supply.setVisible(false);
        resources_supply_0.setVisible(false);
        resources_supply_1.setVisible(false);
        resources_supply_2.setVisible(false);
        resources_supply_3.setVisible(false);
        currentResource.setVisible(false);
        lblStoring.setVisible(false);
        boxCurrentResource.setVisible(false);
        discardResourceBtn.setVisible(false);
        swapDepotsBtn.setVisible(false);
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
            if(leaderCard.isActive() && leaderCard.isProductionLeaderCard() && availableProductions.contains(leaderCard)){
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
            if(leaderCard.isActive() && leaderCard.isProductionLeaderCard()){
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

        ImageView[] desks = {desk0,desk1,desk2};

        buyDevelopmentcardBtn.setVisible(false);
        marketBtn.setVisible(false);
        startProductionBtn.setVisible(false);

        int index = 0;
        for(DevelopmentCardSlot slot:slots){
            if(slot.accepts(developmentCard)){
                final int slotIndex = index;
                final ImageView selectedImageView = slot.isEmpty()?desks[index]:slotsImg[index][slot.getSize()-1];
                selectedImageView.getStyleClass().add("selectable");
                selectedImageView.setOnMouseClicked((e)-> {
                    disableDevelopmentCardSlots();
                    clientController.chooseDevelopmentCardsSlot(slotIndex);
                });
            }
            index++;
        }
    }

    private void disableDevelopmentCardSlots(){
        ImageView[][] slotsImg =
                {{developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2},
                        {developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2},
                        {developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2}};

        ImageView[] desks = {desk0,desk1,desk2};

        int index = 0;
        for(DevelopmentCardSlot slot:playerBoard.getDevelopmentCardSlots()){
                final ImageView selectedImageView = slot.isEmpty()?desks[index]:slotsImg[index][slot.getSize()-1];
                selectedImageView.getStyleClass().remove("selectable");
                selectedImageView.setOnMouseClicked((e)->{});
            index++;
        }
    }

    private void showPlayerBoard(PlayerBoard playerBoard){
        this.playerBoard = playerBoard;

        showLeaderCards(playerBoard.getLeaderCards());

        showDevelopmentCards(playerBoard.getDevelopmentCardSlots());

        showWarehouse(playerBoard.getWarehouse());

        showStrongbox(playerBoard.getStrongbox());

        showFaithTrack(playerBoard.getFaithTrack());
    }

    public void resetControls(Action[] availableActions) {
        this.populateUserSelect();
        this.enableControls();

        startProductionBtn.setDisable(Arrays.stream(availableActions).noneMatch(action -> action == Action.ACTIVATE_PRODUCTION));
        marketBtn.setDisable(Arrays.stream(availableActions).noneMatch(action -> action == Action.TAKE_RESOURCES_FROM_MARKET));
        buyDevelopmentcardBtn.setDisable(Arrays.stream(availableActions).noneMatch(action -> action == Action.BUY_DEVELOPMENT_CARD));
        skipBtn.setVisible(Arrays.stream(availableActions).anyMatch(action -> action == Action.SKIP));
        swapDepotsBtn.setVisible(false);

        startProductionBtn.setVisible(true);
        marketBtn.setVisible(true);
        buyDevelopmentcardBtn.setVisible(true);

        enableWarehouseSelection(defaultWarehouseAction);

        ImageView[][] slotsImg =
                        {{developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2},
                        {developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2},
                        {developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2}};

        for (ImageView[] imageViews : slotsImg) {
            for (ImageView imageView : imageViews) {
                imageView.getStyleClass().clear();
                imageView.getStyleClass().add("card");
                imageView.getStyleClass().add("developmentcard");
                imageView.setOnMouseClicked((e) -> {});
            }
        }

        this.clearResourceSupply();
    }

    @FXML
    public void skip(){
        clientController.performAction(Action.SKIP);
    }

    public void swapDepots() {
       enableWarehouseSelection(defaultWarehouseAction);
    }

    public void showFaithTrack(FaithTrack faithTrack) {
        if(!faithTrack.isBlackCross()) {
            playerBoard.setFaithTrack(faithTrack);

            marker.setX(FAITH_TRACK_CELLS[faithTrack.getFaithMarker()][0]);
            marker.setY(FAITH_TRACK_CELLS[faithTrack.getFaithMarker()][1]);
            marker.setVisible(true);

            ImageView[] popeFavorTiles = {vaticanreport0, vaticanreport1, vaticanreport2};
            for (int i = 0; i < 3; i++) {
                popeFavorTiles[i].setImage(new Image("/images/punchboard/pope_favor_tile_missed_" + i + ".png"));
                popeFavorTiles[i].setVisible(true);
                if (faithTrack.getPopeFavorTiles()[i] == null) {
                    popeFavorTiles[i].setVisible(false);
                } else if (faithTrack.getPopeFavorTiles()[i].isUncovered()) {
                    popeFavorTiles[i].setImage(new Image("/images/punchboard/pope_favor_tile" + i + ".png"));
                    popeFavorTiles[i].setVisible(true);
                }
            }
        }else{
            blackCross.setX(FAITH_TRACK_CELLS[faithTrack.getFaithMarker()][0]);
            blackCross.setY(FAITH_TRACK_CELLS[faithTrack.getFaithMarker()][1]);
            blackCross.setVisible(true);
        }

    }

    public void showStrongbox(Strongbox strongbox){
        this.playerBoard.setStrongbox(strongbox);

        lblCoinStrongbox.setText(String.valueOf(strongbox.getResourcesNum(ResourceType.COIN)));
        lblStoneStrongbox.setText(String.valueOf(strongbox.getResourcesNum(ResourceType.STONE)));
        lblShieldStrongbox.setText(String.valueOf(strongbox.getResourcesNum(ResourceType.SHIELD)));
        lblServantStrongbox.setText(String.valueOf(strongbox.getResourcesNum(ResourceType.SERVANT)));
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
                leaderCards[i].getStyleClass().remove("leadercard");
                leaderCards[i].getStyleClass().add("active-leadercard");
                leaderCards[i].setDisable(true);
                leaderCards[i].setOnMouseClicked((e)->{});
            }else {
                leaderCards[i].getStyleClass().add("leadercard");
                leaderCards[i].getStyleClass().remove("active-leadercard");
                leaderCards[i].setOnMouseClicked((e) -> leaderCardClicked(index));
            }
            leaderCards[i].setVisible(true);
        }
    }

    public void showDevelopmentCards(DevelopmentCardSlot[] developmentCardSlots) {

        //TODO: nascondere carte di default
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

    public void showCurrentActiveUser(String username) {
        if(!username.equals(clientController.getUsername())){
            selectUser.setValue(username);
            selectUser.setDisable(true);
            skipBtn.setVisible(false);
        }
    }
}
