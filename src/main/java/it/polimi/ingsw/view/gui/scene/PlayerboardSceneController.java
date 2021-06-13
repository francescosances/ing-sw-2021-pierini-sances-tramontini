package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.*;

import static it.polimi.ingsw.view.gui.GUI.SPEND;
import static it.polimi.ingsw.view.gui.GUI.calculateRequirements;

public class PlayerboardSceneController extends Controller{
    //TODO: bug: mentre si piazzano le risorse il selettore utente resta sbloccato e permette di rieffettuare nuovamente l'azione
    //TODO: disattivare selettore utenti quando non è il tuo turno
    //TODO: bug: quando si scartano le risorse dal mercato, gli altri giocatori vedono il tuo marker avanzare

    /**
     * The imageViews of the two user's leader cards
     */
    @FXML
    protected ImageView leadercard0,leadercard1;

    /**
     * The imageViews used to show the leader cards depots. This imageViews are hidden when the scene is loaded
     */
    @FXML
    protected ImageView leaderCardDepot00,leaderCardDepot01,leaderCardDepot10,leaderCardDepot11;

    /**
     * The imageViews showing the development cards sorted by slot
     */
    @FXML
    protected ImageView developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2,developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2,developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2,desk0,desk1,desk2;

    /**
     * The action buttons
     */
    @FXML
    protected Button marketBtn,startProductionBtn,buyDevelopmentcardBtn,rollbackBtn,discardResourceBtn,skipBtn,swapDepotsBtn;

    /**
     * The imageViews showing the resources in the warehouse
     */
    @FXML
    protected ImageView warehouse0,warehouse1,warehouse2,warehouse3,warehouse4,warehouse5;

    /**
     * The imageViews showing the void rows of the warehouse
     */
    @FXML
    protected ImageView warehouse_void_0,warehouse_void_1,warehouse_void_2;

    /**
     * The strongbox imageViews
     */
    @FXML
    protected ImageView strongbox0,strongbox1,strongbox2,strongbox3;

    /**
     * The pope favor tiles imageViews
     */
    @FXML
    protected ImageView vaticanreport0,vaticanreport1,vaticanreport2;

    /**
     * The resource supply and the resources to be stored imageviews
     */
    @FXML
    protected ImageView resources_supply,resources_supply_0,resources_supply_1,resources_supply_2,resources_supply_3,currentResource,boxCurrentResource;

    /**
     * The counters of the resources in the strongbox
     */
    @FXML
    protected Label lblStoring,lblCoinStrongbox,lblStoneStrongbox,lblShieldStrongbox,lblServantStrongbox;

    /**
     * The marker of the player and the marker of the black cross on the faith track
     */
    @FXML
    protected ImageView marker,blackCross;

    /**
     * The graphic regions over the warehouse row
     */
    @FXML
    protected Region warehouseRow0,warehouseRow1,warehouseRow2;

    /**
     * The choicebox used to select the user to show
     */
    @FXML
    protected ChoiceBox<String> selectUser;

    /**
     * The button used to choose the base production
     */
    @FXML
    protected ImageView baseProductionBtn;

    /**
     * True if the buttons are enabled so that the user can click them
     */
    private boolean controlsEnabled = false;

    /**
     * The coordinates of the faith track cells used to show the marker
     */
    private final static double[][] FAITH_TRACK_CELLS = {{22,212}, {64,212}, {106,212}, {106,170}, {106,128}, {148,128}, {190,128}, {232,128}, {276,128}, {318,128}, {318,170}, {318,212}, {360,212}, {402,212}, {446,212}, {488,212}, {530,212}, {530,170}, {530,128}, {572,128}, {614,128}, {658,128}, {700,128}, {742,128}, {784,128}};

    /**
     * A reference to the current shown playerboard
     */
    private PlayerBoard playerBoard;

    /**
     * True if the user is storing resources
     */
    private boolean storing = false;

    /**
     * The action to be executed when the rollback button is pressed
     */
    private Runnable onRollbackAction = ()->{};//TODO: controllare quando viene utilizzato e perché non risettato mai ai valori di default

    /**
     * The warehouse rows that have been selected
     */
    private final List<Integer> selectedWarehouseRows = new ArrayList<>();

