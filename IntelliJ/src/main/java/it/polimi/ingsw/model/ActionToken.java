package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.model.cards.EndGameException;

public class ActionToken {

    private DevelopmentColorType developmentCard;
    private int blackCrossSpaces;

    public ActionToken(DevelopmentColorType developmentCard){
        this.developmentCard = developmentCard;
    }

    public ActionToken(int blackCrossSpaces){
        this.blackCrossSpaces = blackCrossSpaces;
    }

    public void show(SoloMatch match) throws EndGameException {
        if(developmentCard != null)
            match.discardDevelopmentCards(developmentCard);
        else {
            match.moveBlackCross(blackCrossSpaces);
            if (blackCrossSpaces == 1)
                match.shuffleActionTokens();
        }
    }

}
