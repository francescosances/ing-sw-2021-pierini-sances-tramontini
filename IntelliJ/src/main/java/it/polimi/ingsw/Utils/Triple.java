package it.polimi.ingsw.Utils;

public class Triple <E,T,P>{

    protected E first;
    protected T second;
    protected P third;

    public Triple(E first,T second,P third){
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public E getFirst() {
        return first;
    }

    public void setFirst(E first) {
        this.first = first;
    }

    public T getSecond() {
        return second;
    }

    public void setSecond(T second) {
        this.second = second;
    }

    public P getThird() {
        return third;
    }

    public void setThird(P third) {
        this.third = third;
    }
}
