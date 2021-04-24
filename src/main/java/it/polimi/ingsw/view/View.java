package it.polimi.ingsw.view;

import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.Market;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.PlayerBoard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Triple;

import java.util.List;

public interface View {

    /**
     * Shows a generic message
     * @param message the message to be shown
     */
    void showMessage(String message);

    /**
     * Shows an error message
     * @param message the error message to be shown
     */
    void showErrorMessage(String message);

    /**
     * Shows the list of available lobbies
     * @param availableLobbies a list of lobbies that the user can join.
     *                         Each element of the list is a Triple composed as followed:
     *                         - first: the name of the match
     *                         - second: the number of players that have already joined the match
     *                         - third: the maximum number of players that can join the match
     */
    void listLobbies(List<Triple<String, Integer, Integer>> availableLobbies);


    /**
     * Reset the view to the state indicated by the match object
     * @param match the match to resume
     */
    void resumeMatch(Match match);


    void yourTurn();

    void init();

    void askLogin();

    void userConnected(String username);

    void userDisconnected(String username);

    void listLeaderCards(List<LeaderCard> leaderCardList,int cardsToChoose);

    /**
     * Shows the entire player board
     * @param playerBoard the player board to be shown
     */
    void showPlayerBoard(PlayerBoard playerBoard);

    /**
     * List the resources stored in the warehouse
     */
     void showWarehouseStatus(Warehouse warehouse);

     void askToSwapDepots(Warehouse warehouse);

    void askToChooseMarketRowOrColumn(Market market);

    /**
      * Shows the actions that the user can choose to perform
      * @param availableActions the array of available actions which the user can choose
      */
    void askForAction(Action... availableActions);

    void takeResourcesFromMarket(Market market);

    void showMarketStatus(Market market);

    void showResources(Resource[] resources);

    void askToStoreResource(Resource resource,Warehouse warehouse);

    void chooseWhiteMarbleConversion(LeaderCard leaderCard, LeaderCard leaderCard1);
}
