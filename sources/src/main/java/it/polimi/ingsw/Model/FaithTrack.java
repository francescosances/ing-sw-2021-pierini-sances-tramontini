package it.polimi.ingsw.Model;

public class FaithTrack {

    public static final int[] POPE_SPACES = {8,16,24};
    public static final int SIZE = 24;

    private Match match;
    private int faithMarker;
    private PopeFavorTile[] popeFavorTiles;

    public FaithTrack(Match match){
        this.match = match;
        popeFavorTiles = new PopeFavorTile[POPE_SPACES.length];
        for(int i=0;i<POPE_SPACES.length;i++){
            popeFavorTiles[i] = new PopeFavorTile();
        }
        faithMarker = 0;
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

    public int getVictoryPoints(){
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

    public void moveMarker() throws EndGameException {
        faithMarker++;
        if(isPopeSpace(faithMarker)) {
            match.vaticanReport(faithMarker);
            if(faithMarker == SIZE)
                throw new EndGameException();
        }

    }

    public PopeFavorTile[] getPopeFavoreTiles(){
        return popeFavorTiles;
    }

    public void setMatch(Match match){
        this.match = match;
    }

}
