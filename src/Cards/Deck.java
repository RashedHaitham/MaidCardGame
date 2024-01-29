package Cards;

import java.util.ArrayList;
import java.util.Collections;

public class Deck {
    private final ArrayList<Card> cards;
    private static Deck instance;

    public static Deck getInstance() {
        if (instance == null)
            instance = new Deck();
        return instance;
    }

    private Deck() {
        cards = new ArrayList<>();
        createCards();
    }

    private void createCards(){
        String[] suits = {"♠", "♣", "♦", "♥"};
        String[] values = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        for (String suit : suits) {
            for (String value : values) {
                Card card = CardFactory.createCard(suit, value);
                cards.add(card);
            }
        }
        cards.add(CardFactory.createCard("\uD83C\uDCCF", "Joker"));
        Collections.shuffle(cards);
    }

    public ArrayList<Card> getCards(){
        return cards;
    }

    public synchronized ArrayList<Card> dealCard(int cardsPerPlayer) {
        ArrayList<Card> hand = new ArrayList<>(cards.subList(0, cardsPerPlayer));
        cards.subList(0, cardsPerPlayer).clear();
        return hand;
    }
}