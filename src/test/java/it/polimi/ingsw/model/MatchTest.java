package it.polimi.ingsw.model;

import it.polimi.ingsw.model.cards.Deck;
import it.polimi.ingsw.model.cards.DevelopmentCard;
import it.polimi.ingsw.model.cards.LeaderCard;
import it.polimi.ingsw.model.storage.ResourceType;
import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.view.View;
import it.polimi.ingsw.view.VirtualView;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MatchTest {

    private Match match;

    @Before
    public void setUp() {
        match = new Match("TestMatch",3);
        match.addPlayer("first");
        match.addPlayer("second");
        match.getPlayerBoard("second").getStrongbox().addResources(Map.ofEntries(Map.entry(ResourceType.STONE,5),Map.entry(ResourceType.SHIELD,5),Map.entry(ResourceType.COIN,5),Map.entry(ResourceType.SERVANT,5)));
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

    @Test
    public void buyDevelopmentCardTest(){
        List<Deck<DevelopmentCard>> before = Serializer.deserializeDevelopmentCardsDeckList(Serializer.serializeDevelopmentCardsDeckList(match.getDevelopmentCardDecks()));
        match.buyDevelopmentCard(before.get(0).top(),match.getPlayerBoard("first"));
        assertEquals(before.get(0).top(),match.getDevelopmentCardDecks().get(0).top());
        match.buyDevelopmentCard(before.get(0).top(),match.getPlayerBoard("second"));
        assertNotEquals(before.get(0).top(),match.getDevelopmentCardDecks().get(0).top());
        assertNotEquals(match.getDevelopmentCardDecks().get(0).size(),before.get(0).size());
    }

    @Test
    public void addViewTest(){
        View view = new VirtualView(null,"first");
        int oldSize = match.views.size();
        match.views = null;
        match.addView(view);
        assertNotNull(match.views);
        assertEquals(oldSize+1,match.views.size());
        assertTrue(match.views.contains(view));
    }

    @Test
    public void removeViewTest(){
        View view = new VirtualView(null,"first");
        match.addView(view);
        int oldSize = match.views.size();
        match.removeView(view);
        assertEquals(oldSize-1,match.views.size());
        assertFalse(match.views.contains(view));
    }

}