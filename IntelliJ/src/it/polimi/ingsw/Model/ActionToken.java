package it.polimi.ingsw.Model;

public class ActionToken {

    private DevelopmentColorType developmentCard;
    private int blackCrossSpaces;

    public void ActionToken(DevelopmentColorType developmentCard){
        this.developmentCard = developmentCard;
    }

    public void ActionToken(int blackCrossSpaces){
        this.blackCrossSpaces = blackCrossSpaces;
    }

    public void show(SoloMatch match) throws EndGameException {
        if(developmentCard != null)
            match.discardDevelopmentCard(developmentCard);
        else {
            match.moveBlackCross(blackCrossSpaces);
            if (blackCrossSpaces == 1)
                match.shuffleActionTokens();
        }
    }

}
