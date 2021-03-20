package it.polimi.ingsw.Model;

import java.util.ArrayList;
import java.util.List;

public class DevelopmentCardSlot {

    private List<DevelopmentCard> developmentCards;

    public DevelopmentCardSlot(){
        developmentCards = new ArrayList<>();
    }

    public DevelopmentCard getTopCard(){
        if(developmentCards.isEmpty())
            return null;
        return developmentCards.get(0);
    }

    public void addCard(DevelopmentCard developmentCard){
        developmentCards.add(developmentCard);
    }

    public boolean accepts(DevelopmentCard developmentCard){
        return getTopCard() == null || getTopCard().getLevel() == developmentCard.getLevel()-1;
    }

    public int getSize(){
        return developmentCards.size();
    }


}
