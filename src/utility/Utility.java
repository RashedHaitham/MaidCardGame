package utility;

import Cards.Color;
public class Utility {
    public static String getColor(Color c){
        return switch (c) {
            case RED -> "\u001B[41m";
            case BLACK -> "\u001b[47m";
            case JOKER -> "\u001B[43m";
        };
    }
    public static final String STOP = "\u001B[0m";
    public static final String BLACK_FONT = "\u001B[30m";
    public static final String WHITE = "\u001B[47m";

    public static final int cardWidth = 11;
    public static final int spaceBetweenCards = 2;

    public static String space(int n) {
        StringBuilder s = new StringBuilder();
        while (n-- > 0) {
            s.append(" ");
        }
        return s.toString();
    }
}
