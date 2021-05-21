package it.polimi.ingsw.view;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentCardSlot;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.model.storage.Warehouse;
import it.polimi.ingsw.utils.Triple;

import java.util.List;
import java.util.Map;

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
     * @param playerBoard the PlayerBoard of the match to resume
     */
    void resumeMatch(PlayerBoard playerBoard);

    /**
     * Initializes the view and shows welcome screen
     */
    void init();

    /**
     * Asks the user to log in
     */
    void askLogin();

    /**
     * Shows a message that ask to wait for the other players before the match can start
     */
    void waitForOtherPlayers();

    /**
     * Notifies that someone connected to the current match
     * @param username the username of the connected user
     */
    void userConnected(String username);

    /**
     * Notifies that someone disconnected from the current match
     * @param username the username of the disconnected user
     */
    void userDisconnected(String username);

    /**
     * Shows a list of Leader Cards and asks to choose some of them
     * @param leaderCardList the list of Leader Cards to choose from
     * @param cardsToChoose the amount of cards to choose
     */
    void listLeaderCards(List<LeaderCard> leaderCardList,int cardsToChoose);

    /**
     * Shows the LeaderCards to the player
     * @param leaderCardList the LeaderCards to choose
     */
    void showPlayerLeaderCards(List<LeaderCard> leaderCardList);

    /**
     * Shows a list of Development Cards and asks to choose some of them
     * @param developmentCardList the list of Development Cards to choose from
     * @param cardsToChoose the amount of cards to choose
     * @param userBoard the Player Board of the user
     */
    void listDevelopmentCards(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard userBoard);

    /**
     * Shows the entire player board
     * @param playerBoard the player board to be shown
     */
    void showPlayerBoard(PlayerBoard playerBoard);

    /**
     * List the resources stored in the warehouse
     */
    void showWarehouse(Warehouse warehouse);

    /**
     * Asks to swap two depots
     * @param warehouse the user's warehouse, containing the depots to be swapped
     */
    void askToSwapDepots(Warehouse warehouse);

    /**
      * Shows the actions that the user can choose to perform
      * @param availableActions the array of available actions which the user can choose
      */
    void askForAction(List<String> usernames, Action... availableActions);

    /**
     * Asks which row or column of resources the users wants to take from the market
     * @param market the market to choose from
     */
    void takeResourcesFromMarket(Market market);

    /**
     * Shows the market
     * @param market the market to be shown
     */
    void showMarket(Market market);

    /**
     * Shows the resources gained from the market
     * @param resources the resources to be shown
     */
    void showResourcesGainedFromMarket(Resource[] resources);

    /**
     * Asks in which depots to store the resources
     * @param resource the resources to be stored
     * @param warehouse the warehouse where to store the resources
     */
    void askToStoreResource(Resource resource,Warehouse warehouse);

    /**
     * Asks which Leader Card the user wants to use to convert a white marble
     * @param leaderCard the first Leader Card
     * @param leaderCard1 the second Leader Card
     */
    void chooseWhiteMarbleConversion(LeaderCard leaderCard, LeaderCard leaderCard1);

    /**
     * Asks in which Development Card Slot to put a new Development Card
     * @param slots the Development Card Slots to choose from
     * @param developmentCard the Development Card to be stored
     */
    void askToChooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard);

    /**
     * Asks which production powers to activate
     * @param availableProductions the production powers to choose from
     * @param playerBoard the Player Board of the user
     */
    void chooseProductions(List<Producer> availableProductions,PlayerBoard playerBoard);

    /**
     * Shows who is the current player
     * @param username the username of the player to be shown
     */
    void showCurrentActiveUser(String username);

    /**
     * Asks to choose the initial resources
     * @param values the resources to choose from
     * @param resourcesToChoose the amount of resources to be chosen
     */
    void askToChooseStartResources(Resource[] values,int resourcesToChoose);

    void showPlayers(Map<String, Boolean> users);

}
