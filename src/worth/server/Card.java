package worth.server;

import static worth.server.CardStatus.*;

public class Card {

    private String name;
    private String description;
    private CardStatus status;

    /*
     * Metodo Costruttore per creare una card costituita da un nome,
     * una descrizione e uno stato, che impostiamo a TODO di default
     * ma che possiamo modificare ogni qualvolta ce ne sia la necessità
     */
    public Card (String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TODO;
    }

    public int changeStatus (CardStatus status) {
        if(this.status == status) {
            System.out.println("The Card is already in this status");
            return 1;
        }
        this.status = status;
        System.out.println("The Card has been moved to the desired status");
        return 0;
    }

    public String getName() {
        return this.name;
    }

    public CardStatus getStatus() {
        return this.status;
    }
}