    /**
     * The default action executed when a warehouse row is selected
     */
    private final Runnable defaultWarehouseAction = ()->{
        if(selectedWarehouseRows.size() == 2){
            swapDepotsBtn.getStyleClass().remove("selected");
            clientController.swapDepots(selectedWarehouseRows.get(0),selectedWarehouseRows.get(1));
            clearWarehouseSelection();
        }
    };

    /**
     * The action to be executed when the user change the current shown user on the choiceBox
     */
    private final ChangeListener<? super Number> changeUserListener = (observableValue, value, index) -> {
        int intIndex = (Integer) index;
        if(intIndex < 0)return;
        String username = selectUser.getItems().get(intIndex);
        if(!username.equals(playerBoard.getUsername())) {//if the user is not already shown
            clientController.showPlayerBoard(selectUser.getItems().get(intIndex));
            selectUser.setDisable(true);
            disableControls();
        }
    };

    /**
     * The producers that have currently been selected to start the production
     */
    private List<Producer> selectedProducers;

    /**
     * Returns the references to the imageViews showing the resources in the wharehouse
     * @return the references to the imageViews showing the resources in the wharehouse
     */
    private ImageView[][] getWarehouseResourcesImgs(){
        return new ImageView[][]{
                {warehouse0},
                {warehouse1,warehouse2},
                {warehouse3,warehouse4,warehouse5}
        };
    }

    /**
     * Returns the references to the imageViews showing the rows of the warehouse
     * @return the references to the imageViews showing the rows of the warehouse
     */
    private ImageView[] getWarehouseRowsImgs(){
        return new ImageView[]{warehouse_void_0,warehouse_void_1,warehouse_void_2};
    }

    /**
     * Returns the references to the imageViews showing the leader cards
     * @return the references to the imageViews showing the leader cards
     */
    private ImageView[] getLeaderCardsImgs(){
        return new ImageView[]{leadercard0,leadercard1};
    }

    /**
     * Returns the references to the imageViews showing the development cards slots
     * @return the references to the imageViews showing the development cards slots
     */
    private ImageView[][] getDevelopmentCardsSlotsImgs(){
        return new ImageView[][]{
                {developmentcardslot0_0,developmentcardslot0_1,developmentcardslot0_2},
                {developmentcardslot1_0,developmentcardslot1_1,developmentcardslot1_2},
                {developmentcardslot2_0,developmentcardslot2_1,developmentcardslot2_2}
        };
    }

    /**
     * Returns the references to the imageViews showing the resources to store
     * @return the references to the imageViews showing the resources to store
     */
    private ImageView[] getResourcesSupplyImgs(){
        return new ImageView[]{resources_supply_0,resources_supply_1,resources_supply_2,resources_supply_3};
    }

    /**
     * Returns the references to the imageViews showing the development card slot's desks
     * @return the references to the imageViews showing the development card slot's desks
     */
    private ImageView[] getDesksImgs(){
        return new ImageView[]{desk0,desk1,desk2};
    }


    /**
     * The method used to initialize the stage. The controls are disabled by default
     * @param playerBoard the playerBoard to show
     */
    @FXML
    public void initialize(PlayerBoard playerBoard){
        showPlayerBoard(playerBoard);

        disableControls();

        selectUser.setDisable(false);

        populateUserSelect();
    }

    /**
     * The method called when the rollback button is pressed. It executes the set rollbackAction and tell the clientcontroller to rollback.
     */
    @FXML
    public void rollback(){
        onRollbackAction.run();
        clientController.rollback();
    }

    /**
     * Populate the user ChoiceBox with the list of players
     */
    public void populateUserSelect(){
        List<String> players = clientController.getPlayers();

        if(players == null)
            return;

        selectUser.getSelectionModel().selectedIndexProperty().removeListener(changeUserListener);//removes the listener to avoid loop listeners call

        selectUser.setItems(FXCollections.observableArrayList(players));
        selectUser.setValue((playerBoard.getUsername().equals(clientController.getUsername()))?Match.YOU_STRING: playerBoard.getUsername());

        selectUser.getSelectionModel().selectedIndexProperty().addListener(changeUserListener);//reset the listener to the choicebox
    }

