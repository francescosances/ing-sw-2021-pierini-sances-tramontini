package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Strongbox;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ViewStub implements View {
    
    List<Object> messages;

    public ViewStub() {
        messages = new ArrayList<>();
    }

    public Object popMessage() {
        return messages.remove(0);
    }

    public boolean isEmpty(){
        return messages.isEmpty();
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
        messages.add(availableLobbies);
    }

    @Override
    public void resumeMatch(PlayerBoard playerBoard) {
        messages.add(playerBoard);
    }

    @Override
    public void init() {
    }

    @Override
    public void askLogin() {}

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
        messages.add(leaderCardList);
        messages.add(cardsToChoose);
    }

    @Override
    public void showPlayerLeaderCards(List<LeaderCard> leaderCardList) {
        messages.add(leaderCardList);
    }

    @Override
    public void showLeaderCards(List<LeaderCard> leaderCards) {
        messages.add(leaderCards);
    }

    @Override
    public void showDevelopmentCardSlots(DevelopmentCardSlot[] developmentCardSlots) {
        messages.add(developmentCardSlots);
    }

    @Override
    public void listDevelopmentCards(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard userBoard) {
        messages.add(developmentCardList);
        messages.add(cardsToChoose);
        messages.add(userBoard);
    }

    @Override
    public void showPlayerBoard(PlayerBoard playerBoard) {
        messages.add(playerBoard);
    }

    @Override
    public void showFaithTrack(FaithTrack faithTrack) {
        messages.add(faithTrack);

    }

    @Override
    public void showVaticanReportTriggered(String username, int vaticanReportCount) {
        messages.add(username);
        messages.add(vaticanReportCount);
    }

    @Override
    public void showWarehouse(Warehouse warehouse) {
        messages.add(warehouse);

    }

    @Override
    public void showStrongbox(Strongbox strongbox) {
        messages.add(strongbox);

    }

    @Override
    public void askToSwapDepots(Warehouse warehouse) {
        messages.add(warehouse);

    }

    @Override
    public void askForAction(List<String> usernames, Action... availableActions) {
        messages.add(usernames);
        messages.add(availableActions);
    }

    @Override
    public void takeResourcesFromMarket(Market market) {
        messages.add(market);

    }

    @Override
    public void showMarket(Market market) {
        messages.add(market);

    }

    @Override
    public void showResourcesGainedFromMarket(Resource[] resources) {
        messages.add(resources);

    }

    @Override
    public void askToStoreResource(Resource resource, Warehouse warehouse) {
        messages.add(resource);
        messages.add(warehouse);
    }

    @Override
    public void chooseWhiteMarbleConversion(LeaderCard leaderCard, LeaderCard leaderCard1) {
        messages.add(leaderCard);
        messages.add(leaderCard1);
    }

    @Override
    public void askToChooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard) {
        messages.add(slots);
        messages.add(developmentCard);

    }

    @Override
    public void chooseProductions(List<Producer> availableProductions, PlayerBoard playerBoard) {
        messages.add(availableProductions);
        messages.add(playerBoard);
    }

    @Override
    public void askToChooseProductionCosts(Requirements requirements) {
        messages.add(requirements);
    }

    @Override
    public void askToChooseProductionGains(Requirements requirements) {
        messages.add(requirements);
    }

    @Override
    public void showCurrentActiveUser(String username) {
        messages.add(username);

    }

    @Override
    public void askToChooseStartResources(Resource[] values, int resourcesToChoose) {
        messages.add(values);
        messages.add(resourcesToChoose);
    }

    @Override
    public void showPlayers(Map<String, Boolean> users) {
        messages.add(users);
    }

    @Override
    public void showActionToken(ActionToken actionToken) {
        messages.add(actionToken);

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
        messages.add(playerList);
    }

    @Override
    public String getUsername() {
        return "ViewStub";
    }
}
