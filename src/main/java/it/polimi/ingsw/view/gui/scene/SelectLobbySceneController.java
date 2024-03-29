package it.polimi.ingsw.view.gui.scene;

import it.polimi.ingsw.serialization.Serializer;
import it.polimi.ingsw.utils.Triple;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextInputDialog;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SelectLobbySceneController extends SceneController {

    /**
     * The choiceBox used to show the available lobbies
     */
    @FXML
    private ChoiceBox<Triple<String,Integer,Integer>> lobbySelector;

    /**
     * Fill the choiceBox with the available matches
     * @param availableLobbies the lobbies that the user can join
     */
    @FXML
    public void initialize(List<Triple<String, Integer, Integer>> availableLobbies){
        availableLobbies.add(0,new Triple<>("Create new match",0,0));
        lobbySelector.setConverter(new StringConverter<>() {

            @Override
            public String toString(Triple<String, Integer, Integer> temp) {
                if(temp == null)return "";
                if (temp.getSecond() == 0 && temp.getThird() == 0)
                    return temp.getFirst();
                return temp.getFirst() + " (" + temp.getSecond() + "/" + temp.getThird() + ")";
            }

            @Override
            public Triple<String, Integer, Integer> fromString(String s) {
                Pattern pattern = Pattern.compile("^(.*)\\(([0-9])+/([0-9])+\\)$");
                Matcher matcher = pattern.matcher(s);

                if (matcher.find()) {
                    return new Triple<>(matcher.group(1), Serializer.deserializeInt(matcher.group(2)), Serializer.deserializeInt(matcher.group(3)));
                }
                return new Triple<>(s, 0, 0);
            }
        });
        lobbySelector.setItems(FXCollections.observableArrayList(availableLobbies));
        lobbySelector.setValue(availableLobbies.get(0));
    }

    /**
     * Show the prompt used to ask the number of player of the new match
     * @return an optional containing the number of player of the new match. Empty if the back button has been pressed
     */
    private Optional<Integer> askNumberOfPlayers(){
        TextInputDialog td = new TextInputDialog();
        td.setHeaderText("Choose number of players:");
        td.setContentText("Insert a number between 1 and 4");
        Optional<String> dialogResult = td.showAndWait();
        td.getDialogPane().setStyle("-fx-font-family: Arial");
        if (dialogResult.isPresent()) {
            try {
                int res = Serializer.deserializeInt(dialogResult.get());
                if(res < 0 || res > 4)
                    return askNumberOfPlayers();
                return Optional.of(res);
            }catch (Exception e){
                return askNumberOfPlayers();
            }
        } else {
            // cancel have been pressed.
            return Optional.empty();
        }
    }

    /**
     * Ask to the clientcontroller to refresh the list of available matches
     */
    @FXML
    public void refresh(){
        clientController.refreshLobbies();
    }

    /**
     * Join the selected match
     */
    @FXML
    public void join(){
        Triple<String,Integer,Integer> selected = lobbySelector.getValue();
        if(selected == null)
            return;
        if(selected.getSecond() == 0 && selected.getThird() == 0) {
            Optional<Integer> result = askNumberOfPlayers();
            result.ifPresent(integer -> clientController.createNewLobby(integer));
        } else
            clientController.lobbyChoice(selected.getFirst(),selected.getThird());
    }

}
