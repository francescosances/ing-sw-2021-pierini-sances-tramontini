package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Strongbox;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

class ViewStub implements View {
    
    List<String> messages;

    public ViewStub() {
        messages = new ArrayList<>();
    }

    public String popMessage() {
        return messages.remove(0);
    }

    @Override
    public void showMessage(String message) {
        messages.add(message);
    }

    @Override
    public void showErrorMessage(String message) {
        messages.add(message);
    }

    @Override
    public void listLobbies(List<Triple<String, Integer, Integer>> availableLobbies) {
        messages.add(availableLobbies.toString());
    }

    @Override
    public void resumeMatch(PlayerBoard playerBoard) {
        messages.add(playerBoard.toString());
    }

    @Override
    public void init() {
    }

    @Override
    public void askLogin() {
        messages.add("a");
    }

    @Override
    public void waitForOtherPlayers() {
    }

    @Override
    public void userConnected(String username) {
        messages.add(username);
    }

    @Override
    public void userDisconnected(String username) {
        messages.add(username);
    }

    @Override
    public void listLeaderCards(List<LeaderCard> leaderCardList, int cardsToChoose) {

    }

    @Override
    public void showPlayerLeaderCards(List<LeaderCard> leaderCardList) {
        messages.add(leaderCardList.toString());
    }

    @Override
    public void showLeaderCards(List<LeaderCard> leaderCards) {
        messages.add(leaderCards.toString());
    }

    @Override
    public void showDevelopmentCardSlots(DevelopmentCardSlot[] developmentCardSlots) {
        messages.add(Arrays.toString(developmentCardSlots));
    }

    @Override
    public void listDevelopmentCards(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard userBoard) {

    }

    @Override
    public void showPlayerBoard(PlayerBoard playerBoard) {
        messages.add(playerBoard.toString());

    }

    @Override
    public void showFaithTrack(FaithTrack faithTrack) {
        messages.add(faithTrack.toString());

    }

    @Override
    public void showVaticanReportTriggered(String username, int vaticanReportCount) {
        messages.add(username + vaticanReportCount);
    }

    @Override
    public void showWarehouse(Warehouse warehouse) {
        messages.add(warehouse.toString());

    }

    @Override
    public void showStrongbox(Strongbox strongbox) {
        messages.add(strongbox.toString());

    }

    @Override
    public void askToSwapDepots(Warehouse warehouse) {
        messages.add(warehouse.toString());

    }

    @Override
    public void askForAction(List<String> usernames, Action... availableActions) {
        messages.add(usernames.toString()
                + Arrays.toString(availableActions));
    }

    @Override
    public void takeResourcesFromMarket(Market market) {
        messages.add(market.toString());

    }

    @Override
    public void showMarket(Market market) {
        messages.add(market.toString());

    }

    @Override
    public void showResourcesGainedFromMarket(Resource[] resources) {
        messages.add(Arrays.toString(resources));

    }

    @Override
    public void askToStoreResource(Resource resource, Warehouse warehouse) {
        messages.add(resource.toString() + warehouse.toString());

    }

    @Override
    public void chooseWhiteMarbleConversion(LeaderCard leaderCard, LeaderCard leaderCard1) {
        messages.add(leaderCard.toString() + leaderCard1.toString());

    }

    @Override
    public void askToChooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard) {
        messages.add(Arrays.toString(slots) + developmentCard.toString());

    }

    @Override
    public void chooseProductions(List<Producer> availableProductions, PlayerBoard playerBoard) {
        messages.add(availableProductions.toString() + playerBoard.toString());
    }

    @Override
    public void askToChooseProductionCosts(Requirements requirements) {

    }

    @Override
    public void askToChooseProductionGains(Requirements requirements) {

    }

    @Override
    public void showCurrentActiveUser(String username) {
        messages.add(username);

    }

    @Override
    public void askToChooseStartResources(Resource[] values, int resourcesToChoose) {
        messages.add(Arrays.toString(values) + resourcesToChoose);
    }

    @Override
    public void showPlayers(Map<String, Boolean> users) {
        messages.add(users.toString());
    }

    @Override
    public void showActionToken(ActionToken actionToken) {
        messages.add(actionToken.toString());

    }

    @Override
    public void showProducerUser() {
    }

    @Override
    public void actionPerformed() {
    }

    @Override
    public void showEndGameTriggered() {

    }

    @Override
    public void showCharts(List<PlayerBoard> playerList) {
        messages.add(playerList.toString());
    }

    @Override
    public String getUsername() {
        return "ViewStub";
    }
}
