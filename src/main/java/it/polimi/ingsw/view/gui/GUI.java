package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentCardSlot;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.gui.scene.Controller;
import it.polimi.ingsw.view.gui.scene.SelectLobbySceneController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.util.List;

public class GUI implements View {

    private ClientController clientController;

    private Stage stage;

    private String currentScene;

    private Controller currentController;


    public GUI(ClientController clientController, Stage stage){
        this.clientController = clientController;
        this.stage = stage;
    }

    private Controller loadScene(String sceneName){
        if(currentScene != null && currentScene.equals(sceneName))
            return currentController;
        this.currentScene = sceneName;
        Pair<Scene, Controller> sceneControllerPair = JavaFXGui.loadScene(sceneName, clientController);
        stage.setScene(sceneControllerPair.fst);
        this.currentController = sceneControllerPair.snd;
        return sceneControllerPair.snd;
    }

    @Override
    public void showMessage(String message) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
            alert.show();
        });
    }

    @Override
    public void showErrorMessage(String message) {
        Platform.runLater(()->{
            Alert alert = new Alert(Alert.AlertType.ERROR, message);
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
    public void resumeMatch(Match match) {

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

    }

    @Override
    public void userDisconnected(String username) {

    }

    @Override
    public void listLeaderCards(List<LeaderCard> leaderCardList, int cardsToChoose) {
        Platform.runLater(()->{
            loadScene("select_leader_cards_scene");
        });
    }

    @Override
    public void listDevelopmentCards(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard userBoard) {

    }

    @Override
    public void showPlayerBoard(PlayerBoard playerBoard) {

    }

    @Override
    public void showWarehouse(Warehouse warehouse) {

    }

    @Override
    public void askToSwapDepots(Warehouse warehouse) {

    }

    @Override
    public void askForAction(Action... availableActions) {

    }

    @Override
    public void takeResourcesFromMarket(Market market) {

    }

    @Override
    public void showMarket(Market market) {

    }

    @Override
    public void showResourcesGainedFromMarket(Resource[] resources) {

    }

    @Override
    public void askToStoreResource(Resource resource, Warehouse warehouse) {

    }

    @Override
    public void chooseWhiteMarbleConversion(LeaderCard leaderCard, LeaderCard leaderCard1) {

    }

    @Override
    public void askToChooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard) {

    }

    @Override
    public void chooseProductions(List<Producer> availableProductions, PlayerBoard playerBoard) {

    }

    @Override
    public void showCurrentActiveUser(String username) {

    }

    @Override
    public void askToChooseStartResources(Resource[] values, int resourcesToChoose) {

    }

}
