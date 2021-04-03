package it.polimi.ingsw.view;


public interface RealView extends View {

    // show welcome screen and ask for server ip and port
    void init();

    // ask for server ip and port
    void askConnection();

    // ask desired username
    void askLogin();

}
