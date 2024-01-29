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
    int counter;
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

    public void playerTurn(Player currentPlayer) {
        synchronized (playerQueueLock) {
            while (!currentPlayer.equals(playerQueue.getCurrentPlayer())) {
                try {
                    playerQueueLock.wait(); // Players wait if it's not their turn
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }

            List<Card> playerHand = currentPlayer.getCardsInHand();
            Display.printHands();

            // Check for game over and exit the turn if necessary
            if (isGameOver()) {
                gameFinished = true;
                return;
            }

            playRound();

            // Handle player actions during their turn
            if (!playerHand.isEmpty()) {
                Card takenCard = null;
                Player nextPlayer;

                try {
                    nextPlayer = playerQueue.getNextPlayer();
                    if (nextPlayer == currentPlayer) {
                        return;
                    }

                    takenCard = nextPlayer.takeRandomCard();
                    System.out.println("Player " + currentPlayer.getPlayerID() + " took " + takenCard + " from Player " + nextPlayer.getPlayerID());
                    if (nextPlayer.getCardsInHand().isEmpty()) {
                        playerQueue.removeCurrentPlayer();
                        System.out.println("Player " + nextPlayer.getPlayerID() + " has discarded all their cards and is removed from the game!");
                        playerQueue.getNextPlayer();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (takenCard != null) {
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
                if (currentPlayer.getCardsInHand().isEmpty()) {
                    Player player = playerQueue.removePlayerByID(currentPlayer.getPlayerID());
                    System.out.println("Player " + player.getPlayerID() + " has discarded all their cards and is removed from the game!");
                    playerQueue.getNextPlayer();
                }
            }
            playerQueueLock.notifyAll();
        }
    }

    public void playRound() {
        synchronized (playerQueueLock) {
            if (isGameOver()) {
                return;
            }
            System.out.println("***************************** ROUND " + ++counter + " *****************************");
            int playerSize = playerQueue.size();
            for (int i = 0; i < playerSize; i++) {
                Player player = playerQueue.removeCurrentPlayer();
                if (!player.getCardsInHand().isEmpty()) {
                    playerQueue.getQueue().add(player); // Only add to the queue when a player can play again
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