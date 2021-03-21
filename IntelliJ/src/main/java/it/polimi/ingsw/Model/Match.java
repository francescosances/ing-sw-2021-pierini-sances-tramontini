package it.polimi.ingsw.Model;

import Utils.Triple;
import com.sun.tools.javac.util.Pair;

import java.util.*;

public class Match {

    public static final int MAX_PLAYERS = 4;

    protected Market market;

    protected List<PlayerBoard> players;

    protected List<Deck<DevelopmentCard>> developmentDecks;
    protected Deck<LeaderCard> leaderCards;

    private int vaticanReportsCount;

    public Match(){
        this.players = new ArrayList<>();
        this.market = new Market();
        this.developmentDecks = new ArrayList<>();
        this.leaderCards = generateLeaderCards();
        this.vaticanReportsCount = 0;
    }

    public static Deck<LeaderCard> generateLeaderCards(){
        Deck<LeaderCard> ret = new Deck<>();
        ret.add(new DiscountLeaderCard(2,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,1,1),new Triple<>(DevelopmentColorType.GREEN,1,1)),ResourceType.SERVANT));
        ret.add(new DiscountLeaderCard(2,new Requirements(new Triple<>(DevelopmentColorType.BLUE,1,1),new Triple<>(DevelopmentColorType.PURPLE,1,1)),ResourceType.SHIELD));
        ret.add(new DiscountLeaderCard(2,new Requirements(new Triple<>(DevelopmentColorType.GREEN,1,1),new Triple<>(DevelopmentColorType.BLUE,1,1)),ResourceType.STONE));
        ret.add(new DiscountLeaderCard(2,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,1,1),new Triple<>(DevelopmentColorType.PURPLE,1,1)),ResourceType.COIN));

        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.COIN,5)),ResourceType.STONE));
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.STONE,5)),ResourceType.SERVANT));
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.SERVANT,5)),ResourceType.SHIELD));
        ret.add(new DepotLeaderCard(3,new Requirements(new Pair<>(ResourceType.SHIELD,5)),ResourceType.COIN));

        ret.add(new WhiteMarbleLeaderCard(5,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,1,2),new Triple<>(DevelopmentColorType.BLUE,1,1)),ResourceType.SERVANT));
        ret.add(new WhiteMarbleLeaderCard(5,new Requirements(new Triple<>(DevelopmentColorType.GREEN,1,2),new Triple<>(DevelopmentColorType.PURPLE,1,1)),ResourceType.SHIELD));
        ret.add(new WhiteMarbleLeaderCard(5,new Requirements(new Triple<>(DevelopmentColorType.BLUE,1,2),new Triple<>(DevelopmentColorType.YELLOW,1,1)),ResourceType.STONE));
        ret.add(new WhiteMarbleLeaderCard(5,new Requirements(new Triple<>(DevelopmentColorType.PURPLE,1,2),new Triple<>(DevelopmentColorType.GREEN,1,1)),ResourceType.COIN));

        ret.add(new ProductionLeaderCard(4,new Requirements(new Triple<>(DevelopmentColorType.YELLOW,2,1)),new Requirements(new Pair<>(ResourceType.SHIELD,1))));
        ret.add(new ProductionLeaderCard(4,new Requirements(new Triple<>(DevelopmentColorType.BLUE,2,1)),new Requirements(new Pair<>(ResourceType.SERVANT,1))));
        ret.add(new ProductionLeaderCard(4,new Requirements(new Triple<>(DevelopmentColorType.PURPLE,2,1)),new Requirements(new Pair<>(ResourceType.STONE,1))));
        ret.add(new ProductionLeaderCard(4,new Requirements(new Triple<>(DevelopmentColorType.GREEN,2,1)),new Requirements(new Pair<>(ResourceType.COIN,1))));

        return ret;
    }

    public Deck<DevelopmentCard> getDevelopmentCardDeck(DevelopmentColorType color, int level){
        List<DevelopmentColorType> colors = Arrays.asList(DevelopmentColorType.values());
        int column = colors.indexOf(color);
        return developmentDecks.get((level-1)*colors.size()+column);
    }

    public PlayerBoard addPlayer(String username){
        if(players.size() >= MAX_PLAYERS)
            throw new IllegalStateException("Maximum number of players reached");
        if(players.stream().anyMatch(playerBoard -> playerBoard.getUsername().equals(username)))
            throw new IllegalArgumentException("Username already inserted");
        PlayerBoard temp = new PlayerBoard(username);
        players.add(temp);
        return temp;
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
        for(PlayerBoard p : players){
            if(p.getFaithTrack().getFaithMarker() >= popeSpace-3-vaticanReportsCount){
                p.getFaithTrack().getPopeFavoreTiles()[vaticanReportsCount].uncover();
            }else{
                p.getFaithTrack().getPopeFavoreTiles()[vaticanReportsCount] = null;
            }
        }
        vaticanReportsCount++;
    }

}
