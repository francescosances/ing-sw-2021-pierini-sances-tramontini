package it.polimi.ingsw.model.cards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class DevelopmentCardSlot implements Iterable<DevelopmentCard> {

    /**
     * The list of the development cards in the slot
     */
    private final List<DevelopmentCard> developmentCards;

    /**
     * The maximum number of card a slot can have
     */
    public static final int MAX_CARDS_PER_SLOT = 3;

    /**
     * Initializes a new DevelopmentCardSlot object
     */
    public DevelopmentCardSlot(){
        developmentCards = new ArrayList<>();
    }

    /**
     * Returns the DevelopmentCard at the top of the slot
     * @return the DevelopmentCard at the top of the slot
     */
    public DevelopmentCard getTopCard(){
        if(developmentCards.isEmpty())
            return null;
        return developmentCards.get(getSize()-1);
    }

    /**
     * Adds a card on top of the slot if accepted
     * @param developmentCard the card to add
     */
    public void addCard(DevelopmentCard developmentCard){
        if (this.accepts(developmentCard))
            developmentCards.add(developmentCard);
    }

    /**
     * Returns true if the slot accepts the DevelopmentCard parameter, false elsewhere
     * @param developmentCard the card to check if accepted
     * @return true if the slot accepts the DevelopmentCard parameter, false elsewhere
     */
    public boolean accepts(DevelopmentCard developmentCard){
        return (getTopCard() == null && developmentCard.getLevel() == 1) ||
                (getTopCard() != null && developmentCard.getLevel() <= MAX_CARDS_PER_SLOT && getTopCard().getLevel() == developmentCard.getLevel()-1);
    }

    /**
     * Returns the number of cards in the slot
     * @return the number of cards in the slot
     */
    public int getSize(){
        return developmentCards.size();
    }

    /**
     * Returns the number of cards that match with the parameters
     * @param color The color the cards must have to match with the parameter
     * @param level The level the cards must have to match with the parameter. 0 if it doesn't matter
     * @return the number of cards that match with the parameters
     */
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

    /**
     * Returns the card in the slot with the specified level
     * @param level the level of the card to get
     * @return the card in the slot with the specified level
     */
    public DevelopmentCard getFromLevel(int level){
        if(developmentCards.size() < level)
            throw new IllegalStateException("No card for this level");
        return developmentCards.get(level - 1);
    }

    /**
     * Returns the sum of the victory points of the cards in the slot
     * @returnthe sum of the victory points of the cards in the slot
     */
    public int getVictoryPoints(){
        return developmentCards.stream()
                .mapToInt(DevelopmentCard::getVictoryPoints)
                .sum();
    }

    public boolean isEmpty(){
        return this.developmentCards.isEmpty();
    }

    /**
     * Returns an iterator of the object
     * @return an iterator of the object
     */
    @Override
    public Iterator<DevelopmentCard> iterator() {
        return developmentCards.iterator();
    }

    /**
     * Indicates whether some other object is equal to this one
     * @param o that is confronted
     * @return true if o is equal to the object, false elsewhere
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DevelopmentCardSlot that = (DevelopmentCardSlot) o;
        return Objects.equals(developmentCards, that.developmentCards);
    }

    /**
     * Returns a string representation of the object
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        return "DevelopmentCardSlot: " + developmentCards;
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
