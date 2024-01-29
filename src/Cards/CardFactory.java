package Cards;

public class CardFactory {

    public static Card createCard(String suit, String value) {
        if ("Joker".equals(value)) {
            return createJoker();
        }
        return createStandardCard(suit, value);
    }

    private static Card createStandardCard(String suit, String value) {
        Color color = switch (suit) {
            case "♠", "♣" -> Color.BLACK;
            case "♦", "♥" -> Color.RED;
            default -> throw new IllegalArgumentException("Invalid suit");
        };
        return new Card(suit, value, color);
    }

    private static Card createJoker() {
        return new Card("\uD83C\uDCCF", "Joker", Color.JOKER);
    }
}
