package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentCardSlot;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;
import javafx.application.Application;

import java.util.List;

public class GUI implements View {

    private ClientController clientController;

    public GUI(ClientController clientController){
        this.clientController = clientController;
    }

    @Override
    public void init() {
        new Thread(() -> Application.launch(JavaFXGui.class)).start();
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showErrorMessage(String message) {

    }

    @Override
    public void listLobbies(List<Triple<String, Integer, Integer>> availableLobbies) {

    }

    @Override
    public void resumeMatch(Match match) {

    }


    @Override
    public void askLogin() {

    }

    @Override
    public void userConnected(String username) {

    }

    @Override
    public void userDisconnected(String username) {

    }

    @Override
    public void listLeaderCards(List<LeaderCard> leaderCardList, int cardsToChoose) {

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
