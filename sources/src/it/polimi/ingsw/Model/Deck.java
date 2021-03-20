package it.polimi.ingsw.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Deck <E>{

    private ArrayList<E> cards;

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

}
