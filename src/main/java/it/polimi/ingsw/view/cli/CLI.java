package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.controller.ClientController;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.cards.*;
import it.polimi.ingsw.model.storage.*;
import it.polimi.ingsw.utils.Triple;
import it.polimi.ingsw.view.View;

import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CLI implements View {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /**
     * The controller related to this view
     */
    private final ClientController clientController;
    /**
     * The scanner that reads the user inputs
     */
    private final Scanner input;
    /**
     * The default output stream to communicate with the user
     */
    private final PrintStream output;
    /**
     * The output stream used to communicate the errors to the user
     */
    private final PrintStream errorOutput;
    /**
     * Handles synchronization on output stream
     */
    private final Lock outputLock;
    /**
     * Helps granting optimal visualization to different screen
     */
    private final boolean lightMode;

    private static final String SPEND = "spend";

    private static final String GAIN = "gain";

    public CLI(ClientController clientController, boolean lightMode) {
        this.clientController = clientController;
        this.input = new Scanner(System.in);
        this.output = System.out;
        this.errorOutput = System.out;
        this.lightMode = lightMode;
        this.outputLock = new ReentrantLock();
    }

    @Override
    public void showMessage(String message) {
        output.println(message);
    }

    @Override
    public void showErrorMessage(String message) {
        errorOutput.println(ANSI_RED + message + ANSI_RESET);
    }

    @Override
    public void init() {
        output.println("Welcome");
        askConnection();
    }

    public void askConnection() {
        output.print("Insert server address: ");
        String serverAddress = input.next();
        output.print("Insert server port: ");
        int serverPort = input.nextInt();
        try {
            clientController.connect(serverAddress, serverPort);
        } catch (Exception e) {
            showErrorMessage("An error has occurred. Try again");
            askConnection();
        }
    }

    @Override
    public void askLogin() {
        output.print("Insert desired username: ");
        String username = input.next();
        clientController.login(username);
    }

    @Override
    public void waitForOtherPlayers() {
        outputLock.lock();
        output.println("************");
        output.println("Waiting for other players");
        output.println("************");
        outputLock.unlock();
    }

    @Override
    public void listLobbies(List<Triple<String, Integer, Integer>> availableLobbies) {
        outputLock.lock();
        output.println("Create a new match or join one:");
        output.println("  [0] New match");
        int choice;
        if (availableLobbies != null) {
            for (int i = 1; i <= availableLobbies.size(); i++) {
                output.print("  [" + i + "] ");
                output.print(availableLobbies.get(i - 1).getFirst() + "'s match ");
                output.println("(" + availableLobbies.get(i - 1).getSecond() + "/" + availableLobbies.get(i - 1).getThird() + ")");
            }
            output.println("\n" + ANSI_WHITE + " Insert a negative number to refresh" + ANSI_RESET);
            outputLock.unlock();
            choice = input.nextInt();
        } else {
            outputLock.unlock();
            choice = 0;
        }
        if (choice == 0) {
            int playersNumber;
            do {
                output.println("Insert number of players:");
                playersNumber = input.nextInt();
            } while (playersNumber <= 0 || playersNumber > Match.MAX_PLAYERS);
            clientController.createNewLobby(playersNumber);
        } else if (choice > 0 && choice <= availableLobbies.size())
            clientController.lobbyChoice(availableLobbies.get(choice - 1).getFirst(), availableLobbies.get(choice - 1).getThird());
        else
            clientController.refreshLobbies();
    }

    @Override
    public void resumeMatch(PlayerBoard playerBoard) {
        output.println("You have been reconnected to your previous match");
    }

    @Override
    public void userConnected(String username) {
        output.println("\n" + username + " has joined the match");
    }

    @Override
    public void userDisconnected(String username) {
        output.println("\n" + username + " has been disconnected");
    }

    @Override
    public void listLeaderCards(List<LeaderCard> leaderCardList, int cardsToChoose) {
        outputLock.lock();
        output.println();
        printLeaderCards(leaderCardList);
        output.println("Choose " + cardsToChoose + " leader cards:");
        outputLock.unlock();
        int[] choices = new int[cardsToChoose];
        LeaderCard[] cardsChosen = new LeaderCard[cardsToChoose];
        for (int i = 0; i < cardsToChoose; i++) {
            choices[i] = input.nextInt();
            if (choices[i] < 0 && cardsToChoose == 1) {
                clientController.rollback();
                return;
            }
            for (int j = 0; j < i; j++) {
                if (choices[i] == choices[j]) {
                    showErrorMessage("You chose the same card twice");
                    listLeaderCards(leaderCardList, cardsToChoose);
                    return;
                }
            }
            if (choices[i] < 0 || choices[i] >= leaderCardList.size()) {
                showErrorMessage("Invalid choice");
                listLeaderCards(leaderCardList, cardsToChoose);
                return;
            }
            cardsChosen[i] = leaderCardList.get(choices[i]);
        }
        clientController.leaderCardsChoice(cardsChosen);
    }

    @Override
    public void showPlayerLeaderCards(List<LeaderCard> leaderCardList) {
        output.println();
        while (true) {
            outputLock.lock();
            output.println("You have these leader cards");
            printLeaderCards(leaderCardList);
            output.println("Select which leader card you want to activate or discard");
            output.println(ANSI_WHITE + "Insert a negative number to reverse the action" + ANSI_RESET);
            outputLock.unlock();
            int choice = input.nextInt();

            if (choice < 0) {
                clientController.rollback();
                return;
            } else if (choice == 0 || choice == 1) {
                while (true) {
                    outputLock.lock();
                    output.println("What do you want to do with this card?");
                    output.println(ANSI_WHITE + "Insert a negative number to reverse the action" + ANSI_RESET);
                    output.println(" [0] Discard");
                    output.println(" [1] Activate");
                    outputLock.unlock();

                    int action = input.nextInt();

                    if (action < 0) {
                        clientController.rollback();
                        return;
                    } else if (action == 0) {
                        clientController.discardLeaderCard(choice);
                        return;
                    } else if (action == 1) {
                        clientController.activateLeaderCard(choice);
                        return;
                    }
                    errorOutput.println("Invalid choice");
                }
            }
            errorOutput.println("Invalid choice");
        }
    }

    @Override
    public void showLeaderCards(List<LeaderCard> leaderCards) {
        outputLock.lock();
        output.println(clientController.getCurrentActiveUser() + " changed " + getPronoun() + "leader cards hand");
        printLeaderCards(leaderCards);
        outputLock.unlock();
    }

    private String getPronoun() {
        return clientController.getCurrentActiveUser().equals(Match.YOU_STRING) ?
                "your" : "their";
    }

    @Override
    public void showDevelopmentCardSlots(DevelopmentCardSlot[] developmentCardSlots) {
        outputLock.lock();
        output.println("\n" + clientController.getCurrentActiveUser() + " bought a card!");
        output.print("Now " + getPronoun());
        output.println(" development card slots are:");
        printDevelopmentCards(developmentCardSlots);
        outputLock.unlock();
    }

    private String developmentCardColor(DevelopmentColorType card) {
        switch (card) {
            case BLUE:
                return ANSI_BLUE;
            case YELLOW:
                return ANSI_YELLOW;
            case PURPLE:
                return ANSI_PURPLE;
            case GREEN:
                return ANSI_GREEN;
        }
        return "";
    }


    @Override
    public void listDevelopmentCards(List<Deck<DevelopmentCard>> developmentCardList, int cardsToChoose, PlayerBoard playerBoard) {
        outputLock.lock();
        output.println();
        List<DevelopmentCard> availableCards = new ArrayList<>();
        for (Deck<DevelopmentCard> deck : developmentCardList) {
            output.print(developmentCardColor(deck.top().getColor()));
            output.printf("%s level %d\n", deck.top().getColor(), deck.top().getLevel());
            DevelopmentCard developmentCard = deck.top();
            if (!developmentCard.getCost().satisfied(playerBoard) || !playerBoard.acceptsDevelopmentCard(developmentCard))
                output.print(ANSI_WHITE + "[X]");
            else {
                output.printf(developmentCardColor(developmentCard.getColor()) + "[%d]", availableCards.size());
                availableCards.add(developmentCard);
            }
            output.println(developmentCard);
        }
        output.print(ANSI_RESET);

        output.println("Choose " + cardsToChoose + " development card:");
        output.println(ANSI_WHITE + "Insert a negative number to reverse the action" + ANSI_RESET);
        outputLock.unlock();
        int[] choices = new int[cardsToChoose];
        DevelopmentCard[] cardsChosen = new DevelopmentCard[cardsToChoose];
        for (int i = 0; i < cardsToChoose; i++) {
            choices[i] = input.nextInt();
            for (int j = 0; j < i; j++) {
                if (choices[i] == choices[j]) {
                    showErrorMessage("You chose the same card twice");
                    listDevelopmentCards(developmentCardList, cardsToChoose, playerBoard);
                    return;
                }
            }
            if (choices[i] >= availableCards.size()) {
                showErrorMessage("Invalid choice");
                listDevelopmentCards(developmentCardList, cardsToChoose, playerBoard);
                return;
            } else if (choices[i] < 0) {
                clientController.rollback();
                return;
            }
            cardsChosen[i] = availableCards.get(choices[i]);
        }
        clientController.chooseDevelopmentCards(cardsChosen);
    }

    @Override
    public void askToChooseDevelopmentCardSlot(DevelopmentCardSlot[] slots, DevelopmentCard developmentCard) {
        outputLock.lock();
        output.println("\nWhere do you want to put the chosen card?");
        int index = 0;
        for (DevelopmentCardSlot slot : slots) {
            if (slot.accepts(developmentCard))
                output.printf("[%d] %s\n", index, slot);
            else
                output.printf(ANSI_WHITE + "[X] %s\n" + ANSI_RESET, slot);
            index++;
        }
        outputLock.unlock();
        int choice = input.nextInt();
        if (choice < 0 || choice >= slots.length) {
            showErrorMessage("Invalid choice");
            askToChooseDevelopmentCardSlot(slots, developmentCard);
            return;
        }
        clientController.chooseDevelopmentCardsSlot(choice);
    }

    private String formatResourceString(String original) {
        String temp = "           " + original;
        return temp.substring(temp.length() - 11);
    }

    private String getProductionFirstRow(Requirements requirements) {
        Map.Entry<Resource, Integer> entry = requirements.iterator().next();
        if (requirements.getResourceRequirementsSize() == 1)
            return "  " + formatResourceString("");
        return entry.getValue() + " " + formatResourceString(entry.getKey().toString());
    }

    private String getProductionSecondRow(Requirements requirements) {
        Iterator<Map.Entry<Resource, Integer>> iterator = requirements.iterator();
        Map.Entry<Resource, Integer> entry = iterator.next();
        if (requirements.getResourceRequirementsSize() != 2) {
            if (requirements.getResourceRequirementsSize() == 3)
                entry = iterator.next();
            return entry.getValue() + " " + formatResourceString(entry.getKey().toString());
        }
        return "  " + formatResourceString("");
    }

    private String getProductionThirdRow(Requirements requirements) {
        Iterator<Map.Entry<Resource, Integer>> iterator = requirements.iterator();
        Map.Entry<Resource, Integer> entry = null;
        if (requirements.getResourceRequirementsSize() > 1) {
            for (int i = 0; i < requirements.getResourceRequirementsSize(); i++)
                entry = iterator.next();
            return entry.getValue() + " " + formatResourceString(entry.getKey().toString());
        }
        return "  " + formatResourceString("");
    }

    private void printProduction(int index, Producer producer) {
        Requirements cost = producer.getProductionCost();
        Requirements gain = producer.getProductionGain();

        output.printf("[%d]  %s ╗  %s\n", index, getProductionFirstRow(cost), getProductionFirstRow(gain));
        output.printf("     %s ╟> %s\n", getProductionSecondRow(cost), getProductionSecondRow(gain));
        output.printf("     %s ╝  %s\n", getProductionThirdRow(cost), getProductionThirdRow(gain));
    }

    private void listAvailableProductions(List<Producer> availableProductions) {
        int counter = 0;
        for (Producer producer : availableProductions) {
            printProduction(counter++, producer);
        }
    }

    @Override
    public void chooseProductions(List<Producer> availableProductions, PlayerBoard playerBoard) {
        outputLock.lock();
        output.println();
        listAvailableProductions(availableProductions);
        List<Integer> choices = new ArrayList<>();
        int temp;
        output.println("Choose the productions to activate.");
        output.println(ANSI_WHITE + "Insert a negative number to exit." + ANSI_RESET);
        outputLock.unlock();
        temp = input.nextInt();
        if (temp < 0) {
            clientController.rollback();
            return;
        }
        Requirements onDemandCosts = new Requirements();
        Requirements onDemandGains = new Requirements();
        while (temp >= 0) {
            if (choices.contains(temp))
                showErrorMessage("You have already chosen to produce this");
            else {
                try {
                    choices.add(temp);
                    Producer producer = availableProductions.get(temp);
                    onDemandCosts.addResourceRequirement(NonPhysicalResourceType.ON_DEMAND, producer.getProductionCost().getResources(NonPhysicalResourceType.ON_DEMAND));
                    onDemandGains.addResourceRequirement(NonPhysicalResourceType.ON_DEMAND, producer.getProductionGain().getResources(NonPhysicalResourceType.ON_DEMAND));
                } catch (IndexOutOfBoundsException e) {
                    showErrorMessage("You don't have a Producer associated with this number");
                }
            }
            temp = input.nextInt();
        }

        askToChooseProductionCosts(onDemandCosts);
        askToChooseProductionGains(onDemandGains);

        clientController.chooseProductions(choices, onDemandCosts, onDemandGains);
    }

    public void askToChooseProductionCosts(Requirements requirements) {
        chooseOnDemandResources(requirements,SPEND);
    }

    public void askToChooseProductionGains(Requirements requirements) {
        chooseOnDemandResources(requirements,GAIN);
    }

    private void chooseOnDemandResources(Requirements entries, String string) {
        while (entries.getResources(NonPhysicalResourceType.ON_DEMAND) > 0) {
            outputLock.lock();
            output.println("You have to choose " + entries.getResources(NonPhysicalResourceType.ON_DEMAND) + " resources to " + string);
            output.println("What resource do you want to "+string+"?");
            this.printResources(ResourceType.values(), true);
            outputLock.unlock();
            int choice;
            do {
                choice = input.nextInt();
            } while (choice < 0 || choice > ResourceType.values().length);
            entries.removeResourceRequirement(NonPhysicalResourceType.ON_DEMAND, 1);
            entries.addResourceRequirement(ResourceType.values()[choice], 1);
        }
    }

    @Override
    public void showCurrentActiveUser(String username) {
        outputLock.lock();
        output.println();
        output.println("************");
        output.print("It's ");
        if (clientController.getCurrentActiveUser().equals(Match.YOU_STRING))
            output.print("your");
        else
            output.print(username + "'s");
        output.println(" turn");
        output.println("************");
        outputLock.unlock();
    }

    @Override
    public void askToChooseStartResources(Resource[] values, int resourcesToChoose) {
        outputLock.lock();
        output.printf("\nYou have to select %d resources of your choice\n", resourcesToChoose);
        printResources(values, true);
        int[] choices = new int[resourcesToChoose];
        Resource[] resourcesChosen = new Resource[resourcesToChoose];
        outputLock.unlock();
        for (int i = 0; i < resourcesToChoose; i++) {
            choices[i] = input.nextInt();
            if (choices[i] < 0 || choices[i] >= values.length) {
                showErrorMessage("Invalid choice");
                askToChooseStartResources(values, resourcesToChoose);
                return;
            }
            resourcesChosen[i] = values[choices[i]];
        }
        clientController.chooseStartResources(resourcesChosen);
    }

    @Override
    public void showPlayers(Map<String, Boolean> users) {
        outputLock.lock();
        output.println();
        clientController.setPlayers(new ArrayList<>(users.keySet()));
        output.println("The players of this match are:");
        for (String user : users.keySet()) {
            output.print("- " + user);
            if (!users.get(user))
                output.print("\t\t- not active");
            output.print("\n");
        }
        outputLock.unlock();
    }

    @Override
    public void showActionToken(ActionToken actionToken) {
        outputLock.lock();
        output.println();
        output.println("An action token was drawn!");
        if (actionToken.getDevelopmentCard() != null) {
            output.print("Two ");
            output.print(developmentCardColor(actionToken.getDevelopmentCard()) + actionToken.getDevelopmentCard() + " development cards " + ANSI_RESET);
            output.println("were discarded");
        }
        else {
            output.print("Black cross moved by " + actionToken.getBlackCrossSpaces() + " space");
            if (actionToken.getBlackCrossSpaces() == 1){
                output.println("\nAll action token were shuffled");
            } else
                output.println("s");
        }
        outputLock.unlock();
    }

    @Override
    public void showProducerUser(PlayerBoard playerBoard) {
        output.println("\n" + clientController.getCurrentActiveUser() + " decided to produce");
    }

    @Override
    public void actionPerformed() {
    }

    @Override
    public void showEndGameTriggered() {
        output.println("\n"+ clientController.getCurrentActiveUser()+" has played " +getPronoun() + "last turn!");
    }

    @Override
    public void showCharts(List<PlayerBoard> playerList) {
        outputLock.lock();
        output.println("\nMatch ended!\nThe chart is:");
        for (int i = 0; i < playerList.size(); i++) {
            output.println();
            output.println(i + "° place: " + playerList.get(i));
            output.println("Total victory points: " + playerList.get(i).getTotalVictoryPoints());
            showPlayerBoard(playerList.get(i));
        }
        if (clientController.getUsername().equals(playerList.get(0).getUsername()))
            output.println("You won!");
        else
            output.println(playerList.get(0).getUsername() + " won!");
    }

    @Override
    public String getUsername() {
        return clientController.getUsername();
    }


    @Override
    public void showPlayerBoard(PlayerBoard playerBoard) {
        outputLock.lock();
        output.println("\n" + playerBoard.getUsername() + "'s player board is");
        printFaithTrack(playerBoard.getFaithTrack());
        printStorage(playerBoard);
        printDevelopmentCards(playerBoard.getDevelopmentCardSlots());
        printLeaderCards(playerBoard.getLeaderCards());
        outputLock.unlock();
    }

    private void printCross(int pos, FaithTrack faithTrack) {
        if (pos == faithTrack.getFaithMarker())
            if (faithTrack.isBlackCross())
                output.print("†");
            else
                output.print(ANSI_RED + "†" + ANSI_RESET);
        else
            output.print(" ");
    }

    @Override
    public void showFaithTrack(FaithTrack faithTrack) {
        outputLock.lock();
        output.println();
        String name;
        if (faithTrack.getUsername().equals(clientController.getUsername()))
            name = Match.YOU_STRING;
        else
            name = faithTrack.getUsername();
        output.println(name + " got faith points!");
        output.println("Now " +
                (faithTrack.getUsername().equals(clientController.getUsername()) ? "your" : "their") +
                " faith track is:");
        printFaithTrack(faithTrack);
        outputLock.unlock();
    }

    private void printFaithTrack(FaithTrack faithTrack) {
        output.println("Faith track: " + faithTrack.getTrackVictoryPoints() + " victory points");

        printCross(0, faithTrack);
        output.print(" ");
        int vaticanReportCount = 0;
        for (int pos = 1; pos <= FaithTrack.SIZE; pos++) {
            if (pos == FaithTrack.POPE_SPACES[vaticanReportCount]) {
                output.print(ANSI_RED + "[" + ANSI_RESET);
                printCross(pos, faithTrack);
                output.print(ANSI_RED + "]" + ANSI_RESET);
                vaticanReportCount++;
            } else if ((pos >= FaithTrack.POPE_SPACES[vaticanReportCount] - 3 - vaticanReportCount) && (pos <= FaithTrack.POPE_SPACES[vaticanReportCount] - 1)) {
                output.print(ANSI_YELLOW + "[" + ANSI_RESET);
                printCross(pos, faithTrack);
                output.print(ANSI_YELLOW + "]" + ANSI_RESET);
            } else {
                output.print("[");
                printCross(pos, faithTrack);
                output.print("]");
            }
        }
        output.println();

        printPopeFavorTiles(faithTrack);
    }

    @Override
    public void showVaticanReportTriggered(String username, int vaticanReportCount) {
        output.println("\n" + username + " triggered the " + vaticanReportCount + "° Vatican report!");
    }

    private void printPopeFavorTiles(FaithTrack faithTrack) {
        if (faithTrack.isBlackCross())
            return; //Black Cross doesn't have PopeFavorTiles
        output.println("Pope favor tiles: " + faithTrack.getPopeFavorTilesVictoryPoints() + " victory points");
        for (PopeFavorTile tile: faithTrack.getPopeFavorTiles()) {
            output.print("  ");
            if (tile == null)
                output.println("discarded");
            else
                output.println(tile);
        }
    }

    private void printStorage(PlayerBoard playerBoard) {
        output.println("Storage: " + playerBoard.getResourcesVictoryPoints() + " victory points");
        printWarehouse(playerBoard.getWarehouse());
        printStrongbox(playerBoard.getStrongbox());
    }

    @Override
    public void showWarehouse(Warehouse warehouse) {
        outputLock.lock();
        output.println("\n" + clientController.getCurrentActiveUser() + " changed " + getPronoun() + " warehouse");
        printWarehouse(warehouse);
        outputLock.unlock();
    }

    private void printWarehouse(Warehouse warehouse) {
        output.println("Depots:");
        for (int i = 0; i < warehouse.getDepots().size(); i++) {
            Depot depot = warehouse.getDepots().get(i);
            output.printf("  [%d] ", i);
            for (int j = 0; j < depot.getSize(); j++)
                output.print("[" + ((depot.getOccupied() > j) ? printResource(depot.getResourceType()) : " ") + "]");
            for (int j = 10 - depot.getSize() * 3; j > 0; j--)
                output.print(" ");
            output.print(depot.getResourceType() == null ? "Empty" : depot.getResourceType().toString());
            output.println();
        }
    }

    private String printResource(ResourceType resource) {
        String res;
        switch (resource) {
            case SHIELD:
                res = ANSI_BLUE + "*";
                break;
            case STONE:
                res = (lightMode) ? ANSI_BLACK + "*" : ANSI_WHITE + "*";
                break;
            case COIN:
                res = ANSI_YELLOW + "*";
                break;
            case SERVANT:
                res = ANSI_PURPLE + "*";
                break;
            default:
                res = " ";
        }
        return res + ANSI_RESET;
    }

    @Override
    public void showStrongbox(Strongbox strongbox) {
        outputLock.lock();
        output.println("\n" + clientController.getCurrentActiveUser() + " changed " + getPronoun() + " strongbox");
        printStrongbox(strongbox);
        outputLock.unlock();
    }

    private void printStrongbox(Strongbox strongbox){
        output.println("Strongbox:");
        for (Map.Entry<Resource, Integer> res : strongbox.getAllResources())
            output.println("  " + res.getKey() + ": " + res.getValue());
    }

    private void printDevelopmentCards(DevelopmentCardSlot[] developmentCardSlots) {
        int victoryPoints = Arrays.stream(developmentCardSlots)
                .mapToInt(DevelopmentCardSlot::getVictoryPoints)
                .sum();
        output.println("Development cards: " + victoryPoints + " victory points");
        for (int i = 0; i < developmentCardSlots.length; i++) {
            final DevelopmentCardSlot developmentCardSlot = developmentCardSlots[i];
            if (developmentCardSlot.getSize() > 0) {
                output.println("  Slot [" + i + "]:");
                developmentCardSlot.forEach(developmentCard -> {
                    if (developmentCard.equals(developmentCardSlot.getTopCard()))
                        output.println("    Active: " + developmentCard);
                    else {
                        output.print("           color: " + developmentCard.getColor());
                        output.println(" victory points: " + developmentCard.getVictoryPoints());
                    }
                });
            }
        }
    }

    private void printLeaderCards(List<LeaderCard> leaderCardList) {
        int victoryPoints = leaderCardList.stream()
                .filter(LeaderCard::isActive)
                .mapToInt(LeaderCard::getVictoryPoints)
                .sum();
        output.println("Leader cards: " + victoryPoints + " victory points");
        for (int i = 0; i < leaderCardList.size(); i++) {
            output.print("  [" + i + "] ");
            output.println(leaderCardList.get(i).toString());
        }
    }

    @Override
    public void askToSwapDepots(Warehouse warehouse) {
        outputLock.lock();
        output.println();
        this.printWarehouse(warehouse);
        output.println("Select 2 depots to swap:");
        outputLock.unlock();
        int depotA = input.nextInt();
        int depotB = input.nextInt();
        if (depotA < 0 || depotA >= warehouse.getDepots().size() || depotB < 0 || depotB >= warehouse.getDepots().size()) {
            showErrorMessage("Invalid choice");
            askToSwapDepots(warehouse);
        } else {
            clientController.swapDepots(depotA, depotB);
        }
    }

    /**
     * Show a single marble whose color depends on the type of marble
     *
     * @param marbleType the type of marble
     * @return a string containing the colored circle inside two brackets
     */
    private String printMarble(MarbleType marbleType) {
        String marble = "[";
        switch (marbleType) {
            case RED:
                marble += ANSI_RED + "●";
                break;
            case BLUE:
                marble += ANSI_BLUE + "●";
                break;
            case GREY:
                marble += (lightMode) ? ANSI_BLACK + "●" : ANSI_WHITE + "◯";
                break;
            case YELLOW:
                marble += ANSI_YELLOW + "●";
                break;
            case PURPLE:
                marble += ANSI_PURPLE + "●";
                break;
            case WHITE:
                marble += (lightMode) ? ANSI_WHITE + "◯" : "●";
                break;
            default:
                marble = " ";
        }
        return marble + ANSI_RESET + "]";
    }

    @Override
    public void showMarket(Market market) {
        outputLock.lock();
        output.println("\n" + clientController.getCurrentActiveUser() + " took resources from market!");
        output.println("Now the market is:");
        printMarket(market);
        outputLock.unlock();
    }

    private void printMarket(Market market) {
        for (int i = 0; i < Market.COLUMNS * 3; i++)
            output.print(" ");
        output.println(printMarble(market.getSlideMarble()));
        for (int r = 0; r < Market.ROWS; r++) {
            for (int c = 0; c < Market.COLUMNS; c++) {
                output.print(printMarble(market.getMarble(r, c)));
            }
            output.println();
        }
    }

    @Override
    public void showResourcesGainedFromMarket(Resource[] resources) {
        outputLock.lock();
        output.println("\nThese are the resources gained from market needing to be stored:");
        printResources(resources, false);
        outputLock.unlock();
    }

    private void printResources(Resource[] resources, boolean showNumbers) {
        List<Resource> resourceList = Arrays.asList(resources);
        Collections.reverse(resourceList);
        for (int i = 0; i < resourceList.size(); i++) {
            if (showNumbers)
                output.printf("  [%d]", i);
            output.println("  " + resourceList.get(i));
        }
    }

    @Override
    public void askToStoreResource(Resource resource, Warehouse warehouse) {
        outputLock.lock();
        output.println("\nYou have to store a "+ resource);
        output.println("Where do you want to store this " + resource + " resource?");
        printWarehouse(warehouse);
        output.printf("  [%d] Move resources\n", warehouse.getDepots().size());
        output.printf("  [%d] Discard\n", warehouse.getDepots().size() + 1);
        outputLock.unlock();
        int choice = input.nextInt();
        if (choice < 0 || choice > warehouse.getDepots().size() + 1) {
            showErrorMessage("Invalid choice");
            askToStoreResource(resource, warehouse);
            return;
        }
        if (choice == warehouse.getDepots().size()) {
            askToSwapDepots(warehouse);
            return;
        }
        clientController.chooseDepot(choice);

    }

    @Override
    public void chooseWhiteMarbleConversion(LeaderCard card1, LeaderCard card2) {
        outputLock.lock();
        output.println("\nYou have 2 active white marble leader cards. Choose conversion output:");
        output.println("  [0] " + card1.getOutputResourceType());
        output.println("  [1] " + card2.getOutputResourceType());
        outputLock.unlock();
        int choice = input.nextInt();
        if (choice < 0 || choice >= 2) {
            showErrorMessage("Invalid choice");
            chooseWhiteMarbleConversion(card1, card2);
            return;
        }
        clientController.chooseWhiteMarbleConversion(choice);
    }

    @Override
    public void takeResourcesFromMarket(Market market) {
        output.println();
        printMarket(market);
        int choice;
        do {
            outputLock.lock();
            output.println("Do you want to choose a row or a column?");
            output.println("  [0] Row");
            output.println("  [1] Column");
            output.println(ANSI_WHITE + "Insert a negative number to reverse the action" + ANSI_RESET);
            outputLock.unlock();
            choice = input.nextInt();
            if (choice < 0) {
                clientController.rollback();
                return;
            } else if (choice != 0 && choice != 1)
                showErrorMessage("Invalid choice");
        } while (choice != 0 && choice != 1);

        if (choice == 0) {//rows
            output.println("Which row would you like to choose? [0-" + (Market.ROWS - 1) + "]");
            int row = input.nextInt();
            if (row < 0 || row >= Market.ROWS) {
                showErrorMessage("Invalid choice");
                takeResourcesFromMarket(market);
                return;
            }
            clientController.chooseMarketRow(row);
        } else {//columns
            output.println("Which column would you like to choose? [0-" + (Market.COLUMNS - 1) + "]");
            int column = input.nextInt();
            if (column < 0 || column >= Market.COLUMNS) {
                showErrorMessage("Invalid choice");
                takeResourcesFromMarket(market);
                return;
            }
            clientController.chooseMarketColumn(column);
        }
    }

    @Override
    public void askForAction(List<String> usernames, Action... availableActions) {
        outputLock.lock();
        output.println("\nWhich action do you want to perform?");
        for (int i = 0; i < availableActions.length; i++) {
            output.print("  [" + i + "] ");
            output.println(availableActions[i].toString());
        }
        outputLock.unlock();
        int choice = input.nextInt();
        if (choice >= 0 && choice < availableActions.length) {
            if (Arrays.asList(availableActions).contains(Action.SHOW_PLAYER_BOARD) &&
                    Arrays.asList(availableActions).indexOf(Action.SHOW_PLAYER_BOARD) == choice) {
                choosePlayerBoard(usernames);
                return;
            }
            clientController.performAction(availableActions[choice]);
        } else {
            showErrorMessage("Invalid choice");
            askForAction(usernames, availableActions);
        }
    }

    private void choosePlayerBoard(List<String> usernames){
        output.println("\nWhose player board do you want to see?");
        for (int i = 0; i < usernames.size(); i++) {
            output.print("[" + i + "] ");
            output.println(usernames.get(i));
        }
        int player = input.nextInt();
        while (player < 0 || player >= usernames.size())
            showErrorMessage("Invalid choice");
        clientController.showPlayerBoard(usernames.get(player));
    }
}
