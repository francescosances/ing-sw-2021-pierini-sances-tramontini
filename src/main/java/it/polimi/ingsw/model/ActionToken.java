package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.DevelopmentColorType;

public class ActionToken {

    /**
     * The DevelopmentCard color that needs to be discarded when the Action Token is drawn
     */
    private final DevelopmentColorType developmentCard;

    /**
     * The number of spaces the black cross must move when the Action Token is drawn
     */
    private final Integer blackCrossSpaces;

    /**
     * Initialize a new ActionToken that discards DevelopmentCards when drawn
     * @param developmentCard the color of the DevelopmentCards discarded
     */
    public ActionToken(DevelopmentColorType developmentCard){
        if (developmentCard == null)
            throw new NullPointerException();
        this.developmentCard = developmentCard;
        blackCrossSpaces = null;
    }

    /**
     * Initialize a new ActionToken that moves the blackCross when drawn
     * @param blackCrossSpaces the number of spaces the blackCross moves when drawn
     */
    public ActionToken(int blackCrossSpaces){
        this.blackCrossSpaces = blackCrossSpaces;
        this.developmentCard = null;
    }

    /**
     * Executes the action of the Action Token. Discards a specific color development card or moves the blackCross. If the blackCross moves by 1 space, shuffles all the tokens
     * @param match the match in which the ActionToken is drawn
     * @throws EndGameException if the blackCross moved to the last FaithTrackSpace
     */
    public void show(SoloMatch match) throws EndGameException {
        if (match == null){
            throw new NullPointerException();
        }
        if(developmentCard != null)
            match.discardDevelopmentCards(developmentCard);
        else {
            if (blackCrossSpaces== null)
                throw new NullPointerException();
            match.moveBlackCross(blackCrossSpaces);
            if (blackCrossSpaces == 1)
                match.shuffleActionTokens();
        }
    }

    /**
     * Returns true if other equals the ActionToken; false elsewhere
     * @param other the Object to compare
     * @return true if other equals the ActionToken; false elsewhere
     */
    @Override
    public boolean equals(Object other){
        if (this == other) return true;
        if (!(other instanceof ActionToken))
            return false;
        ActionToken o = (ActionToken) other;

        if (this.blackCrossSpaces != null && o.blackCrossSpaces != null)
            return this.blackCrossSpaces.equals(o.blackCrossSpaces) && this.developmentCard == null && o.developmentCard == null;
        if (this.developmentCard != null && o.developmentCard != null)
            return this.developmentCard.equals(o.developmentCard) && this.blackCrossSpaces == null && o.blackCrossSpaces == null;
        return false;
    }

    /**
     * Returns the hash code of the object
     * @return the hash code of the object
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
