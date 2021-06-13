package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;
import it.polimi.ingsw.view.View;

public class SoloMatch extends Match{

    /**
     * Black cross' Faith track
     */
    private final FaithTrack blackCross;
    /**
     * A deck containing the action token to draw at the end of each turn
     */
    private Deck<ActionToken> actionTokens;
    /**
     * Is true if an action token has to be drawn at the end of the turn
     */
    private boolean drawableActionToken;
    /**
     * is true if a deck is empty, false elsewhere
     */
    private boolean cardsFinished;

    /**
     * Initializes a new SoloMatch object
     * @param matchName the name of the match
     */
    public SoloMatch(String matchName){
        super(matchName, 1);
        blackCross = new FaithTrack(this, FaithTrack.BLACK_CROSS);
        blackCross.setBlackCross(true);
        shuffleActionTokens();
        drawableActionToken = false;
        cardsFinished = false;
    }

    /**
     * Moves the black cross along the faith track
     * @param spaces the number of spaces the black cross must move
     * @throws EndGameException if the black cross reaches the last space
     */
    public void moveBlackCross(int spaces) throws EndGameException {
        if (blackCross.hasMatchMissing())
            blackCross.setMatch(this);
        blackCross.moveMarker(spaces);
    }

    /**
     * The actionTokens deck is refilled with all action tokens and shuffled
     */
    public void shuffleActionTokens() {
        actionTokens = new Deck<>();
        actionTokens.add(new ActionToken(DevelopmentColorType.BLUE));
        actionTokens.add(new ActionToken(DevelopmentColorType.GREEN));
        actionTokens.add(new ActionToken(DevelopmentColorType.YELLOW));
        actionTokens.add(new ActionToken(DevelopmentColorType.PURPLE));
        actionTokens.add(new ActionToken(2));
        actionTokens.add(new ActionToken(2));
        actionTokens.add(new ActionToken(1));
        actionTokens.shuffle();
    }

    /**
     * Returns black cross' FaithTrack
     * @return black cross' FaithTrack
     */
    public FaithTrack getBlackCross(){
        return blackCross;
    }

    /**
     * Discard two DevelopmentCards of the specified lowest level
     * @param color the color specified
     */
    protected void discardDevelopmentCards(DevelopmentColorType color) throws EndGameException {
        int count = 2;
        int level = 1;
        while (count > 0){
            Deck<DevelopmentCard> temp = getDevelopmentCardDeck(color, level);
            if (temp.isEmpty())
                level++;
            else {
                temp.remove(0);
                count--;
                if (getDevelopmentCardDeck(color, 3).isEmpty()) {
                    cardsFinished = true;
                    throw new EndGameException();
                }
            }
        }
    }

    /**
     * Returns true if a deck is empty, false elsewhere
     * @return true if a deck is empty, false elsewhere
     */
    public boolean anEmptyDeck(){
        return cardsFinished;
    }

    /**
     * Draws an ActionToken from actionTokens Deck
     * @return the ActionToken Drawn
     */
    public ActionToken drawActionToken(){
        return actionTokens.remove(0);
    }

    /**
     * Handles the end of a player's turn.
     * Draws a new ActionToken and plays its action
     */
    @Override
    public void endTurn(){
        super.endTurn();
        if (drawableActionToken) {
            ActionToken actionToken = drawActionToken();
            for (View view : views) {
                view.showActionToken(actionToken);
                actionToken.show(this);
            }
        }
        drawableActionToken = true;
    }

    /**
     * Moves the blackCross by one space
     * @param player the player that discarded the resource
     * @throws EndGameException if the black cross reaches the last space
     */
    @Override
    public void discardResource(PlayerBoard player) throws EndGameException {
        moveBlackCross(1);
    }

    @Override
    public void addView(View view) {
        super.addView(view);
        blackCross.addView(view);
    }

    @Override
    public void removeView(View view) {
        super.removeView(view);
        blackCross.removeView(view);
    }


    /**
     * Returns true if other equals the ActionToken; false elsewhere
     * @param o the Object to compare
     * @return true if other equals the ActionToken; false elsewhere
     */
    @Override
    public boolean equals(Object o) {
        SoloMatch that = (SoloMatch) o;
        return  super.equals(o) &&
                this.blackCross.equals(that.blackCross) &&
                this.actionTokens.equals(that.actionTokens);
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
