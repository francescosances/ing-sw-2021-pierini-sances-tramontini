package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import it.polimi.ingsw.model.Action;
import it.polimi.ingsw.model.EndGameException;
import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.SoloMatch;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.network.ClientHandler;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.view.VirtualView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameController implements PlayerStatusListener {

    /**
     * The match managed by the controller
     */
    private Match match;

    /**
     * A list containing the PlayerControllers of the players of the match
     */
    private final List<PlayerController> players;

    /**
     * An observer that catches all modifies to the match
     */
    private transient StatusObserver statusObserver;

    /**
     * Constructor that initialize a new match without players
     * @param matchName the name of the match that will be created
     */
    public GameController(String matchName, int playersNumber, StatusObserver statusObserver){
        if (playersNumber == 1)
            match = new SoloMatch(matchName);
        else
            match = new Match(matchName,playersNumber);
        players = new ArrayList<>();
        match.setCurrentPhase(Match.GamePhase.ADDING_PLAYERS);
        this.statusObserver = statusObserver;
    }

    /**
     * Method that map a message and an username with the the action that must be executed
     * @param message the message received from the client via socket
     * @param username the username of the user that sent the message
     */
    public void handleReceivedGameMessage(Message message, String username){
        Gson gson = new Gson();
        try {
            if(!username.equals(players.get(match.getCurrentPlayerIndex()).getUsername())){
                System.out.println("Invalid message received from "+username);
                return;
            }
            final PlayerController currentPlayerController = getPlayerController(username);
            switch (message.getType()) {
                case LEADER_CARDS_CHOICE:
                    leaderCardsChoice(username, Serializer.deserializeLeaderCardDeck(message.getData("leaderCards")));
                    break;
                case START_RESOURCES:
                    currentPlayerController.chooseStartResources(Serializer.deserializeResources(message.getData("resources")));
                    break;
                case PERFORM_ACTION:
                    currentPlayerController.performAction(gson.fromJson(message.getData("action"), Action.class));
                    break;
                case SWAP_DEPOTS:
                    int depotA = Integer.parseInt(message.getData("depotA"));
                    int depotB = Integer.parseInt(message.getData("depotB"));
                    currentPlayerController.swapDepots(depotA,depotB);
                    break;
                case SELECT_MARKET_ROW:
                    int row = Integer.parseInt(message.getData("row"));
                    currentPlayerController.selectMarketRow(row);
                    break;
                case SELECT_MARKET_COLUMN:
                    int column = Integer.parseInt(message.getData("column"));
                    currentPlayerController.selectMarketColumn(column);
                    break;
                case WHITE_MARBLE_CONVERSION:
                    currentPlayerController.chooseWhiteMarbleConversion(Integer.parseInt(message.getData("choice")));
                    break;
                case RESOURCE_TO_STORE:
                    currentPlayerController.storeResourceToWarehouse(Integer.parseInt(message.getData("choice")));
                    break;
                case DEVELOPMENT_CARDS_TO_BUY:
                    currentPlayerController.buyDevelopmentCard(Serializer.deserializeDevelopmentCardsList(message.getData("developmentCards")).get(0));
                    break;
                case CHOOSE_DEVELOPMENT_CARD_SLOT:
                    currentPlayerController.chooseDevelopmentCardSlot(Integer.parseInt(message.getData("slotIndex")));
                    break;
                case PRODUCTION:
                    currentPlayerController.chooseProductions(Serializer.deserializeRequirements(message.getData("costs")),Serializer.deserializeRequirements(message.getData("gains")));
                    break;
                case ROLLBACK:
                    currentPlayerController.performAction(Action.CANCEL);
                    break;
            }
        }catch (EndGameException e){
            setPhase(Match.GamePhase.END_GAME);
        }
    }

    /**
     * Starts the match ending the phase of adding players. The firt player is chosen randomly.
     */
    public void start(){
        match.setCurrentPlayerIndex((int) (Math.random() * players.size()));
        match.setStarted(true);
        for(int i=0;i<players.size();i++)
            players.get((match.getCurrentPlayerIndex()+i)%players.size()).setPlayerIndex(i);
        setPhase(Match.GamePhase.PLAYERS_SETUP);
    }

    /**
     * Submit the leader cards choice to the right player controller and move the turn to next player.
     * @param username the username of the user who chose the cards
     * @param leaderCards the cards chosen by the user
     */
    public void leaderCardsChoice(String username,List<LeaderCard> leaderCards){
        getPlayerController(username).chooseLeaderCards(leaderCards.toArray(new LeaderCard[]{}));
    }

    /**
     * Adds the player to the match and creates the instance of the relative player controller. An associated virtual view is also connected to the clientHandler.
     * Add this object as observer of the new Player Controller
     * @param username the username of the player to add
     * @param clientHandler the clientHandler that manage the socket connection with the client
     * @return the player controller containing the reference to the virtual view
     */
    public PlayerController addPlayer(String username, ClientHandler clientHandler){
        PlayerController playerController = getPlayerController(username);
        if(playerController != null){
            //Reactivating existing player
            playerController.setVirtualView(new VirtualView(clientHandler));
        }else{
            playerController = new PlayerController(username, match.addPlayer(username), new VirtualView(clientHandler));
            players.add(playerController);
        }
        playerController.addObserver(this);
        return playerController;
    }

    /**
     * Returns the player controller associated to the given username
     * @param username the username of the desired user
     * @return the player controller associated to the given username
     */
    public PlayerController getPlayerController(String username){
        for(PlayerController x : players)
            if(x.getUsername().equals(username))
                return x;
        return null;
    }

    /**
     * Returns the name of the match associated to this controller
     * @return the name of the match associated to this controller
     */
    public String getMatchName(){
        return match.getMatchName();
    }

    /**
     * Returns the number of players that have joined the match
     * @return the number of players that have joined the match
     */
    public int getJoinedPlayers(){
        return players.size();
    }

    /**
     * Returns the maximum number of players that can join the match
     * @return the maximum number of players that can join the match
     */
    public int getTotalPlayers(){
        return match.getMaxPlayersNumber();
    }

    /**
     * Returns true if the match has reached the maximum number of players and cannot accept another. Otherwhise it returns false.
     * @return true if the match has reached the maximum number of players and cannot accept another. Otherwhise it returns false.
     */
    public boolean isFull(){
        return match.isFull();
    }

    /**
     * Returns true if the match has already started
     * @return true if the match has already started
     */
    public boolean isStarted(){
        return match.isStarted();
    }

    /**
     * Returns the match associated to this controller
     * @return the match associated to this controller
     */
    public Match getMatch(){
        return match;
    }

    /**
     * Method that should be called everytime that the game phase or the current player are changed
     * Organizes the main game phases
     */
    protected void onStatusChanged(){
        switch (match.getCurrentPhase()){
            case PLAYERS_SETUP:
                if(match.getUsersReadyToPlay() == players.size()) {
                    setPhase(Match.GamePhase.TURN);
                    return;
                }
                players.get(match.getCurrentPlayerIndex()).setup();
                match.setUsersReadyToPlay(match.getUsersReadyToPlay()+1);
                showCurrentActiveUser();
                break;
            case TURN:
                players.get(match.getCurrentPlayerIndex()).startTurn();
                showCurrentActiveUser();
                break;
            case END_GAME:
                players.forEach(PlayerController::endGame);
                break;
        }
        statusObserver.onStatusChanged(this);
    }

    /**
     * Assures every non playing players receive a message showing whose turn is
     */
    private void showCurrentActiveUser(){
        for(int i=0;i<players.size();i++)
            if(i != match.getCurrentPlayerIndex())
                players.get(i).getVirtualView().showCurrentActiveUser(players.get(match.getCurrentPlayerIndex()).getUsername());
    }

    /**
     * Manages the main turn phases for the current player
     * @param player the player whose turn is
     */
    @Override
    public void onPlayerStatusChanged(PlayerController player) {
        System.out.println("The player "+player.getUsername()+" has changed his status to "+player.getCurrentStatus());
        final PlayerController playerController = players.get(match.getCurrentPlayerIndex());
        switch (player.getCurrentStatus()) {
            case TURN_ENDED:
                playerController.showPlayerBoard();
                match.endTurn();
                nextTurn();
                break;
            case PERFORMING_ACTION:
                playerController.askForAction();
                match.setCurrentPhase(Match.GamePhase.TURN);
                break;
            case ACTION_PERFORMED:
                playerController.startNormalAction();
                match.setCurrentPhase(Match.GamePhase.TURN);
                break;
        }
    }

    /**
     * Move the current player to the next one
     */
    public void nextTurn(){
        match.setCurrentPlayerIndex((match.getCurrentPlayerIndex()+1)%players.size());
        if(players.stream().noneMatch(PlayerController::isActive)) //TODO: No one is active (può succedere che si arrivi qui?)
            return;
        if(!players.get(match.getCurrentPlayerIndex()).isActive()) { // The current player is inactive
            broadcastMessage("The player: "+players.get(match.getCurrentPlayerIndex()).username+" is inactive. His turn has been skipped");
            if(match.getCurrentPhase() == Match.GamePhase.PLAYERS_SETUP)
                onStatusChanged();
            nextTurn();
        }
        onStatusChanged();
    }

    /**
     * Sends a message to all the users connected
     * @param message the message to be sent
     */
    protected void broadcastMessage(String message){
       players.forEach(player -> {player.getVirtualView().showMessage(message);});
    }

    /**
     * Allows to manually set the next phase
     * @param phase the phase currentPhase should turn into
     */
    protected void setPhase(Match.GamePhase phase){
        match.setCurrentPhase(phase);
        onStatusChanged();
    }

    /**
     * Activates the specified user and notify to the others that the user has been connected.
     * @param username the user to be connected
     */
    public void connect(String username){
        players.forEach((user)->{
            if(user.getUsername().equals(username))
                user.activate();
            else
                user.getVirtualView().userConnected(username);
        });
    }

    /**
     * Deactivates the specified user and notify to the others that the user has been disconnected.
     * @param username the user to be disconnected
     */
    public void disconnect(String username){
        players.forEach((user)->{
            if(user.getUsername().equals(username)) {
                user.deactivate();
                if(user.getCurrentStatus() == PlayerController.PlayerStatus.ACTION_PERFORMED)
                    user.turnEnded();
            }
            user.getVirtualView().userDisconnected(username);
        });
    }

    /**
     * Returns true if the user is currently active
     * @param username the user whose status is to be checked
     * @return true if the user is currently active else false
     */
    public boolean isConnected(String username){
        return players.stream().filter(x->x.getUsername().equals(username)).anyMatch(PlayerController::isActive);
    }

    //TODO:javadoc
    public List<PlayerController> getPlayers() {
        return players;
    }
}
