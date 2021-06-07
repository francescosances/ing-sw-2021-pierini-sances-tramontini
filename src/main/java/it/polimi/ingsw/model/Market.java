package it.polimi.ingsw.model;


import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.view.ObservableFromView;
import it.polimi.ingsw.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Market implements ObservableFromView {

    public static final int ROWS = 3;
    public static final int COLUMNS = 4;

    private final MarbleType[][] marbles;
    private MarbleType slideMarble;
    private transient List<View> views;

    public Market(){
        marbles = new MarbleType[ROWS][COLUMNS];
        generateMarbles();
        views = new ArrayList<>();
    }

    public MarbleType getMarble(int row,int column){
        return marbles[row][column];
    }

    protected void setMarble(int row,int column,MarbleType marble){
        marbles[row][column] = marble;
    }

    protected void setSlideMarble(MarbleType marble){
        slideMarble = marble;
    }

    public MarbleType getSlideMarble(){
        return slideMarble;
    }

    public Resource[] chooseRow(int row){
        if(row < 0 || row >= ROWS)
            throw new IllegalArgumentException("Invalid row");
        Resource[] res = new Resource[COLUMNS];
        for(int i=0;i<COLUMNS;i++){
            res[i] = getMarble(row,i).toResource();
        }
        MarbleType temp = getMarble(row,0);
        for(int i=1;i<COLUMNS;i++){
            setMarble(row,i-1,getMarble(row,i));
        }
        setMarble(row,COLUMNS-1,slideMarble);
        slideMarble = temp;
        updateViews();
        return res;
    }

    public Resource[] chooseColumn(int column){
        if(column < 0 || column >= COLUMNS)
            throw new IllegalArgumentException("Invalid column");
        Resource[] res = new Resource[ROWS];
        for(int i=0;i<ROWS;i++){
            res[i] = getMarble(i,column).toResource();
        }
        MarbleType temp = getMarble(0,column);
        for(int i=1;i<ROWS;i++){
            setMarble(i-1,column,getMarble(i,column));
        }
        setMarble(ROWS-1,column,slideMarble);
        slideMarble = temp;
        updateViews();
        return res;
    }

    private void generateMarbles(){
        List<MarbleType> allMarbles = new ArrayList<>();
        allMarbles.add(MarbleType.RED);
        allMarbles.add(MarbleType.WHITE);
        allMarbles.add(MarbleType.WHITE);
        allMarbles.add(MarbleType.WHITE);
        allMarbles.add(MarbleType.WHITE);
        allMarbles.add(MarbleType.YELLOW);
        allMarbles.add(MarbleType.YELLOW);
        allMarbles.add(MarbleType.GREY);
        allMarbles.add(MarbleType.GREY);
        allMarbles.add(MarbleType.BLUE);
        allMarbles.add(MarbleType.BLUE);
        allMarbles.add(MarbleType.PURPLE);
        allMarbles.add(MarbleType.PURPLE);

        Collections.shuffle(allMarbles);

        for(int r=0;r<ROWS;r++){
            for(int c=0;c<COLUMNS;c++){
                marbles[r][c] = allMarbles.get(r*COLUMNS+c);
            }
        }
        slideMarble = allMarbles.get(allMarbles.size()-1);
    }

    @Override
    public void addView(View view) {
        if (views == null)
            views = new ArrayList<>();
        views.add(view);
    }

    @Override
    public void removeView(View view) {
        views.remove(view);
    }

    private void updateViews(){
        for (View view:views)
            view.showMarket(this);
    }

    @Override
    public boolean equals (Object other){
        if (this == other) return true;
        if (!(other instanceof  Market))
            return false;

        Market o = (Market) other;
        for (int row = 0; row < ROWS; row++) {
            for (int column = 0; column < COLUMNS; column++) {
                if (!(this.getMarble(row, column).equals(o.getMarble(row, column))))
                    return false;
            }

        }
        return this.slideMarble.equals(o.slideMarble);
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
