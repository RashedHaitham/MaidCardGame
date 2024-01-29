package utility;

import Cards.Card;
import Cards.Color;
import Queue.Player;
import Queue.PlayerQueue;

import java.util.List;
import java.util.Objects;

import static utility.Utility.*;

public class Display {

    public synchronized static void printHands() {
        PlayerQueue playerQueue = PlayerQueue.getInstance();
        System.out.println("***************************** Current Cards *****************************\n");
        for (Player player : playerQueue.getQueue()) {
            if (!player.getCardsInHand().isEmpty()) {
                System.out.println("Player " + player.getPlayerID() + "'s Cards:");
                printPlayerCards(player);
            }
        }
    }

    private synchronized static void printPlayerCards(Player player) {
        List<Card> playerHands = player.getCardsInHand();
        String playerCards = spaceParts(playerHands) + namePart(playerHands) + spaceParts(playerHands);
        System.out.println(playerCards);
        System.out.println("\n");
    }

    private static String spaceParts(List<Card> cardList){
        StringBuilder c = new StringBuilder();
        for(int i=0;i<1;i++){ // height of card
            for (Card card : cardList) {
                String suit = card.getSuit();
                String color = "";
                if (Objects.equals(suit, "♠") || Objects.equals(suit, "♣")){
                    color=getColor(Color.BLACK);
                }
                else if(Objects.equals(suit, "♦") || Objects.equals(suit, "♥")){
                    color=getColor(Color.RED);
                }
                else color=getColor(Color.JOKER);
                String fill = color + STOP + space(spaceBetweenCards) ;
                //String fill = color + STOP + space(spaceBetweenCards) ;
                c.append(fill);
            }
            c.append("\n");
        }
        return c.toString();
    }

    private static String namePart(List<Card> cardList) {
        StringBuilder c = new StringBuilder();
        for (Card card : cardList) {
            String suit = card.getSuit();
            String value=card.getValue();
            String color = "";
            if (Objects.equals(suit, "♠") || Objects.equals(suit, "♣")){
                color=getColor(Color.BLACK);
            }
            else if(Objects.equals(suit, "♦") || Objects.equals(suit, "♥")){
                color=getColor(Color.RED);
            }
            else color=getColor(Color.JOKER);
            int spaceCount = (cardWidth - (value.length()+suit.length())) / 2;
            String fill = color + space(spaceCount) + BLACK_FONT + suit +value+space(cardWidth - (value.length()+suit.length() + spaceCount))+ STOP + space(spaceBetweenCards);
            c.append(fill);
        }
        //c.append("\n");
        return c.toString();
    }

}