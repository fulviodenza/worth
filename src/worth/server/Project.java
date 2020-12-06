package worth.server;

import java.util.ArrayList;

public class Project {

    public String projectName;
    public ArrayList<String> memberList;
    public ArrayList<Card> taskList;
    public ArrayList<Card> TODO_List;
    public ArrayList<Card> IN_PROGRESS_List;
    public ArrayList<Card> TO_BE_REVISED_List;
    public ArrayList<Card> DONE_List;

    public Project(String projectName) {

        this.projectName = projectName;
        memberList = new ArrayList<>();
        taskList = new ArrayList<>();
    }

    /**
     *
     * @param cardName
     * @param cardDescription
     * @return 1 if the operation exit with some error
     *          0 if the operation exit with no error
     */
    public int createCard(String cardName, String cardDescription) {

        for(Card c : taskList) {
            if(cardName.equals(c.getName())) {
                System.out.println("The task is already in the Card list");
                return 1;
            }
        }
        Card newCard = new Card(cardName, cardDescription);
        taskList.add(newCard);
        TODO_List.add(newCard);
        newCard.changeStatus(CardStatus.TODO);
        return 0;
    }

    public int moveCard(Card card, CardStatus status) {
        if(taskList.contains(card)) {
            card.changeStatus(status);
            return 0;
        }
        return 1;
    }
}
