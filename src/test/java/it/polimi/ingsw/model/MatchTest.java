package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.LeaderCard;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class MatchTest {

    private Match match;

    @Before
    public void setUp() {
        match = new Match("TestMatch",3);
        match.addPlayer("first");
        match.addPlayer("second");
    }

    @After
    public void tearDown() throws Exception {
        match = null;
    }

    @Test
    public void drawLeaderCardsTest(){
        LeaderCard before = match.getLeaderCards().top();
        List<LeaderCard> drawn = match.drawLeaderCards(1);

        assertEquals(before,drawn.get(0));
    }

    @Test
    public void chooseLeaderCardTest(){
        LeaderCard toBeRemoved = match.getLeaderCards().get(0);
        match.chooseLeaderCard(toBeRemoved);

        assertNotEquals(match.getLeaderCards().get(0), toBeRemoved);
    }

    @Test
    public void containsUsernameTest(){
        for(PlayerBoard playerBoard:match.getPlayers()){
            assertTrue(match.containsUsername(playerBoard.getUsername()));
        }
        assertFalse(match.containsUsername(null));
    }

    @Test
    public void getPlayerBoardTest(){
        String username = match.getPlayers().get(0).getUsername();
        assertEquals(match.getPlayerBoard(username), match.players.get(0));

        assertThrows(IllegalArgumentException.class,()-> match.getPlayerBoard(null));
    }

    @Test
    public void addPlayerTest(){
        assertThrows(IllegalArgumentException.class,()->match.addPlayer("first"));
        assertThrows(IllegalStateException.class,()->{
            match.addPlayer("third");
            match.addPlayer("invalid");
        });

        assertTrue(match.players.stream().anyMatch(playerBoard -> playerBoard.getUsername().equals("third")));
    }

    @Test
    public void discardResourceTest(){
        List<Integer> markerPositionsBefore = new ArrayList<>();

        for(PlayerBoard playerBoard: match.getPlayers())
            markerPositionsBefore.add(playerBoard.getFaithTrack().getFaithMarker());

        match.discardResource(match.getPlayerBoard(match.getPlayers().get(0).getUsername()));

        for(Integer index:markerPositionsBefore){
            if(index==0)
                assertEquals(markerPositionsBefore.get(0).intValue(),match.getPlayers().get(0).getFaithTrack().getFaithMarker());
            else
                assertEquals(markerPositionsBefore.get(index) +1,match.getPlayers().get(index).getFaithTrack().getFaithMarker());
        }
    }

}