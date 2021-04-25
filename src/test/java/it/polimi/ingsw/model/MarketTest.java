package it.polimi.ingsw.model;

import it.polimi.ingsw.model.storage.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MarketTest {

    private Market market;

    @Before
    public void setUp() throws Exception {
        market = new Market();
    }

    @After
    public void tearDown() throws Exception {
        market = null;
    }

    @Test
    public void chooseRowTest() {
        market.setSlideMarble(MarbleType.WHITE);
        market.setMarble(0,0, MarbleType.WHITE);
        market.setMarble(0,1, MarbleType.PURPLE);
        market.setMarble(0,2, MarbleType.BLUE);
        market.setMarble(0,3, MarbleType.YELLOW);
        market.setMarble(1,0, MarbleType.PURPLE);
        market.setMarble(1,1, MarbleType.YELLOW);
        market.setMarble(1,2, MarbleType.WHITE);
        market.setMarble(1,3, MarbleType.GREY);
        market.setMarble(2,0, MarbleType.WHITE);
        market.setMarble(2,1, MarbleType.RED);
        market.setMarble(2,2, MarbleType.GREY);
        market.setMarble(2,3, MarbleType.BLUE);

        List<Resource> res = new ArrayList<Resource>(Arrays.asList(market.chooseRow(1)));
        assertEquals(MarbleType.PURPLE.toResource(), res.get(0));
        assertEquals(MarbleType.YELLOW.toResource(), res.get(1));
        assertEquals(MarbleType.WHITE.toResource(), res.get(2));
        assertEquals(MarbleType.GREY.toResource(), res.get(3));

        assertEquals(MarbleType.WHITE, market.getMarble(0,0));
        assertEquals(MarbleType.PURPLE, market.getMarble(0,1));
        assertEquals(MarbleType.BLUE, market.getMarble(0,2));
        assertEquals(MarbleType.YELLOW, market.getMarble(0,3));

        assertEquals(MarbleType.YELLOW, market.getMarble(1,0));
        assertEquals(MarbleType.WHITE, market.getMarble(1,1));
        assertEquals(MarbleType.GREY, market.getMarble(1,2));
        assertEquals(MarbleType.WHITE, market.getMarble(1,3));
        assertEquals(MarbleType.PURPLE, market.getSlideMarble());

        assertEquals(MarbleType.WHITE, market.getMarble(2,0));
        assertEquals(MarbleType.RED, market.getMarble(2,1));
        assertEquals(MarbleType.GREY, market.getMarble(2,2));
        assertEquals(MarbleType.BLUE, market.getMarble(2,3));
    }

    @Test
    public void chooseColumnTest() {
        market.setSlideMarble(MarbleType.WHITE);
        market.setMarble(0,0, MarbleType.WHITE);
        market.setMarble(0,1, MarbleType.PURPLE);
        market.setMarble(0,2, MarbleType.BLUE);
        market.setMarble(0,3, MarbleType.YELLOW);
        market.setMarble(1,0, MarbleType.PURPLE);
        market.setMarble(1,1, MarbleType.YELLOW);
        market.setMarble(1,2, MarbleType.WHITE);
        market.setMarble(1,3, MarbleType.GREY);
        market.setMarble(2,0, MarbleType.WHITE);
        market.setMarble(2,1, MarbleType.RED);
        market.setMarble(2,2, MarbleType.GREY);
        market.setMarble(2,3, MarbleType.BLUE);

        List<Resource> res = new ArrayList<Resource>(Arrays.asList(market.chooseColumn(2)));
        assertEquals(MarbleType.BLUE.toResource(), res.get(0));
        assertEquals(MarbleType.WHITE.toResource(), res.get(1));
        assertEquals(MarbleType.GREY.toResource(), res.get(2));

        assertEquals(MarbleType.WHITE, market.getMarble(0,0));
        assertEquals(MarbleType.PURPLE, market.getMarble(0,1));
        assertEquals(MarbleType.WHITE, market.getMarble(0,2));
        assertEquals(MarbleType.YELLOW, market.getMarble(0,3));

        assertEquals(MarbleType.PURPLE, market.getMarble(1,0));
        assertEquals(MarbleType.YELLOW, market.getMarble(1,1));
        assertEquals(MarbleType.GREY, market.getMarble(1,2));
        assertEquals(MarbleType.GREY, market.getMarble(1,3));
        assertEquals(MarbleType.BLUE, market.getSlideMarble());

        assertEquals(MarbleType.WHITE, market.getMarble(2,0));
        assertEquals(MarbleType.RED, market.getMarble(2,1));
        assertEquals(MarbleType.WHITE, market.getMarble(2,2));
        assertEquals(MarbleType.BLUE, market.getMarble(2,3));
    }
}