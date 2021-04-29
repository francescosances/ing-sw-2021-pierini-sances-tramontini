package it.polimi.ingsw.model;

import java.util.Arrays;
public class FaithTrack {

    /**
     * Array with all the spaces that trigger vaticanReport()
     */
    public static final int[] POPE_SPACES = {8,16,24};
    /**
     * The size of the FaithTrack spaces
     */
    public static final int SIZE = 24;

    /**
     * Reference to the match
     */
    private transient Match match;
    /**
     * Contains the actual position of the faithMarker
     */
    private int faithMarker;
    /**
     * Array that contains all the PopeFavorTiles the player has
     */
    private final PopeFavorTile[] popeFavorTiles;
    /**
     * array that stores whether its vaticanReport has already been triggered or not
     */
    private final boolean[] vaticanReports;

    /**
     * Initialize a new FaithTrack connected to the its match
     * @param match the match reference
     */
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

    /**
     * returns true if the space is a popeSpace, false elsewhere
     * @param space the space to confront
     * @return true if the space is a popeSpace, false elsewhere
     */
    public boolean isPopeSpace(int space){
        for (int popeSpace : POPE_SPACES) {
            if (popeSpace == space)
                return true;
        }
        return false;
    }

    /**
     * Returns the position of the faithMarker
     * @return the position of the faithMarker
     */
    public int getFaithMarker(){
        return this.faithMarker;
    }

    /**
     * Returns the number of victory points tied to the position on the FaithTrack
     * @return the number of victory points tied to the position on the FaithTrack
     */
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

    /**
     * Returns the number of VictoryPoints associated with PopeFavorTiles
     * @return the number of VictoryPoints associated with PopeFavorTiles
     */
    public int getPopeFavorTilesVictoryPoints() {
        int ret = 0;
        for (int i = 0; i < popeFavorTiles.length; i++) {
            if (popeFavorTiles[i] != null)
                ret += popeFavorTiles[i].getVictoryPoints();
        }
        return ret;
    }

    /**
     * Returns the number of victory points gained in the FaithTrack
     * @return the number of victory points gained in the FaithTrack
     */
    public int getVictoryPoints(){
        return getTrackVictoryPoints() + getPopeFavorTilesVictoryPoints();
    }

    /**
     * Moves the marker by one space
     * @throws EndGameException if the faithMarker is on the last space
     */
    public void moveMarker() throws EndGameException {
        faithMarker++;
        if(match.getVaticanReportsCount() < vaticanReports.length &&
                !vaticanReports[match.getVaticanReportsCount()] && isPopeSpace(faithMarker))
            match.vaticanReport(faithMarker);
        if(faithMarker == SIZE)
            throw new EndGameException(true);
    }

    /**
     * Returns the array that contains all player's PopeFavorTiles
     * @return the array that contains all player's PopeFavorTiles
     */
    public PopeFavorTile[] getPopeFavorTiles(){
        return popeFavorTiles;
    }


    /**
     * Sets the match reference
     * @param match to be referenced
     */
    public void setMatch(Match match){
        if (match == null)
            throw new NullPointerException();
        this.match = match;
    }
    /**
     * sets the current of the vatican report to true
     * @param vaticanReportCount the number of the vatican report triggered
     */
    protected void vaticanReportTriggered(int vaticanReportCount) {
        vaticanReports[vaticanReportCount] = true;
    }

    /**
     * Returns true if the space is a Pope Space that hasn't triggered a vatican report yet
     * @param space the space to compare
     * @return true if the space is a Pope Space that hasn't triggered a vatican report yet
     */
    public boolean isValidVaticanReport(int space){
        int count = 0;
        while (count < POPE_SPACES.length && space != POPE_SPACES[count])
            count++;
        return count < vaticanReports.length && !vaticanReports[count];
    }

    /**
     * Indicates whether some other object is equal to this one
     * @param o that is confronted
     * @return true if o is equal to the object, false elsewhere
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaithTrack that = (FaithTrack) o;
        return faithMarker == that.faithMarker && Arrays.equals(popeFavorTiles, that.popeFavorTiles) && Arrays.equals(vaticanReports, that.vaticanReports);
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "FaithTrack{" +
                ", faithMarker=" + faithMarker +
                ", popeFavorTiles=" + Arrays.toString(popeFavorTiles) +
                ", vaticanReports=" + Arrays.toString(vaticanReports) +
                '}';
    }
}
