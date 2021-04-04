package it.polimi.ingsw.model.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Deck <E> implements Iterable<E>{

    private List<E> cards;

    public Deck(){
        cards = new ArrayList<>();
    }

    public void shuffle(){
        Collections.shuffle(cards);
    }

    public E top(){
        return cards.get(0);
    }

    public E pop(){
        E temp = top();
        cards.remove(0);
        return temp;
    }

    @Override
    public Iterator<E> iterator(){
        return cards.iterator();
    }

    public E get(int position){
       return cards.get(position);
    }

    public E remove(int position){
        E temp = get(position);
        cards.remove(position);
        return temp;
    }

    public void add(E item){
        cards.add(item);
    }

    public int size(){
        return cards.size();
    }

    public boolean isEmpty(){
        return cards.isEmpty();
    }

}
