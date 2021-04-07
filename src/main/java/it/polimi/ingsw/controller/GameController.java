package it.polimi.ingsw.controller;

import it.polimi.ingsw.model.Match;
import it.polimi.ingsw.model.SoloMatch;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.network.ClientHandler;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Message;
import it.polimi.ingsw.view.VirtualView;

import java.util.ArrayList;
import java.util.List;

public class GameController {

    private Match match;
    private final List<PlayerController> players;

    private int currentPlayerIndex;
    private GamePhase currentPhase;

    public GameController(String matchName){
        match = new Match(matchName);
        players = new ArrayList<>();
        currentPhase = GamePhase.ADDING_PLAYERS;
    }

    public void handleReceivedGameMessage(Message message, String username){
        switch (message.getType()) {
            case START_MATCH:
                this.start();
                break;
            case LEADER_CARDS_CHOICE:
                this.leaderCardsChoice(username, Serializer.deserializeLeaderCards(message.getData("leaderCards")));
                break;
        }
    }

    public void start(){
        if(this.players.size() == 1) {
            this.match = new SoloMatch(match.getMatchName());
            match.addPlayer(players.get(0).getUsername());//TODO: gestire salvataggio istanza match single player
        }
        currentPlayerIndex = (int) (Math.random() * players.size());
        nextPhase();
        System.out.println("PARTITA AVVIATA");
    }

    public void leaderCardsChoice(String username,List<LeaderCard> leaderCards){
        getPlayerController(username).chooseLeaderCards(leaderCards.toArray(new LeaderCard[]{}));
        nextTurn();
    }

    public PlayerController addPlayer(String username, ClientHandler clientHandler){
        PlayerController playerController = getPlayerController(username);
        if(playerController != null){
            //Reactivating existing player
            playerController.setVirtualView(new VirtualView(clientHandler));
        }else{
            playerController = new PlayerController(username, match.addPlayer(username), new VirtualView(clientHandler));
            players.add(playerController);
        }
        this.connect(username);
        return playerController;
    }

    public PlayerController getPlayerController(String username){
        for(PlayerController x : players)
            if(x.getUsername().equals(username))
                return x;
        return null;
    }

    public String getMatchName(){
        return match.getMatchName();
    }

    public int getJoinedPlayers(){
        return players.size();
    }

    public int getTotalPlayers(){
        return Match.MAX_PLAYERS;
    }

    public boolean isFull(){
        return match.isFull();
    }

    public Match getMatch(){
        return match;
    }

    protected void onStatusChanged(){
        switch (currentPhase){
            case PLAYERS_SETUP:
                players.get(currentPlayerIndex).drawLeaderCards();
                break;
        }
    }

    public void nextTurn(){
        players.get(currentPlayerIndex).turnEnded();
        currentPlayerIndex = (currentPlayerIndex+1)%players.size();
        if(players.stream().noneMatch(PlayerController::isActive))//No one is active
            return;
        if(!players.get(currentPlayerIndex).isActive())
            nextTurn();
        players.get(currentPlayerIndex).yourTurn();
        currentPhase.incrementCurrentPhasePlayers();
        if(currentPhase.getCurrentPhasePlayers() >= players.size())
            currentPhase = currentPhase.next();
        onStatusChanged();
    }

    protected void nextPhase(){
        this.currentPhase = this.currentPhase.next();
        onStatusChanged();
    }

    public enum GamePhase {
        ADDING_PLAYERS,
        PLAYERS_SETUP,
        //TODO: aggiungere fase intermedia per scartare carte leader / spostare risorse
        TURN,
        END_GAME;

        /**
         * Counter that represent the number of players that have finished the current phase
         */
        private int currentPhasePlayers = 0;

        public int getCurrentPhasePlayers(){return currentPhasePlayers;}

        public void incrementCurrentPhasePlayers(){
            this.currentPhasePlayers++;
        }

        private static final GamePhase[] vals = values();

        /**
         * Returns the next phase
         * @return a GamePhase object representing the phase next to this
         */
        public GamePhase next()
        {
            this.currentPhasePlayers = 0;
            return vals[(this.ordinal()+1) % vals.length];
        }
    }

    public void connect(String username){
        players.forEach((user)->{
            if(user.getUsername().equals(username)) {
                user.activate();
            }
            user.getVirtualView().userConnected(username);
        });
    }

    public void disconnect(String username){
        players.forEach((user)->{
            if(user.getUsername().equals(username)) {
                user.deactivate();
            }
            user.getVirtualView().userDisconnected(username);
        });
    }

    public boolean isConnected(String username){
        return players.stream().filter(x->x.getUsername().equals(username)).anyMatch(PlayerController::isActive);
    }
}
