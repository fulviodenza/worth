package server;

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

}