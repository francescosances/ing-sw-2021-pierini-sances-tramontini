package it.polimi.ingsw.utils;

import it.polimi.ingsw.view.VirtualView;

public interface ObservableFromView {
    void addView(VirtualView view);
    void removeView(VirtualView view);
}