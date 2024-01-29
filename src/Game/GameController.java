package Game;

import Cards.Card;
import Queue.Player;
import Queue.PlayerQueue;
import utility.Display;
import Exceptions.GameStateException;
import java.util.List;
public class GameController {

    private static GameController instance;
    boolean gameFinished;
    int RoundCounter =0;
    final PlayerQueue playerQueue;
    private final Object playerQueueLock ;
    private final Object gameStatusLock ;


    private GameController() {
        playerQueue = PlayerQueue.getInstance();
        playerQueueLock =  new Object();
        gameStatusLock = new Object();
    }

    public static GameController getInstance() {
        if (instance == null) {
            instance = new GameController();
        }
        return instance;
    }

    public void playerTurn(Player currentPlayer) throws InterruptedException {
        synchronized (playerQueueLock) {
            if (isGameOver()) {
                gameFinished = true;
                return;
            }
            System.out.println("***************************** ROUND " + ++RoundCounter + " *****************************");
            CheckPlayers();
            executePlayerActions(currentPlayer);

            playerQueueLock.notifyAll();
        }
    }

    private void executePlayerActions(Player currentPlayer) throws InterruptedException {
        List<Card> playerHand = currentPlayer.getCardsInHand();
        Display.printHands();

        if (playerHand.isEmpty()) return;

        Player nextPlayer = getNextPlayer(currentPlayer);
        if (nextPlayer == null) return;

        Card takenCard = takeCardFromNextPlayer(nextPlayer, currentPlayer);
        if (takenCard != null) {
            processTakenCard(currentPlayer, playerHand, takenCard);
        }

        checkAndRemovePlayerIfHandEmpty(currentPlayer);
    }

    private Player getNextPlayer(Player currentPlayer) throws InterruptedException {
        Player nextPlayer = playerQueue.getNextPlayer();
        if (nextPlayer == currentPlayer) return null;
        return nextPlayer;
    }

    private Card takeCardFromNextPlayer(Player nextPlayer, Player currentPlayer) throws InterruptedException {
        Card takenCard = nextPlayer.takeRandomCard();
        System.out.println("Player " + currentPlayer.getPlayerID() + " took " + takenCard + " from Player " + nextPlayer.getPlayerID());
        if (nextPlayer.getCardsInHand().isEmpty()) {
            playerQueue.removePlayer();
            System.out.println("Player " + nextPlayer.getPlayerID() + " has discarded all their cards and is removed from the game!");
            playerQueue.getNextPlayer();
        }
        return takenCard;
    }

    private void processTakenCard(Player currentPlayer, List<Card> playerHand, Card takenCard) {
        boolean matched = false;
        for (int i = 0; i < playerHand.size(); i++) {
            Card playerCard = playerHand.get(i);
            if (playerCard != null && takenCard.matches(playerCard)) {
                System.out.println("Player " + currentPlayer.getPlayerID() + " matched " + takenCard + " with " + playerCard + " and discarded both");
                playerHand.remove(i);
                matched = true;
                break;
            }
        }

        if (!matched) {
            playerHand.add(takenCard);
        }
    }

    private void checkAndRemovePlayerIfHandEmpty(Player currentPlayer) throws InterruptedException {
        if (currentPlayer.getCardsInHand().isEmpty()) {
            Player player = playerQueue.removePlayerByID(currentPlayer.getPlayerID());
            System.out.println("Player " + player.getPlayerID() + " has discarded all their cards and is removed from the game!");
            playerQueue.getNextPlayer();
        }
    }

    public void CheckPlayers() throws InterruptedException {
        synchronized (playerQueueLock) {
            if (isGameOver()) {
                return;
            }
            for (int i = 0; i < playerQueue.size(); i++) {
                Player player = playerQueue.removePlayer();
                if (!player.getCardsInHand().isEmpty()) {
                    playerQueue.addPlayer(player); // Only add to the queue when a player can play again
                }
            }
        }
    }

    public boolean isGameOver() {
        synchronized (gameStatusLock) {
            if (gameFinished) {
                return true;
            }
            if (playerQueue.size() == 1) {
                Player lastPlayer = playerQueue.getCurrentPlayer();
                if (lastPlayer.getCardsInHand().size() == 1 && lastPlayer.getCardsInHand().get(0).isJoker()) {
                    System.out.println("Player " + lastPlayer.getPlayerID() + " has lost! They got left with a Joker!");
                    endGame();
                    return true;
                }
            }
            return false;
        }
    }

    private void endGame() {
        synchronized (gameStatusLock) {
            if (gameFinished) {
                throw new GameStateException("Game is already finished.");
            }
            gameFinished = true;
            playerQueue.notifyAllPlayers();
        }
    }

}