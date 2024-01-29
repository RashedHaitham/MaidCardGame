package Game;

import Cards.Deck;
import Queue.Player;
import Queue.PlayerQueue;
public abstract class Game {
    protected PlayerQueue playerQueue;
    protected Deck deck;


    protected void initializeGame(){
        playerQueue = PlayerQueue.getInstance();
        playerQueue.initializeQueue();
        dealCardsToPlayers();
        StartWith();
    }

    protected void StartWith() {
        Player startingPlayer = playerQueue.getPlayerByID(playerQueue.size());
        playerQueue.setCurrentPlayer(startingPlayer);
        //playerQueue.setCurrentPlayerID(playerQueue.getCurrentPlayer().getPlayerID());
    }

    public synchronized static void dealCardsToPlayers() {
        PlayerQueue playerQueue = PlayerQueue.getInstance();
        Deck deck = Deck.getInstance();

        System.out.println("Dealing cards to players...");
        int numberOfPlayers = playerQueue.size();
        int cardsPerPlayer = deck.getCards().size() / numberOfPlayers;
        int extraCards = deck.getCards().size() % numberOfPlayers;

        int playerIndex = 0;
        for (Player player : playerQueue.getQueue()) {
            //dealing extra cards to the first k players
            int numPlayerCards = cardsPerPlayer + (extraCards > 0 ? 1 : 0);
            player.addCardsToHand(deck.dealCard(numPlayerCards));
            player.discardMatchingPairs();

            extraCards = Math.max(0, extraCards - 1);

            playerIndex++;
        }
    }


    public abstract void play();
}