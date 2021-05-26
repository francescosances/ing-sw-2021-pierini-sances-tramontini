package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Strongbox;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.gui.scene.*;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class GUI implements View {

    private ClientController clientController;

    private Stage mainStage;

    private String currentScene;

    private Controller currentController;

    private PlayerboardSceneController playerboardSceneController;

    private Semaphore playerBoardSemaphore = new Semaphore(0);

    private Set<Alert> openedAlert;

    public GUI(ClientController clientController, Stage stage){
        this.clientController = clientController;
        this.mainStage = stage;
        this.openedAlert = new HashSet<>();
    }

    private Controller loadScene(String sceneName){
        return loadScene(sceneName,false);
    }

    private Controller loadScene(String sceneName,boolean override){
        if(!override && currentScene != null && currentScene.equals(sceneName))
            return currentController;
        this.currentScene = sceneName;
        Pair<Scene, Controller> sceneControllerPair = JavaFXGui.loadScene(sceneName, clientController);
        this.currentController = sceneControllerPair.snd;
        mainStage.setScene(sceneControllerPair.fst);
        return sceneControllerPair.snd;
    }

    private Controller openModal(String sceneName,String title,Runnable onClose){
        Pair<Scene,Controller> temp = JavaFXGui.loadScene(sceneName,this.clientController);
        Stage stage = new Stage();
        stage.setScene(temp.fst);
        stage.setResizable(false);
        stage.setAlwaysOnTop(true);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setOnCloseRequest((e)-> onClose.run());
        stage.setTitle(title);
        stage.show();
        for(Alert alert : openedAlert){
            alert.close();
            Alert newAlert = new Alert(alert.getAlertType(),alert.getContentText());
            newAlert.initOwner(stage);
            newAlert.setOnCloseRequest((e)->openedAlert.remove(alert));
            newAlert.show();
        }
        return temp.snd;
    }

    private PlayerboardSceneController getPlayerBoardSceneController() {
        try {
            playerBoardSemaphore.tryAcquire(3, TimeUnit.SECONDS);
            playerBoardSemaphore.release();
        } catch (InterruptedException ignored) {
            return null;
        }
        return playerboardSceneController;
    }

    @Override
    public void showMessage(String message) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.initOwner(mainStage);
            openedAlert.add(alert);
            alert.setOnCloseRequest((e)-> openedAlert.remove(alert));
            alert.show();
        });
    }

    @Override
    public void showErrorMessage(String message) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR, message);
            alert.initOwner(mainStage);
            openedAlert.add(alert);
            alert.setOnCloseRequest((e)-> openedAlert.remove(alert));
            alert.show();
        });
    }

    @Override
    public void listLobbies(List<Triple<String, Integer, Integer>> availableLobbies) {
        Platform.runLater(() -> {
            SelectLobbySceneController controller = (SelectLobbySceneController) loadScene("select_lobby_scene");
            controller.initialize(availableLobbies);
        });
    }


    @Override
    public void resumeMatch(PlayerBoard playerBoard) {
        showPlayerBoard(playerBoard);
    }

    @Override
    public void init() {
        loadScene("server_setup_scene");
    }


    @Override
    public void askLogin() {
        loadScene("login_scene");
    }

    @Override
    public void waitForOtherPlayers() {
        loadScene("waiting_scene");
    }

    @Override
    public void userConnected(String username) {
        showMessage(username+" has joined the match");
    }

    @Override
    public void userDisconnected(String username) {
        showMessage(username+" has left the match");
    }

    @Override
    public void listLeaderCards(List<LeaderCard> leaderCardList, int cardsToChoose) {
        Platform.runLater(()->{
            SelectLeaderCardsController controller = (SelectLeaderCardsController) loadScene("select_leader_cards_scene");
            controller.initialize(leaderCardList,cardsToChoose);
        });
    }

    @Override
    public void showPlayerLeaderCards(List<LeaderCard> leaderCardList) {
        Platform.runLater(()->{
            if(getPlayerBoardSceneController() != null)
                getPlayerBoardSceneController().showLeaderCards(leaderCardList);
        });
    }

    @Override
    public void showLeaderCards(List<LeaderCard> leaderCards) {
        if(getPlayerBoardSceneController() == null)
            return;
        Platform.runLater(()-> getPlayerBoardSceneController().showLeaderCards(leaderCards));
    }

    @Override
    public void showDevelopmentCardSlots(DevelopmentCardSlot[] developmentCardSlots) {
        if(getPlayerBoardSceneController() == null)
            return;
        Platform.runLater(()-> getPlayerBoardSceneController().showDevelopmentCards(developmentCardSlots));
    }

    @Override
    public void listDevelopmentCards(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard userBoard) {
        Platform.runLater(()->{
            SelectDeveloperCardsController controller = (SelectDeveloperCardsController) openModal("select_development_cards_scene","Select development cards",()->{clientController.rollback();});
            controller.initialize(developmentCardList,cardsToChoose,userBoard);
        });
    }

    @Override
    public void showPlayerBoard(PlayerBoard playerBoard) {
        Platform.runLater(()->{
            if(playerboardSceneController == null)
                playerboardSceneController = (PlayerboardSceneController) loadScene("playerboard_scene");
            playerBoardSemaphore.release();
            playerboardSceneController.initialize(playerBoard);
        });
    }

    @Override
    public void showFaithTrack(FaithTrack faithTrack) {
        Platform.runLater(()->{
            if(getPlayerBoardSceneController() == null)
                return;
            getPlayerBoardSceneController().showFaithTrack(faithTrack);
        });
    }

    @Override
    public void showVaticanReportTriggered(String username, int vaticanReportCount) {
        //TODO
    }

    @Override
    public void showWarehouse(Warehouse warehouse) {
        if(getPlayerBoardSceneController() == null)
            return;
        Platform.runLater(()-> getPlayerBoardSceneController().showWarehouse(warehouse));
    }

    @Override
    public void showStrongbox(Strongbox strongbox) {
        if(getPlayerBoardSceneController() == null)
            return;
        Platform.runLater(()-> getPlayerBoardSceneController().showStrongbox(strongbox));
    }

    @Override
    public void askToSwapDepots(Warehouse warehouse) {

    }


    @Override
    public void askForAction(List<String> usernames, Action... availableActions) {
        clientController.setPlayers(usernames);
        if(getPlayerBoardSceneController() == null)
            return;
        Platform.runLater(()-> getPlayerBoardSceneController().resetControls(availableActions));
    }

    @Override
    public void takeResourcesFromMarket(Market market) {
        Platform.runLater(()-> showMarket(market));
    }

    @Override
    public void showMarket(Market market) {
        ((MarketSceneController)openModal("market_scene","Market",()->clientController.rollback())).initialize(market);
    }

    @Override
    public void showResourcesGainedFromMarket(Resource[] resources) {
        if(getPlayerBoardSceneController() == null)
            return;
        Platform.runLater(()-> playerboardSceneController.storeResourcesFromMarket(resources));
    }

    @Override
    public void askToStoreResource(Resource resource, Warehouse warehouse) {
        if(getPlayerBoardSceneController() == null)
            return;
        Platform.runLater(()-> playerboardSceneController.askToStoreResource(resource,warehouse));
    }

    @Override
    public void chooseWhiteMarbleConversion(LeaderCard leaderCard, LeaderCard leaderCard1) {
        Platform.runLater(()->{
           SelectWhiteMarbleConversionController controller = (SelectWhiteMarbleConversionController) openModal("select_white_marble_conversion","Select white marble conversion",()->chooseWhiteMarbleConversion(leaderCard,leaderCard1));
           controller.initialize(leaderCard.getOutputResourceType(),leaderCard1.getOutputResourceType());
        });
    }

    @Override
    public void askToChooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard) {
        if(getPlayerBoardSceneController() == null)return;
        Platform.runLater(()-> getPlayerBoardSceneController().chooseDevelopmentCardSlot(slots,developmentCard));
    }

    @Override
    public void chooseProductions(List<Producer> availableProductions, PlayerBoard playerBoard) {
        if(getPlayerBoardSceneController() == null)
            return;
        Platform.runLater(()->{
            playerboardSceneController.initialize(playerBoard);
            playerboardSceneController.askProductionsToStart(availableProductions);
        });
    }

    @Override
    public void showCurrentActiveUser(String username) {

    }

    @Override
    public void askToChooseStartResources(Resource[] values, int resourcesToChoose) {
        Platform.runLater(()-> ((SelectResourcesController) loadScene("select_resources_scene")).initialize(values,resourcesToChoose));
    }

    @Override
    public void showPlayers(Map<String, Boolean> users) {
        this.clientController.setPlayers(new ArrayList<>(users.keySet()));
    }

    @Override
    public void showActionToken(ActionToken actionToken) {
        Platform.runLater(()->{
           ActionTokenSceneController controller = (ActionTokenSceneController) openModal("action_token_scene","Action token",()->{});
           controller.initialize(actionToken);
        });
    }

    @Override
    public void showProducerUser() {
        //TODO
    }

    @Override
    public String getUsername() {
        return clientController.getUsername();
    }


}
