package it.polimi.ingsw.model;

import java.util.Arrays;
public class FaithTrack {

    public static final int[] POPE_SPACES = {8,16,24};
    public static final int SIZE = 24;

    private transient Match match;
    private int faithMarker;
    private final PopeFavorTile[] popeFavorTiles;
    private final boolean[] vaticanReports;

    public FaithTrack(Match match){
        this.match = match;
        popeFavorTiles = new PopeFavorTile[POPE_SPACES.length];
        for(int i=0;i<POPE_SPACES.length;i++){
            popeFavorTiles[i] = new PopeFavorTile(2+i);
        }
        faithMarker = 0;
        vaticanReports = new boolean[POPE_SPACES.length];
        Arrays.fill(vaticanReports,false);
    }

    private boolean isPopeSpace(int space){
        for (int popeSpace : POPE_SPACES) {
            if (popeSpace == space)
                return true;
        }
        return false;
    }

    public int getFaithMarker(){
        return this.faithMarker;
    }

    public int getTrackVictoryPoints(){
        if(faithMarker < 3)
            return 0;
        if(faithMarker < 6)
            return 1;
        else if(faithMarker < 9)
            return 2;
        else if(faithMarker < 12)
            return 4;
        else if(faithMarker < 15)
            return 6;
        else if(faithMarker < 18)
            return 9;
        else if(faithMarker < 21)
            return 12;
        else if(faithMarker < 24)
            return 16;
        return 20;
    }

    public int getPopeFavorTilesVictoryPoints() {
        return Arrays.stream(popeFavorTiles).mapToInt(PopeFavorTile::getVictoryPoints).sum();
    }

    public int getVictoryPoints(){
        return getTrackVictoryPoints() + getPopeFavorTilesVictoryPoints();
    }

    public void moveMarker() throws EndGameException {
        faithMarker++;
        if(isPopeSpace(faithMarker) && !vaticanReports[match.getVaticanReportsCount()]) {
            vaticanReports[match.getVaticanReportsCount()] = true;
            match.vaticanReport(faithMarker);
        }
        if(faithMarker == SIZE)
            throw new EndGameException();

    }

    public PopeFavorTile[] getPopeFavorTiles(){
        return popeFavorTiles;
    }

    public void setMatch(Match match){
        this.match = match;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaithTrack that = (FaithTrack) o;
        return faithMarker == that.faithMarker && Arrays.equals(popeFavorTiles, that.popeFavorTiles) && Arrays.equals(vaticanReports, that.vaticanReports);
    }

    @Override
    public String toString() {
        return "FaithTrack{" +
                ", faithMarker=" + faithMarker +
                ", popeFavorTiles=" + Arrays.toString(popeFavorTiles) +
                ", vaticanReports=" + Arrays.toString(vaticanReports) +
                '}';
    }
}
