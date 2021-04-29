package it.polimi.ingsw.model;

public class EndGameException extends RuntimeException{

    /**
     * True if the user who throws the exception won the match, false if it lose.
     */
    private final boolean winner;

    public EndGameException(boolean winner){
        this.winner = winner;
    }

    public boolean isWinner(){
        return winner;
    }

}
