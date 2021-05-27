package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentCardSlot;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Strongbox;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

class FakeView implements View {

    PlayerControllerTest playerControllerTest;

    public FakeView(PlayerControllerTest playerControllerTest){
        this.playerControllerTest = playerControllerTest;
    }

    @Override
    public void showMessage(String message) {
        playerControllerTest.testMessage(message);
    }

    @Override
    public void showErrorMessage(String message) {
        playerControllerTest.testMessage(message);
    }

    @Override
    public void listLobbies(List<Triple<String, Integer, Integer>> availableLobbies) {
        playerControllerTest.testMessage(availableLobbies.toString());
    }

    @Override
    public void resumeMatch(PlayerBoard playerBoard) {
        playerControllerTest.testMessage(playerBoard.toString());
    }

    @Override
    public void init() {
    }

    @Override
    public void askLogin() {
    }

    @Override
    public void waitForOtherPlayers() {
    }

    @Override
    public void userConnected(String username) {
        playerControllerTest.testMessage(username);
    }

    @Override
    public void userDisconnected(String username) {
        playerControllerTest.testMessage(username);
    }

    @Override
    public void listLeaderCards(List<LeaderCard> leaderCardList, int cardsToChoose) {

    }

    @Override
    public void showPlayerLeaderCards(List<LeaderCard> leaderCardList) {
        playerControllerTest.testMessage(leaderCardList.toString());
    }

    @Override
    public void showLeaderCards(List<LeaderCard> leaderCards) {
        playerControllerTest.testMessage(leaderCards.toString());
    }

    @Override
    public void showDevelopmentCardSlots(DevelopmentCardSlot[] developmentCardSlots) {
        playerControllerTest.testMessage(Arrays.toString(developmentCardSlots));
    }

    @Override
    public void listDevelopmentCards(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard userBoard) {

    }

    @Override
    public void showPlayerBoard(PlayerBoard playerBoard) {
        playerControllerTest.testMessage(playerBoard.toString());

    }

    @Override
    public void showFaithTrack(FaithTrack faithTrack) {
        playerControllerTest.testMessage(faithTrack.toString());

    }

    @Override
    public void showVaticanReportTriggered(String username, int vaticanReportCount) {
        playerControllerTest.testMessage(username + vaticanReportCount);
    }

    @Override
    public void showWarehouse(Warehouse warehouse) {
        playerControllerTest.testMessage(warehouse.toString());

    }

    @Override
    public void showStrongbox(Strongbox strongbox) {
        playerControllerTest.testMessage(strongbox.toString());

    }

    @Override
    public void askToSwapDepots(Warehouse warehouse) {
        playerControllerTest.testMessage(warehouse.toString());

    }

    @Override
    public void askForAction(List<String> usernames, Action... availableActions) {
        playerControllerTest.testMessage(usernames.toString()
                + Arrays.toString(availableActions));
    }

    @Override
    public void takeResourcesFromMarket(Market market) {
        playerControllerTest.testMessage(market.toString());

    }

    @Override
    public void showMarket(Market market) {
        playerControllerTest.testMessage(market.toString());

    }

    @Override
    public void showResourcesGainedFromMarket(Resource[] resources) {
        playerControllerTest.testMessage(Arrays.toString(resources));

    }

    @Override
    public void askToStoreResource(Resource resource, Warehouse warehouse) {
        playerControllerTest.testMessage(resource.toString() + warehouse.toString());

    }

    @Override
    public void chooseWhiteMarbleConversion(LeaderCard leaderCard, LeaderCard leaderCard1) {
        playerControllerTest.testMessage(leaderCard.toString() + leaderCard1.toString());

    }

    @Override
    public void askToChooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard) {
        playerControllerTest.testMessage(Arrays.toString(slots) + developmentCard.toString());

    }

    @Override
    public void chooseProductions(List<Producer> availableProductions, PlayerBoard playerBoard) {
        playerControllerTest.testMessage(availableProductions.toString() + playerBoard.toString());
    }

    @Override
    public void showCurrentActiveUser(String username) {
        playerControllerTest.testMessage(username);

    }

    @Override
    public void askToChooseStartResources(Resource[] values, int resourcesToChoose) {
        playerControllerTest.testMessage(Arrays.toString(values) + resourcesToChoose);
    }

    @Override
    public void showPlayers(Map<String, Boolean> users) {
        playerControllerTest.testMessage(users.toString());
    }

    @Override
    public void showActionToken(ActionToken actionToken) {
        playerControllerTest.testMessage(actionToken.toString());

    }

    @Override
    public void showProducerUser() {
    }

    @Override
    public String getUsername() {
        return "FakeView";
    }
}
