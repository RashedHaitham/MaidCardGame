package Game;

import Cards.Deck;
import Queue.Player;
import Queue.PlayerQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MaidGame extends Game {
    private final Object lock = new Object();

    public MaidGame() {
        playerQueue = PlayerQueue.getInstance();
        deck = Deck.getInstance();
        initializeGame();
    }

    @Override
    public void play() {
        System.out.println("***************** START PLAYING *********************");

        ExecutorService exc= Executors.newFixedThreadPool(playerQueue.getQueue().size());

        for (Player player : playerQueue.getQueue())
            exc.execute(player);

        System.out.println("Number of active threads: " + Thread.activeCount());

        exc.shutdown();

        try {
            // Wait a certain time for all tasks to complete
            if (!exc.awaitTermination(3, TimeUnit.SECONDS)) {
                System.out.println("Some players have not finished yet, forcing shutdown...");
                System.out.println("Number of active threads in force: " + Thread.activeCount());

                exc.shutdownNow();
                System.out.println("Game finished.");

            }
        } catch (InterruptedException e) {
            exc.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }
}
