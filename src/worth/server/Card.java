package worth.server;

import java.util.ArrayList;
import java.util.Objects;

import static worth.server.CardStatus.*;

public class Card {

    private String name;
    private String description;
    private CardStatus status;
    private ArrayList<String> cardHistory;

    /*
     * Metodo Costruttore per creare una card costituita da un nome,
     * una descrizione e uno stato, che impostiamo a TODO di default
     * ma che possiamo modificare ogni qualvolta ce ne sia la necessità
     */
    public Card (String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TODO;
        this.cardHistory = new ArrayList<>();
        cardHistory.add("todo");
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return this.name;
    }

    public CardStatus getStatus() {
        return this.status;
    }

    public void addToCardHistory(String status) {
        cardHistory.add(status);
    }

    public void setStatus(CardStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return Objects.equals(name, card.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
