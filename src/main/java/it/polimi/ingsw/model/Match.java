package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.utils.FileManager;
import it.polimi.ingsw.view.ObservableFromView;
import it.polimi.ingsw.view.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Match implements ObservableFromView {

    public static final int MAX_PLAYERS = 4;

    private final int maxPlayersNumber;

    protected Market market;

    protected List<PlayerBoard> players;

    protected List<Deck<DevelopmentCard>> developmentDecks;

    protected Deck<LeaderCard> leaderCards;

    private int vaticanReportsCount;

    protected String matchName;

    public static final String YOU_STRING = "You";

    /**
     * The index of the list players representing the active player
     */
    private int currentPlayerIndex;

    /**
     * The number of users that have already chosen the leader cards and resources of their choice
     */
    private int usersReadyToPlay = 0;

    /**
     * True if the match has already started
     */
    private boolean started = false;

    /**
     * The current phase of the match
     */
    private GamePhase currentPhase;

    protected transient List<View> views;

    public Match(String matchName) {
        this(matchName,MAX_PLAYERS);
    }

    public Match(String matchName,int maxPlayersNumber) {
        this.players = new ArrayList<>();
        this.market = new Market();
        this.developmentDecks = new ArrayList<>();
        try {
            this.developmentDecks = generateDevelopmentCards();
            this.leaderCards = generateLeaderCards();
        }catch (Exception e){
            e.printStackTrace();
            throw new IllegalStateException("Unable to create new match: error while creating cards decks");
        }
        this.vaticanReportsCount = 0;
        this.matchName = matchName;
        this.maxPlayersNumber = maxPlayersNumber;
        views = new ArrayList<>();
    }

    public PlayerBoard getPlayerBoard(String username){
        for(PlayerBoard x: players){
            if(x.getUsername().equals(username))
                return x;
        }
        throw new IllegalArgumentException("Invalid username");
    }

    private static List<Deck<DevelopmentCard>> generateDevelopmentCards() throws IOException {
        List<Deck<DevelopmentCard>> ret = FileManager.getInstance().readDevelopmentCardsDecks();
        ret.forEach(Deck::shuffle);
        return ret;
    }

    private static Deck<LeaderCard> generateLeaderCards() throws IOException {
        Deck<LeaderCard> ret = FileManager.getInstance().readLeaderCards();
        ret.shuffle();
        return ret;
    }

    public Deck<DevelopmentCard> getDevelopmentCardDeck(DevelopmentColorType color, int level){
        for (Deck<DevelopmentCard> deck:developmentDecks) {
            if (!deck.isEmpty() && deck.top().getColor() == color && deck.top().getLevel() == level)
                return deck;
        }
        return new Deck<>();
    }

    public List<LeaderCard> drawLeaderCards(int num) {
        List<LeaderCard> ret = new ArrayList<>();
        for(int i=0;i<num;i++)
            ret.add(leaderCards.get(i));
        return ret;
    }

    public void chooseLeaderCard(LeaderCard chosenCard){
        Iterator<LeaderCard> cardIterator = leaderCards.iterator();
        while(cardIterator.hasNext()){
            if(cardIterator.next().equals(chosenCard))
                cardIterator.remove();
        }
    }

    public boolean containsUsername(String username){
        return players.stream().anyMatch(playerBoard -> playerBoard.getUsername().equals(username));
    }

    public PlayerBoard addPlayer(String username){
        if(players.size() >= maxPlayersNumber)
            throw new IllegalStateException("Maximum number of players reached");
        if(containsUsername(username))
            throw new IllegalArgumentException("Username already inserted");
        PlayerBoard temp = new PlayerBoard(username,this);
        players.add(temp);
        return temp;
    }

    public int getUsersReadyToPlay() {
        return usersReadyToPlay;
    }

    public void setUsersReadyToPlay(int usersReadyToPlay) {
        this.usersReadyToPlay = usersReadyToPlay;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
        players.get(currentPlayerIndex).setInkwell();
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean matchStarted) {
        this.started = matchStarted;
    }

    public Market getMarket(){
        return market;
    }

    public void discardResource(PlayerBoard player) throws EndGameException {
        for(PlayerBoard x : players){
            if(!x.equals(player))
                x.getFaithTrack().moveMarker();
        }
    }

    public int getVaticanReportsCount() {
        return vaticanReportsCount;
    }

    public boolean vaticanReport(int popeSpace){
        if(!players.isEmpty() && players.get(0).getFaithTrack().isValidVaticanReport(popeSpace)) {
            for (PlayerBoard p : players) {
                if (p.getFaithTrack().getFaithMarker() >= popeSpace - (3 + vaticanReportsCount))
                    p.getFaithTrack().uncoverPopeFavorTile(vaticanReportsCount);
                else
                    p.getFaithTrack().discardPopeFavorTile(vaticanReportsCount);
                p.getFaithTrack().vaticanReportTriggered(vaticanReportsCount);
            }
            vaticanReportsCount++;
            return true;
        }
        return false;
    }

    public String getMatchName(){
        return matchName;
    }

    public boolean isFull(){
        return players.size() == maxPlayersNumber;
    }

    public int getMaxPlayersNumber() {
        return maxPlayersNumber;
    }

    public void endTurn(){
        int nextPlayerIndex = (currentPlayerIndex + 1) % players.size();
        for (View view:views) {
            if (players.get(nextPlayerIndex).getUsername().equals(view.getUsername()))
                updatePlayerBoard(players.get(nextPlayerIndex));
        }
    }

    public Deck<LeaderCard> getLeaderCards() {
        return leaderCards;
    }

    public List<Deck<DevelopmentCard>> getDevelopmentCardDecks() {
        return developmentDecks;
    }

    protected void buyDevelopmentCard(DevelopmentCard developmentCard, PlayerBoard player){
        for(Deck<DevelopmentCard> deck : developmentDecks){
            for(int i=0;i<deck.size();i++){
                if(deck.get(i).equals(developmentCard)) {
                    deck.remove(i);
                    return;
                }
            }
        }
    }

    public List<PlayerBoard> getPlayers() {
        return players;
    }

    @Override
    public void addView(View view) {
        if (views == null)
            views = new ArrayList<>();
        views.add(view);
        for (PlayerBoard playerBoard:players) {
            playerBoard.addView(view);
            for (View otherView:views)
                playerBoard.addView(otherView);
        }
        market.addView(view);
    }

    @Override
    public void removeView(View view) {
        views.remove(view);
        players.forEach(playerBoard -> playerBoard.removeView(view));
        market.removeView(view);
    }

    private void updatePlayerBoard(PlayerBoard playerBoard) {
        for (View view:views)
            view.showPlayerBoard(playerBoard);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match that = (Match) o;
        return  vaticanReportsCount == that.vaticanReportsCount &&
                market.equals(that.market) &&
                players.equals(that.players) &&
                developmentDecks.equals(that.developmentDecks) &&
                leaderCards.equals(that.leaderCards) &&
                matchName.equals(that.matchName);
    }

    /**
     * Returns the hash code of the object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public void setCurrentPhase(GamePhase phase) {
        this.currentPhase = phase;
    }

    public enum GamePhase {
        ADDING_PLAYERS,
        PLAYERS_SETUP,
        TURN,
        END_GAME
    }
}
