package it.polimi.ingsw.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        this.leaderCards = new Deck<>();
        this.vaticanReportsCount = 0;
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