    /**
     * The method to call when a leader card is clicked. It shows a dialog that let the user play or discard the card
     * @param cardIndex the index of the choosen card in the player's cards list
     */
    private void leaderCardClicked(int cardIndex){
        Dialog<String> dialog = new Dialog<>();

        dialog.setTitle("Choose action");

        Pane pane = new Pane();

        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10,10,10,10));
        vBox.setSpacing(10);

        ImageView activate = new ImageView("/images/buttons/activate.png");
        activate.setCursor(Cursor.HAND);
        activate.setPreserveRatio(true);
        activate.setFitWidth(250);
        activate.setOnMouseClicked((e)->{
            dialog.setResult("activate");
            dialog.close();
        });
        vBox.getChildren().add(activate);

        ImageView discard = new ImageView("/images/buttons/discard.png");
        discard.setPreserveRatio(true);
        discard.setCursor(Cursor.HAND);
        discard.setFitWidth(250);
        discard.setOnMouseClicked((e)->{
            swapDepotsBtn.getStyleClass().remove("selected");
            dialog.setResult("discard");
            dialog.close();
        });
        vBox.getChildren().add(discard);

        pane.getChildren().add(vBox);
        dialog.getDialogPane().setContent(pane);

        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
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

    /**
     * Disable all the buttons so that the user cannot click them
     */
    public void disableControls(){
        controlsEnabled = false;
        marketBtn.setDisable(true);
        buyDevelopmentcardBtn.setDisable(true);
        startProductionBtn.setDisable(true);
        Arrays.stream(getLeaderCardsImgs()).forEach(card->card.setDisable(true));
    }

    /**
     * Enable the default buttons so that the user can click them
     */
    public void enableControls(){
        if(playerBoard.getUsername().equals(clientController.getUsername())) {
            controlsEnabled = true;
            marketBtn.setDisable(false);
            buyDevelopmentcardBtn.setDisable(false);
            startProductionBtn.setDisable(false);
            Arrays.stream(getLeaderCardsImgs()).forEach(card->card.setDisable(false));
        }
        selectUser.setDisable(false);
    }

    /**
     * Method called when the market button is pressed. It tells the client controller to open the market
     */
    @FXML
    public void goToMarket() {
        clientController.performAction(Action.TAKE_RESOURCES_FROM_MARKET);
    }

    /**
     * Method called when the production button is pressed.
     * If the user has not choose the producers, it enables the listener to let him choose them. Otherwise, it sends the producer list to the clientController to start the production
     */
    @FXML
    public void startProduction() {
        if(selectedProducers == null)
            selectedProducers = new ArrayList<>();
        if (selectedProducers.isEmpty()) {
            disableControls();
            selectUser.setDisable(true);
            clientController.performAction(Action.ACTIVATE_PRODUCTION);
        }
    }

    /**
     * Method called when the buy development card button is pressed. It tells the client controller to open the development cards list
     */
    @FXML
    public void buyDevelopmentCard() {
        clientController.performAction(Action.BUY_DEVELOPMENT_CARD);
    }

    /**
     * Update the warehouse on the playerboard
     * @param warehouse the new warehouse to show
     */
    public void showWarehouse(Warehouse warehouse){
        this.playerBoard.setWarehouse(warehouse);

        final ImageView[][] imgWarehouse = getWarehouseResourcesImgs();

        List<Depot> depots = warehouse.getDepots();

        for (ImageView[] imageViews : imgWarehouse) {
            for (ImageView imageView : imageViews) imageView.setVisible(false);
        }

        for(int i=0;i<3;i++){
            int j;
            for(j=0;j<depots.get(i).getOccupied();j++){
                //shows the stored resources
                imgWarehouse[i][j].setImage(new Image("/images/resources/"+depots.get(i).getResourceType().toString()+".png"));
                imgWarehouse[i][j].setVisible(true);
            }
        }

        //The depots leaderCards imageViews references
        final ImageView[][] leaderCardsDepotImg = {
                {leaderCardDepot00,leaderCardDepot01},
                {leaderCardDepot10,leaderCardDepot11}
        };

        //hide all the resources by default
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


    /**
     * Method to call when a warehouse row is clicked. It runs the action specified by parameter
     * @param rowIndex the index of the warehouse's row
     * @param action the action to be executed
     */
    private void warehouseSelected(int rowIndex,Runnable action){

        final ImageView[][] imgWarehouse = getWarehouseResourcesImgs();

        final ImageView[] warehouseVoids = getWarehouseRowsImgs();

        final ImageView[] leaderCardsImgs = getLeaderCardsImgs();

        if(selectedWarehouseRows.contains(rowIndex)){
            selectedWarehouseRows.removeIf(i -> i == rowIndex);
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

    /**
     * Let the user click on the warehouse rows
     * @param onDepotSelectedAction the action to be executed when the depot is clicked
     */
    private void enableWarehouseSelection(Runnable onDepotSelectedAction){

        final ImageView[][] imgWarehouse = getWarehouseResourcesImgs();

        final ImageView[] warehouseVoids = getWarehouseRowsImgs();

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

        final ImageView[] leaderCardsImg = getLeaderCardsImgs();

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

    /**
     * Cancel the current warehouse rows selection
     */
    private void clearWarehouseSelection(){
        Arrays.stream(getWarehouseRowsImgs()).forEach(row->row.getStyleClass().remove("selected"));
        Arrays.stream(getWarehouseResourcesImgs()).forEach(row->Arrays.stream(row).forEach(cell->cell.getStyleClass().remove("selected")));

        final ImageView[] leaderCardsImg = getLeaderCardsImgs();

        List<LeaderCard> leaderCards = playerBoard.getLeaderCards();
        for(int i=0;i<leaderCards.size();i++){
            if(leaderCards.get(i).isActive() && leaderCards.get(i).isDepotLeaderCard()){
                leaderCardsImg[i].getStyleClass().remove("selected");
            }
        }

        selectedWarehouseRows.clear();
    }

    /**
     * Shows the resources supply and enables the controls needed to store the resources in the warehouse
     * @param resources the resources to store
     */
    public void storeResourcesFromMarket(Resource[] resources){
        marketBtn.setVisible(false);
        startProductionBtn.setVisible(false);
        buyDevelopmentcardBtn.setVisible(false);

        final ImageView[] imgs = getResourcesSupplyImgs();

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

    /**
     * Highlights the current resource to store
     * @param resource the resource to store
     * @param warehouse the warehouse where to store the resource
     */
    public void askToStoreResource(Resource resource, Warehouse warehouse) {
        storing = true;

        if(resource == null)
            return;

        currentResource.setImage(new Image("/images/resources/"+ resource +".png"));

        discardResourceBtn.setVisible(true);
        currentResource.setVisible(true);
        boxCurrentResource.setVisible(true);
        lblStoring.setVisible(true);

        showWarehouse(warehouse);

        enableWarehouseSelection(()->{
            if(selectedWarehouseRows.size() == 1){
                clientController.chooseDepot(selectedWarehouseRows.get(0));
                clearWarehouseSelection();
            }
        });

        swapDepotsBtn.setVisible(true);
        discardResourceBtn.setVisible(true);
    }

    /**
     * Discards the resource that is currently been stored
     */
    public void discardResource() {
        clientController.chooseDepot(playerBoard.getWarehouse().getDepots().size() + 1);
    }

    /**
     * Closes the resource supply after placing the resources
     */
    private void clearResourceSupply(){
        Arrays.stream(getResourcesSupplyImgs()).forEach(img -> img.setVisible(false));
        resources_supply.setVisible(false);
        currentResource.setVisible(false);
        lblStoring.setVisible(false);
        boxCurrentResource.setVisible(false);
        discardResourceBtn.setVisible(false);
        swapDepotsBtn.setVisible(false);
    }

    /**
     * Associates the imageview with the relative producer and makes it selectable to start the production
     * @param imageView the imageView to enable
     * @param producer the producer associated to the imageview
     */
    private void addListenerToProducer(ImageView imageView, Producer producer){
        imageView.setDisable(false);
        imageView.getStyleClass().remove("selected");
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


    /**
     * Enables controls used to choose producers and to start the production
     * @param availableProductions the productions that can be started
     * @param chooser the action to be executed when a producer is selected
     */
    public void askProductionsToStart(List<Producer> availableProductions,ProductionChooser chooser) {
        selectedProducers = new ArrayList<>();

        selectUser.setDisable(true);

        marketBtn.setVisible(false);
        buyDevelopmentcardBtn.setVisible(false);
        rollbackBtn.setVisible(true);
        startProductionBtn.setVisible(true);

        startProductionBtn.setOnAction((e)->{
            chooser.chooseResource(selectedProducers, calculateRequirements(selectedProducers, SPEND));
        });

        if(availableProductions.contains(DevelopmentCard.getBaseProduction())) {
            baseProductionBtn.getStyleClass().add("btnProduction");
            addListenerToProducer(baseProductionBtn, DevelopmentCard.getBaseProduction());
        }

        final ImageView[] leaderCardsImg = getLeaderCardsImgs();

        int index = 0;
        for(LeaderCard leaderCard:playerBoard.getLeaderCards()){
            if(leaderCard.isActive() && leaderCard.isProductionLeaderCard() && availableProductions.contains(leaderCard)){
                ProductionLeaderCard productionLeaderCard = (ProductionLeaderCard) leaderCard;
                leaderCardsImg[index].getStyleClass().add("selectable");
                addListenerToProducer(leaderCardsImg[index],productionLeaderCard);
            }
            index++;
        }

        final ImageView[][] slots = getDevelopmentCardsSlotsImgs();

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

    /**
     * Reset the currently selected producers
     */
    public void cancelProductionSelection(){
        selectedProducers = new ArrayList<>();

        startProductionBtn.setVisible(true);
        marketBtn.setVisible(true);
        buyDevelopmentcardBtn.setVisible(true);
        rollbackBtn.setVisible(false);

        baseProductionBtn.getStyleClass().remove("btnProduction");
        baseProductionBtn.getStyleClass().remove("selected");
        baseProductionBtn.setDisable(true);

        final ImageView[] leaderCardsImg = getLeaderCardsImgs();

        startProductionBtn.setOnAction((e)->{
            startProduction();
        });

        int index = 0;
        for(LeaderCard leaderCard:playerBoard.getLeaderCards()){
            if(leaderCard.isActive() && leaderCard.isProductionLeaderCard()){
                leaderCardsImg[index].getStyleClass().remove("selectable");
                leaderCardsImg[index].getStyleClass().remove("selected");
                leaderCardsImg[index].setDisable(true);
            }
            index++;
        }

        final ImageView[][] slots = getDevelopmentCardsSlotsImgs();

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

    /**
     * Method called when a development card is bought to choose in which slot put it.
     * @param slots the available slots
     * @param developmentCard the development card to be placed
     */
    public void chooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard) {
        disableControls();

        final ImageView[][] slotsImg = getDevelopmentCardsSlotsImgs();

        final ImageView[] desks = {desk0,desk1,desk2};

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

    /**
     * Makes the slots not selectable by the user
     */
    private void disableDevelopmentCardSlots(){
        final ImageView[][] slotsImg = getDevelopmentCardsSlotsImgs();

        int index = 0;
        for(DevelopmentCardSlot slot:playerBoard.getDevelopmentCardSlots()){
                final ImageView selectedImageView = slot.isEmpty()?getDesksImgs()[index]:slotsImg[index][slot.getSize()-1];
                selectedImageView.getStyleClass().remove("selectable");
                selectedImageView.setOnMouseClicked((e)->{});
            index++;
        }
    }

    /**
     * Render the specified playerboard
     * @param playerBoard the playerboard to be shown
     */
    private void showPlayerBoard(PlayerBoard playerBoard){
        if(storing && !playerBoard.getUsername().equals(this.playerBoard.getUsername()))
            return;

        this.playerBoard = playerBoard;

        showLeaderCards(playerBoard.getLeaderCards());

        showDevelopmentCards(playerBoard.getDevelopmentCardSlots());

        showWarehouse(playerBoard.getWarehouse());

        showStrongbox(playerBoard.getStrongbox());

        showFaithTrack(playerBoard.getFaithTrack());
    }

    /**
     * Reset all the controls to the default state
     * @param availableActions the action that can be executed (the relative buttons are re-enabled)
     */
    public void resetControls(Action[] availableActions) {
        storing = false;
        this.populateUserSelect();
        this.enableControls();

        selectUser.setDisable(false);

        startProductionBtn.setDisable(Arrays.stream(availableActions).noneMatch(action -> action == Action.ACTIVATE_PRODUCTION));
        marketBtn.setDisable(Arrays.stream(availableActions).noneMatch(action -> action == Action.TAKE_RESOURCES_FROM_MARKET));
        buyDevelopmentcardBtn.setDisable(Arrays.stream(availableActions).noneMatch(action -> action == Action.BUY_DEVELOPMENT_CARD));
        skipBtn.setVisible(Arrays.stream(availableActions).anyMatch(action -> action == Action.SKIP));
        swapDepotsBtn.setVisible(false);

        startProductionBtn.setVisible(true);
        marketBtn.setVisible(true);
        buyDevelopmentcardBtn.setVisible(true);

        enableWarehouseSelection(defaultWarehouseAction);

        for (ImageView[] imageViews : getDevelopmentCardsSlotsImgs()) {
            for (ImageView imageView : imageViews) {
                imageView.getStyleClass().clear();
                imageView.getStyleClass().add("card");
                imageView.getStyleClass().add("developmentcard");
                imageView.setOnMouseClicked((e) -> {});
            }
        }

        baseProductionBtn.getStyleClass().remove("selected");

        this.clearResourceSupply();

        if(leadercard0 != null)
            leadercard0.getStyleClass().remove("selected");
        if(leadercard1 != null)
            leadercard1.getStyleClass().remove("selected");

    }

    /**
     * Method called when the turn ends
     */
    @FXML
    public void skip(){
        clientController.performAction(Action.SKIP);
    }

    /**
     * Makes the warehouse rows selectable in order to swap two depots
     */
    public void swapDepots() {
        swapDepotsBtn.getStyleClass().add("selected");
        enableWarehouseSelection(defaultWarehouseAction);
    }

    /**
     * Updates the currently shown playerboard with the specified faithtrack
     * @param faithTrack the faithrack to be shown
     */
    public void showFaithTrack(FaithTrack faithTrack) {
        if(!faithTrack.getUsername().equals(this.playerBoard.getUsername()))
            return;

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
                    popeFavorTiles[i].setImage(new Image("/images/punchboard/pope_favor_tile_y" + i + ".png"));
                    popeFavorTiles[i].setVisible(true);
                }
            }
        }else{
            blackCross.setX(FAITH_TRACK_CELLS[faithTrack.getFaithMarker()][0]);
            blackCross.setY(FAITH_TRACK_CELLS[faithTrack.getFaithMarker()][1]);
            blackCross.setVisible(true);
        }

    }

    /**
     * Updates the currently shown playerboard with the specified strongbox
     * @param strongbox the strongbox to be shown
     */
    public void showStrongbox(Strongbox strongbox){
        this.playerBoard.setStrongbox(strongbox);

        lblCoinStrongbox.setText(String.valueOf(strongbox.getResourcesNum(ResourceType.COIN)));
        lblStoneStrongbox.setText(String.valueOf(strongbox.getResourcesNum(ResourceType.STONE)));
        lblShieldStrongbox.setText(String.valueOf(strongbox.getResourcesNum(ResourceType.SHIELD)));
        lblServantStrongbox.setText(String.valueOf(strongbox.getResourcesNum(ResourceType.SERVANT)));
    }

    /**
     * Updates the currently shown playerboard with the specified leadercards
     * @param leaderCardList the leader cards to show
     */
    public void showLeaderCards(List<LeaderCard> leaderCardList) {
        this.playerBoard.setLeaderCards(leaderCardList);

        final ImageView[] leaderCards = getLeaderCardsImgs();

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

    /**
     * Updates the currently shown playerboard with the specified developmentcards
     * @param developmentCardSlots the development cards to be shown
     */
    public void showDevelopmentCards(DevelopmentCardSlot[] developmentCardSlots) {

        final ImageView[][] slots = getDevelopmentCardsSlotsImgs();

        Arrays.stream(slots).forEach(slotRow -> Arrays.stream(slotRow).forEach(slot ->slot.setVisible(false)));

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

    /**
     * Update the user's choicebox with the currently active user
     * @param username the username of the current active user
     */
    public void showCurrentActiveUser(String username) {
        if(!username.equals(clientController.getUsername()) && !username.equals(Match.YOU_STRING)){
            selectUser.setValue(username);
            selectUser.setDisable(true);
            skipBtn.setVisible(false);
        }
    }

    /**
     * This method is called at the end of the turn to bring back the controls to the default status
     */
    public void performedAction(){
        storing = false;
        clearResourceSupply();
        rollbackBtn.setVisible(false);
        selectedProducers = new ArrayList<>();
        baseProductionBtn.getStyleClass().clear();
        baseProductionBtn.setOnMouseClicked((e)->{});
        startProductionBtn.setOnAction(e->startProduction());
    }

}
