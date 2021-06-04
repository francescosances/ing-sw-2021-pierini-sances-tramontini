package it.polimi.ingsw.model;

public class EndGameException extends RuntimeException{

    /**
     * True if the user who throws the exception won the match, false if it lose.
     */
    private boolean winner;

    public EndGameException(boolean winner){
        this.winner = winner;
    }

    public EndGameException() {
    }

    public boolean isWinner(){
        return winner;
    }

}
