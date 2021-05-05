package it.polimi.ingsw.model;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.NonPhysicalResourceType;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.Triple;

import java.util.*;

public class Match {

    public static final int MAX_PLAYERS = 4;

    private int maxPlayersNumber;

    protected Market market;

    protected List<PlayerBoard> players;

    protected List<Deck<DevelopmentCard>> developmentDecks;

    protected Deck<LeaderCard> leaderCards;

    private int vaticanReportsCount;

    protected String matchName;

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

    public Match(String matchName){
        this(matchName,MAX_PLAYERS);
    }

    public Match(String matchName,int maxPlayersNumber){
        this.players = new ArrayList<>();
        this.market = new Market();
        this.developmentDecks = new ArrayList<>();
        this.developmentDecks = generateDevelopmentCards();
        this.leaderCards = generateLeaderCards();
        this.vaticanReportsCount = 0;
        this.matchName = matchName;
        this.maxPlayersNumber = maxPlayersNumber;
    }

    public PlayerBoard getPlayerBoard(String username){
        for(PlayerBoard x: players){
            if(x.username.equals(username))
                return x;
        }
        throw new IllegalArgumentException("Invalid username");
    }

    private static List<Deck<DevelopmentCard>> generateDevelopmentCards(){
        List<Deck<DevelopmentCard>> ret = new ArrayList<>();


        Deck<DevelopmentCard> green1 = new Deck<>();
        green1.add(new DevelopmentCard(1,new Requirements(new Pair<>(ResourceType.SHIELD,2)),1,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.COIN,1)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        green1.add(new DevelopmentCard(2,new Requirements(new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.SERVANT,1),new Pair<>(ResourceType.STONE,1)),1,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.STONE,1)),new Pair<>(ResourceType.SERVANT,1)));
        green1.add(new DevelopmentCard(3,new Requirements(new Pair<>(ResourceType.SHIELD,3)),1,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.SERVANT,2)),new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.STONE,1)));
        green1.add(new DevelopmentCard(4,new Requirements(new Pair<>(ResourceType.SHIELD,2),new Pair<>(ResourceType.COIN,2)),1,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.SERVANT,1),new Pair<>(ResourceType.STONE,1)),new Pair<>(ResourceType.COIN,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        green1.shuffle();
        ret.add(green1);

        Deck<DevelopmentCard> green2 = new Deck<>();
        green2.add(new DevelopmentCard(5,new Requirements(new Pair<>(ResourceType.SHIELD,4)),2,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.STONE,1)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2)));
        green2.add(new DevelopmentCard(6,new Requirements(new Pair<>(ResourceType.SHIELD,3),new Pair<>(ResourceType.SERVANT,2)),2,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.SERVANT,1)),new Pair<>(ResourceType.STONE,3)));
        green2.add(new DevelopmentCard(7,new Requirements(new Pair<>(ResourceType.SHIELD,5)),2,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.COIN,2)),new Pair<>(ResourceType.STONE,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2)));
        green2.add(new DevelopmentCard(8,new Requirements(new Pair<>(ResourceType.SHIELD,3),new Pair<>(ResourceType.COIN,3)),2,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.COIN,1)),new Pair<>(ResourceType.SHIELD,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        green2.shuffle();
        ret.add(green2);

        Deck<DevelopmentCard> green3 = new Deck<>();
        green3.add(new DevelopmentCard(9,new Requirements(new Pair<>(ResourceType.SHIELD,6)),3,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.COIN,2)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2),new Pair<>(ResourceType.STONE,3)));
        green3.add(new DevelopmentCard(10,new Requirements(new Pair<>(ResourceType.SHIELD,5),new Pair<>(ResourceType.SERVANT,2)),3,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SERVANT,1)),new Pair<>(ResourceType.SHIELD,2),new Pair<>(ResourceType.STONE,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        green3.add(new DevelopmentCard(11,new Requirements(new Pair<>(ResourceType.SHIELD,7)),3,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.SERVANT,1)),new Pair<>(ResourceType.COIN,1),new Pair<>(NonPhysicalResourceType.FAITH_POINT,3)));
        green3.add(new DevelopmentCard(12,new Requirements(new Pair<>(ResourceType.SHIELD,4),new Pair<>(ResourceType.COIN,4)),3,DevelopmentColorType.GREEN,new Requirements(new Pair<>(ResourceType.STONE,1)),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.COIN,3)));
        green3.shuffle();
        ret.add(green3);

        Deck<DevelopmentCard> blue1 = new Deck<>();
        blue1.add(new DevelopmentCard(1,new Requirements(new Pair<>(ResourceType.COIN,2)),1,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.SHIELD,1)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        blue1.add(new DevelopmentCard(2,new Requirements(new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SERVANT,1),new Pair<>(ResourceType.STONE,1)),1,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.SERVANT,1)),new Pair<>(ResourceType.STONE,1)));
        blue1.add(new DevelopmentCard(3,new Requirements(new Pair<>(ResourceType.COIN,3)),1,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.STONE,2)),new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.SERVANT,1)));
        blue1.add(new DevelopmentCard(4,new Requirements(new Pair<>(ResourceType.COIN,2),new Pair<>(ResourceType.SERVANT,2)),1,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.STONE,1),new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.SERVANT,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        blue1.shuffle();
        ret.add(blue1);

        Deck<DevelopmentCard> blue2 = new Deck<>();
        blue2.add(new DevelopmentCard(5,new Requirements(new Pair<>(ResourceType.COIN,4)),2,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.SERVANT,1)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2)));
        blue2.add(new DevelopmentCard(6,new Requirements(new Pair<>(ResourceType.COIN,3),new Pair<>(ResourceType.STONE,2)),2,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.STONE,1)),new Pair<>(ResourceType.SERVANT,3)));
        blue2.add(new DevelopmentCard(7,new Requirements(new Pair<>(ResourceType.COIN,5)),2,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.SERVANT,2)),new Pair<>(ResourceType.SHIELD,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2)));
        blue2.add(new DevelopmentCard(8,new Requirements(new Pair<>(ResourceType.COIN,3),new Pair<>(ResourceType.STONE,3)),2,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.SERVANT,1)),new Pair<>(ResourceType.STONE,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        blue2.shuffle();
        ret.add(blue2);

        Deck<DevelopmentCard> blue3 = new Deck<>();
        blue3.add(new DevelopmentCard(9,new Requirements(new Pair<>(ResourceType.COIN,6)),3,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.SERVANT,2)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2),new Pair<>(ResourceType.SHIELD,3)));
        blue3.add(new DevelopmentCard(10,new Requirements(new Pair<>(ResourceType.COIN,5),new Pair<>(ResourceType.STONE,2)),3,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.SERVANT,2),new Pair<>(ResourceType.STONE,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        blue3.add(new DevelopmentCard(11,new Requirements(new Pair<>(ResourceType.COIN,7)),3,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.STONE,1)),new Pair<>(ResourceType.SHIELD,1),new Pair<>(NonPhysicalResourceType.FAITH_POINT,3)));
        blue3.add(new DevelopmentCard(12,new Requirements(new Pair<>(ResourceType.COIN,4),new Pair<>(ResourceType.STONE,4)),3,DevelopmentColorType.BLUE,new Requirements(new Pair<>(ResourceType.SERVANT,1)),new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SHIELD,3)));
        blue3.shuffle();
        ret.add(blue3);

        Deck<DevelopmentCard> yellow1 = new Deck<>();
        yellow1.add(new DevelopmentCard(1,new Requirements(new Pair<>(ResourceType.STONE,2)),1,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SERVANT,1)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        yellow1.add(new DevelopmentCard(2,new Requirements(new Pair<>(ResourceType.STONE,1),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.COIN,1)),1,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.COIN,1)));
        yellow1.add(new DevelopmentCard(3,new Requirements(new Pair<>(ResourceType.STONE,3)),1,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SHIELD,2)),new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.STONE,1),new Pair<>(ResourceType.SERVANT,1)));
        yellow1.add(new DevelopmentCard(4,new Requirements(new Pair<>(ResourceType.STONE,2),new Pair<>(ResourceType.SHIELD,2)),1,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SERVANT,1)),new Pair<>(ResourceType.SHIELD,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        yellow1.shuffle();
        ret.add(yellow1);

        Deck<DevelopmentCard> yellow2 = new Deck<>();
        yellow2.add(new DevelopmentCard(5,new Requirements(new Pair<>(ResourceType.STONE,4)),2,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SHIELD,1)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2)));
        yellow2.add(new DevelopmentCard(6,new Requirements(new Pair<>(ResourceType.STONE,3),new Pair<>(ResourceType.SHIELD,2)),2,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.STONE,1)),new Pair<>(ResourceType.COIN,3)));
        yellow2.add(new DevelopmentCard(7,new Requirements(new Pair<>(ResourceType.STONE,5)),2,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SHIELD,2)),new Pair<>(ResourceType.SERVANT,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2)));
        yellow2.add(new DevelopmentCard(8,new Requirements(new Pair<>(ResourceType.STONE,3),new Pair<>(ResourceType.SERVANT,3)),2,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.COIN,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        yellow2.shuffle();
        ret.add(yellow2);

        Deck<DevelopmentCard> yellow3 = new Deck<>();
        yellow3.add(new DevelopmentCard(9,new Requirements(new Pair<>(ResourceType.STONE,6)),3,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SHIELD,2)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2),new Pair<>(ResourceType.SERVANT,3)));
        yellow3.add(new DevelopmentCard(10,new Requirements(new Pair<>(ResourceType.STONE,5),new Pair<>(ResourceType.SERVANT,2)),3,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.STONE,1),new Pair<>(ResourceType.SERVANT,1)),new Pair<>(ResourceType.COIN,2),new Pair<>(ResourceType.SHIELD,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        yellow3.add(new DevelopmentCard(11,new Requirements(new Pair<>(ResourceType.STONE,7)),3,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.SERVANT,1),new Pair<>(NonPhysicalResourceType.FAITH_POINT,3)));
        yellow3.add(new DevelopmentCard(12,new Requirements(new Pair<>(ResourceType.STONE,4),new Pair<>(ResourceType.SERVANT,4)),3,DevelopmentColorType.YELLOW,new Requirements(new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.STONE,1),new Pair<>(ResourceType.SERVANT,3)));
        yellow3.shuffle();
        ret.add(yellow3);

        Deck<DevelopmentCard> purple1 = new Deck<>();
        purple1.add(new DevelopmentCard(1,new Requirements(new Pair<>(ResourceType.SERVANT,2)),1,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.STONE,1)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        purple1.add(new DevelopmentCard(2,new Requirements(new Pair<>(ResourceType.SERVANT,1),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.COIN,1)),1,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.COIN,1)),new Pair<>(ResourceType.SHIELD,1)));
        purple1.add(new DevelopmentCard(3,new Requirements(new Pair<>(ResourceType.SERVANT,3)),1,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.COIN,2)),new Pair<>(ResourceType.SHIELD,1),new Pair<>(ResourceType.STONE,1),new Pair<>(ResourceType.SERVANT,1)));
        purple1.add(new DevelopmentCard(4,new Requirements(new Pair<>(ResourceType.SERVANT,2),new Pair<>(ResourceType.STONE,2)),1,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.STONE,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        purple1.shuffle();
        ret.add(purple1);

        Deck<DevelopmentCard> purple2 = new Deck<>();
        purple2.add(new DevelopmentCard(5,new Requirements(new Pair<>(ResourceType.SERVANT,4)),2,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.COIN,1)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2)));
        purple2.add(new DevelopmentCard(6,new Requirements(new Pair<>(ResourceType.SERVANT,3),new Pair<>(ResourceType.COIN,2)),2,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.COIN,1),new Pair<>(ResourceType.SERVANT,1)),new Pair<>(ResourceType.SHIELD,3)));
        purple2.add(new DevelopmentCard(7,new Requirements(new Pair<>(ResourceType.SERVANT,5)),2,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.STONE,2)),new Pair<>(ResourceType.COIN,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2)));
        purple2.add(new DevelopmentCard(8,new Requirements(new Pair<>(ResourceType.SERVANT,3),new Pair<>(ResourceType.SHIELD,3)),2,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.SERVANT,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        purple2.shuffle();
        ret.add(purple2);

        Deck<DevelopmentCard> purple3 = new Deck<>();
        purple3.add(new DevelopmentCard(9,new Requirements(new Pair<>(ResourceType.SERVANT,6)),3,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.STONE,2)),new Pair<>(NonPhysicalResourceType.FAITH_POINT,2),new Pair<>(ResourceType.COIN,3)));
        purple3.add(new DevelopmentCard(10,new Requirements(new Pair<>(ResourceType.SERVANT,5),new Pair<>(ResourceType.COIN,2)),3,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.STONE,1),new Pair<>(ResourceType.SHIELD,1)),new Pair<>(ResourceType.COIN,2),new Pair<>(ResourceType.SERVANT,2),new Pair<>(NonPhysicalResourceType.FAITH_POINT,1)));
        purple3.add(new DevelopmentCard(11,new Requirements(new Pair<>(ResourceType.SERVANT,7)),3,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.COIN,1)),new Pair<>(ResourceType.STONE,1),new Pair<>(NonPhysicalResourceType.FAITH_POINT,3)));
        purple3.add(new DevelopmentCard(12,new Requirements(new Pair<>(ResourceType.SERVANT,4),new Pair<>(ResourceType.SHIELD,4)),3,DevelopmentColorType.PURPLE,new Requirements(new Pair<>(ResourceType.COIN,1)),new Pair<>(ResourceType.STONE,3),new Pair<>(ResourceType.SERVANT,1)));
        purple3.shuffle();
        ret.add(purple3);

        return ret;
    }

    private static Deck<LeaderCard> generateLeaderCards(){
        Deck<LeaderCard> ret = new Deck<>();
        ret.add(new DiscountLeaderCard(2,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,0,1),new Triple<>(DevelopmentColorType.GREEN,0,1)),ResourceType.SERVANT));
        ret.add(new DiscountLeaderCard(2,new Requirements(new Triple<>(DevelopmentColorType.BLUE,0,1),new Triple<>(DevelopmentColorType.PURPLE,0,1)),ResourceType.SHIELD));
        ret.add(new DiscountLeaderCard(2,new Requirements(new Triple<>(DevelopmentColorType.GREEN,0,1),new Triple<>(DevelopmentColorType.BLUE,0,1)),ResourceType.STONE));
        ret.add(new DiscountLeaderCard(2,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,0,1),new Triple<>(DevelopmentColorType.PURPLE,0,1)),ResourceType.COIN));

        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.COIN,5)),ResourceType.STONE));
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.STONE,5)),ResourceType.SERVANT));
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.SERVANT,5)),ResourceType.SHIELD));
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.SHIELD,5)),ResourceType.COIN));

        ret.add(new WhiteMarbleLeaderCard(5,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,0,2),new Triple<>(DevelopmentColorType.BLUE,0,1)),ResourceType.SERVANT));
        ret.add(new WhiteMarbleLeaderCard(5,new Requirements(new Triple<>(DevelopmentColorType.GREEN,0,2),new Triple<>(DevelopmentColorType.PURPLE,0,1)),ResourceType.SHIELD));
        ret.add(new WhiteMarbleLeaderCard(5,new Requirements(new Triple<>(DevelopmentColorType.BLUE,0,2),new Triple<>(DevelopmentColorType.YELLOW,0,1)),ResourceType.STONE));
        ret.add(new WhiteMarbleLeaderCard(5,new Requirements(new Triple<>(DevelopmentColorType.PURPLE,0,2),new Triple<>(DevelopmentColorType.GREEN,0,1)),ResourceType.COIN));

        ret.add(new ProductionLeaderCard(4,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,2,1)),new Requirements(new Pair<>(ResourceType.SHIELD,1))));
        ret.add(new ProductionLeaderCard(4,new Requirements(new Triple<>(DevelopmentColorType.BLUE,2,1)),new Requirements(new Pair<>(ResourceType.SERVANT,1))));
        ret.add(new ProductionLeaderCard(4,new Requirements(new Triple<>(DevelopmentColorType.PURPLE,2,1)),new Requirements(new Pair<>(ResourceType.STONE,1))));
        ret.add(new ProductionLeaderCard(4,new Requirements(new Triple<>(DevelopmentColorType.GREEN,2,1)),new Requirements(new Pair<>(ResourceType.COIN,1))));

        ret.shuffle();
        return ret;
    }

    public Deck<DevelopmentCard> getDevelopmentCardDeck(DevelopmentColorType color, int level){
        List<DevelopmentColorType> colors = Arrays.asList(DevelopmentColorType.values());
        int column = colors.indexOf(color);
        return developmentDecks.get((level-1)*colors.size()+column);
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
        if(players.size() >= MAX_PLAYERS)
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

    public void vaticanReport(int popeSpace){
        if(!players.isEmpty() && players.get(0).getFaithTrack().isValidVaticanReport(popeSpace)) {
            for (PlayerBoard p : players) {
                if (p.getFaithTrack().getFaithMarker() >= popeSpace - (3 + vaticanReportsCount))
                    p.getFaithTrack().getPopeFavorTiles()[vaticanReportsCount].uncover();
                else
                    p.getFaithTrack().getPopeFavorTiles()[vaticanReportsCount] = null;
                p.getFaithTrack().vaticanReportTriggered(vaticanReportsCount);
            }
            vaticanReportsCount++;
        }
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

    public void endTurn(){}

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
        END_GAME;
    }
}
