package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.DevelopmentColorType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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

    public int getCardsNum(DevelopmentColorType color, int level) {
        if (level == 0) // any level
            return developmentCards.stream()
                .filter(card -> card.getColor() == color)
                .mapToInt(x -> 1).sum();
        else // exact level
            return developmentCards.stream()
                    .filter(card -> card.getColor() == color)
                    .filter(card -> card.getLevel() == level)
                    .mapToInt(x -> 1).sum();
    }

    public DevelopmentCard getFromLevel(int level) throws IllegalArgumentException{
        if(developmentCards.size() < level)
            throw new IllegalStateException("No card for this level");
        return developmentCards.get(level - 1);
    }

    @Override
    public Iterator<DevelopmentCard> iterator() {
        return developmentCards.iterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DevelopmentCardSlot that = (DevelopmentCardSlot) o;
        return Objects.equals(developmentCards, that.developmentCards);
    }

    @Override
    public String toString() {

        return "DevelopmentCardSlot{" +
                "developmentCards=" + developmentCards +
                '}';
    }
}
