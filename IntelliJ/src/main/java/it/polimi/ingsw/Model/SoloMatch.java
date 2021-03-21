package it.polimi.ingsw.Model;

public class SoloMatch extends Match{

    private FaithTrack blackCross;
    private Deck<ActionToken> actionTokens;

    public SoloMatch(){
        super();
        blackCross = new FaithTrack(this);
        actionTokens = new Deck<>();
        actionTokens.add(new ActionToken(DevelopmentColorType.BLUE));
        actionTokens.add(new ActionToken(DevelopmentColorType.GREEN));
        actionTokens.add(new ActionToken(DevelopmentColorType.YELLOW));
        actionTokens.add(new ActionToken(DevelopmentColorType.PURPLE));
        actionTokens.add(new ActionToken(2));
        actionTokens.add(new ActionToken(2));
        actionTokens.add(new ActionToken(1));
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

    public void discardDevelopmentCards(DevelopmentColorType color){
        for(int j=0;j<2;j++) {
            for (int i = 0; i < DevelopmentCard.MAX_LEVEL; i++) {
                Deck<DevelopmentCard> temp = getDevelopmentCardDeck(color, i);
                if (!temp.isEmpty()) {
                    temp.remove(0);
                    return;
                }
            }
        }
    }

}
