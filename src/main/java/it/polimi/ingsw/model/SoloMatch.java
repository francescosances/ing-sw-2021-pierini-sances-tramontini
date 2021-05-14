package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;

public class SoloMatch extends Match{

    private final FaithTrack blackCross;
    private final Deck<ActionToken> actionTokens;

    public SoloMatch(String matchName){
        super(matchName, 1);
        blackCross = new FaithTrack(this);
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

    public void moveBlackCross(int spaces) throws EndGameException {
        for(int i=0;i<spaces;i++){
            blackCross.moveMarker();
        }
    }
    public void shuffleActionTokens(){
        actionTokens.shuffle();
    }

    public FaithTrack getBlackCross(){
        return blackCross;
    }

    protected void discardDevelopmentCards(DevelopmentColorType color){
        int count = 2;
        int level = 1;
        while (count > 0){
            Deck<DevelopmentCard> temp = getDevelopmentCardDeck(color, level);
            if (temp.isEmpty())
                level++;
            else {
                temp.remove(0);
                count--;
            }
        }
    }

    public ActionToken drawActionToken(){
        return actionTokens.remove(0);
    }

    @Override
    public void endTurn(){
        super.endTurn();

        //TODO: comunicare al giocatore che un ActionToken è stato pescato
        try {
            drawActionToken().show(this);//TODO: mischiare gli action token scartati
            //TODO: creare metodo endgame per contare i punti
        } catch (EndGameException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void discardResource(PlayerBoard player) throws EndGameException {
        moveBlackCross(1);
    }

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
