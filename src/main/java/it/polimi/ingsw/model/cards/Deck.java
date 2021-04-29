package it.polimi.ingsw.model.cards;

import java.util.*;

public class Deck <E> implements Iterable<E> {

    /**
     * A list of generic cards
     */
    private final List<E> cards;

    public Deck() {
        cards = new ArrayList<>();
    }

    /**
     * Randomly permutes the cards list. All permutations occur with approximately equal likelihood.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Returns the first item of the deck
     *
     * @return the first item of the deck
     */
    public E top() {
        return cards.get(0);
    }

    /**
     * Remove and returns the first item of the deck
     *
     * @return the first item of the deck
     */
    public E pop() {
        return cards.remove(0);
    }

    /**
     * Returns an iterator over the elements in this deck in proper sequence.
     *
     * @return an iterator over the elements in this deck in proper sequence.
     */
    @Override
    public Iterator<E> iterator() {
        return cards.iterator();
    }


    /**
     * Returns the element at the specified position in this deck.
     *
     * @param index index of the element to return
     * @return the element at the specified position in this deck
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   ({@code index < 0 || index >= size()})
     */
    public E get(int index) {
        return cards.get(index);
    }

    /**
     * Remove and returns the element at the specified position in this deck.
     *
     * @param index index of the element to remove
     * @return the element at the specified position in this deck
     * @throws IndexOutOfBoundsException if the index is out of range
     *                                   ({@code index < 0 || index >= size()})
     */
    public E remove(int index) {
        E temp = get(index);
        cards.remove(index);
        return temp;
    }

    /**
     * Appends the specified element to the end of this deck
     *
     * @param item the element to add to the deck
     */
    public void add(E item) {
        cards.add(item);
    }

    /**
     * Returns the number of elements in this deck.
     *
     * @return the number of elements in this deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * Returns if this deck contains no elements.
     *
     * @return if this deck contains no elements
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }


    /**
     * Returns true if the specified element is equal to the deck
     *
     * @param o the element to compare
     * @return true if the elements are equal, false elsewhere
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Deck<?> deck = (Deck<?>) o;
        return cards.equals(deck.cards);
    }

    /**
     * Returns the string that describes the Deck
     * @return the string that describes the Deck
     */
    @Override
    public String toString() {
        return "Deck{" +
                "cards=" + cards +
                '}';
    }
}
