package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DevelopmentCardSlot implements Iterable<DevelopmentCard>{

    private final List<DevelopmentCard> developmentCards;

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

    public DevelopmentCard getFromLevel(int level) throws IllegalArgumentException{
        if(developmentCards.size() <= level)
            throw new IllegalStateException("No card for this level");
        return developmentCards.get(level);
    }

    @Override
    public Iterator<DevelopmentCard> iterator() {
        return developmentCards.iterator();
    }
}
