package it.polimi.ingsw.utils;

import it.polimi.ingsw.view.View;

public interface ObservableFromView {
    void addView(View view);
    void removeView(View view);
}
