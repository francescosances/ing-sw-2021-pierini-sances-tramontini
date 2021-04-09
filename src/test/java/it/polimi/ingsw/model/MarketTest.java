package it.polimi.ingsw.model;

import it.polimi.ingsw.model.storage.Resource;
import it.polimi.ingsw.model.storage.ResourceType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.print.attribute.standard.PrinterURI;
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
        assertEquals(res.get(0), MarbleType.PURPLE.toResource());
        assertEquals(res.get(1), MarbleType.YELLOW.toResource());
        assertEquals(res.get(2), MarbleType.WHITE.toResource());
        assertEquals(res.get(3), MarbleType.GREY.toResource());

        assertEquals(market.getMarble(0,0), MarbleType.WHITE);
        assertEquals(market.getMarble(0,1), MarbleType.PURPLE);
        assertEquals(market.getMarble(0,2), MarbleType.BLUE);
        assertEquals(market.getMarble(0,3), MarbleType.YELLOW);

        assertEquals(market.getMarble(1,0), MarbleType.YELLOW);
        assertEquals(market.getMarble(1,1), MarbleType.WHITE);
        assertEquals(market.getMarble(1,2), MarbleType.GREY);
        assertEquals(market.getMarble(1,3), MarbleType.WHITE);
        assertEquals(market.getSlideMarble(), MarbleType.PURPLE);

        assertEquals(market.getMarble(2,0), MarbleType.WHITE);
        assertEquals(market.getMarble(2,1), MarbleType.RED);
        assertEquals(market.getMarble(2,2), MarbleType.GREY);
        assertEquals(market.getMarble(2,3), MarbleType.BLUE);
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
        assertEquals(res.get(0), MarbleType.BLUE.toResource());
        assertEquals(res.get(1), MarbleType.WHITE.toResource());
        assertEquals(res.get(2), MarbleType.GREY.toResource());

        assertEquals(market.getMarble(0,0), MarbleType.WHITE);
        assertEquals(market.getMarble(0,1), MarbleType.PURPLE);
        assertEquals(market.getMarble(0,2), MarbleType.WHITE);
        assertEquals(market.getMarble(0,3), MarbleType.YELLOW);

        assertEquals(market.getMarble(1,0), MarbleType.PURPLE);
        assertEquals(market.getMarble(1,1), MarbleType.YELLOW);
        assertEquals(market.getMarble(1,2), MarbleType.GREY);
        assertEquals(market.getMarble(1,3), MarbleType.GREY);
        assertEquals(market.getSlideMarble(), MarbleType.BLUE);

        assertEquals(market.getMarble(2,0), MarbleType.WHITE);
        assertEquals(market.getMarble(2,1), MarbleType.RED);
        assertEquals(market.getMarble(2,2), MarbleType.WHITE);
        assertEquals(market.getMarble(2,3), MarbleType.BLUE);
    }
}