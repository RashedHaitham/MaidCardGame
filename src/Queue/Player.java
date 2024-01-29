package Queue;

import Cards.Card;
import Exceptions.PlayerNotFoundException;
import Game.*;
import utility.Display;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Player implements Runnable {
    private final int playerID;
    private final ArrayList<Card> hand;
    private final Object lock;
    private final GameController gameController;
    PlayerQueue playerQueue;


    public Player(int playerID, Object lock) {
        this.hand = new ArrayList<>();
        this.playerID = playerID;
        playerQueue = PlayerQueue.getInstance();
        this.lock = lock;
        gameController = GameController.getInstance();
    }

    public int getPlayerID() {
        return playerID;
    }

    public synchronized Card takeRandomCard() throws InterruptedException {
        if (hand.isEmpty()) {
            throw new IllegalStateException("Hand is empty.");
        }
        Random random = new Random();
        return hand.remove(random.nextInt(hand.size()));
    }

    public synchronized void addCardsToHand(ArrayList<Card> cards) {
        hand.addAll(cards);
    }

    public synchronized ArrayList<Card> getCardsInHand() {
        return hand;
    }

    @Override
    public void run() {
        synchronized (lock) {
            while (playerQueue.size() > 1) {
                while (!playerQueue.isCurrentPlayer(this)&&!gameController.isGameOver()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt(); // Preserve interrupt status
                        return;
                    }
                }
                if (!hand.isEmpty()) {
                    try {
                        gameController.playerTurn(this);
                    } catch (InterruptedException e) {
                        throw new PlayerNotFoundException("no such player");
                    }
                    lock.notifyAll();
                }
                if (gameController.isGameOver()) {
                    lock.notifyAll();
                    return;
                }
            }
        }
    }

    public void discardMatchingPairs() {
        Set<Card> matchedCards = findMatchingPairs();
        hand.removeAll(matchedCards);
    }

    private Set<Card> findMatchingPairs() {
        Set<Card> matchedCards = new HashSet<>();
        for (int i = 0; i < hand.size(); i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                if (hand.get(i).matches(hand.get(j))) {
                    matchedCards.add(hand.get(i));
                    matchedCards.add(hand.get(j));
                    System.out.println("Player " + playerID + " matched and discarded a pair");
                    break;
                }
            }
        }
        return matchedCards;
    }



}