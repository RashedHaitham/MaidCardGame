package Queue;

import Exceptions.InvalidInputException;
import Exceptions.PlayerNotFoundException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;


public class PlayerQueue {
    private static PlayerQueue instance;
    private final Queue<Player> players;
    private Player currentPlayer;
    private final Object lock;

    private PlayerQueue(){
        players = new LinkedList<>();
        lock = new Object();
    }

    public static PlayerQueue getInstance() {
        if (instance == null) {
            synchronized (PlayerQueue.class) {
                if (instance == null) {
                    instance = new PlayerQueue();
                }
            }
        }
        return instance;
    }

    public void initializeQueue() {
        System.out.println("Welcome to the Maid Card Game!");
        Scanner input = new Scanner(System.in);
        int numberOfPlayers = 0;
        while (numberOfPlayers < 2 || numberOfPlayers > 8) {
            System.out.println("Enter the number of players (2-8): ");
            try {
                numberOfPlayers = input.nextInt();
                if (numberOfPlayers < 2 || numberOfPlayers > 8) {
                    //System.out.println("Invalid number of players. Please enter a number between 2 and 8.");
                    throw new InvalidInputException("Invalid number of players. Please enter a number between 2 and 8.");
                }
            } catch (InvalidInputException e) {
                System.out.println("Invalid input. Please enter a number.");
                input.next(); // Clear the invalid input
            }
        }
        for (int i = numberOfPlayers; i > 0; i--) {
            players.add(new Player(i , lock));
        }
        input.close();
    }


    public boolean isCurrentPlayer(Player player) {
        return player.equals(currentPlayer);
    }


    public void notifyAllPlayers() {
        synchronized (lock) {
            lock.notifyAll();
        }
    }

    public Queue<Player> getQueue(){
        return  players;
    }

    public synchronized Player removeCurrentPlayer() {
        return players.poll();
    }

    public synchronized void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
    }

    public synchronized Player getPlayerByID(int playerID) {
        for (Player player : players) {
            if (player.getPlayerID() == playerID) {
                return player;
            }
        }
        return null;
    }

    public synchronized Player removePlayerByID(int playerID) {
        for (Player player : players) {
            if (player.getPlayerID() == playerID) {
                players.remove(player);
                return player;
            }
        }
        return null;
    }

    public synchronized Player getCurrentPlayer() {
        return this.currentPlayer;
    }

    public synchronized Player getNextPlayer() {
        if (players.isEmpty()) {
           throw new PlayerNotFoundException("the queue is empty");
        }
        Player currentPlayer = players.remove();
        players.add(currentPlayer);
        Player nextPlayer = players.peek();
        setCurrentPlayer(nextPlayer);
        return nextPlayer;
    }

    public int size() {
        return  players.size();
    }
}
