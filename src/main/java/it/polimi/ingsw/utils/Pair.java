package it.polimi.ingsw.utils;

import java.util.Objects;

public class Pair <E,T>{

    public E fst;
    public T snd;

    public Pair(E fst,T snd){
        this.fst = fst;
        this.snd = snd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(fst, pair.fst) && Objects.equals(snd, pair.snd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fst, snd);
    }

    @Override
    public String toString() {
        return "Triple{" +
                "first=" + fst +
                ", second=" + snd +
                '}';
    }
}
