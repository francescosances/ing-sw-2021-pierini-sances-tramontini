package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.DevelopmentColorType;

public class ActionToken {

    private DevelopmentColorType developmentCard;
    private Integer blackCrossSpaces;

    public ActionToken(DevelopmentColorType developmentCard){
        this.developmentCard = developmentCard;
    }

    public ActionToken(int blackCrossSpaces){
        this.blackCrossSpaces = blackCrossSpaces;
        this.developmentCard = null;
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

    @Override
    public boolean equals(Object other){
        if (!(other instanceof  ActionToken))
            return false;
        ActionToken o = (ActionToken) other;

        if (this.blackCrossSpaces == o.blackCrossSpaces) {
            if (this.developmentCard != null && o.developmentCard != null)
                return this.developmentCard.equals(o.developmentCard);
            return this.developmentCard == null && o.developmentCard == null;
        }
        return false;
    }

}
