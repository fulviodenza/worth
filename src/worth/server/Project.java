package worth.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import worth.exceptions.MemberNotFoundException;

import javax.xml.crypto.Data;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Project {

    public String projectName;
    public ArrayList<String> memberList;
    public ArrayList<Card> taskList;
    public ArrayList<Card> TODO_List;
    public ArrayList<Card> IN_PROGRESS_List;
    public ArrayList<Card> TO_BE_REVISED_List;
    public ArrayList<Card> DONE_List;
    Path path;
    String pathString;

    public Project(String projectName) {

        this.projectName = projectName;
        memberList = new ArrayList<>();
        taskList = new ArrayList<>();

        //Init of various lists
        TODO_List = new ArrayList<>();
        IN_PROGRESS_List = new ArrayList<>();
        TO_BE_REVISED_List = new ArrayList<>();
        DONE_List = new ArrayList<>();

        //Creation of a file for each list
        File todo = new File(path+"todo_list.json");
        File in_progress = new File(path+"in_progress_list.json");
        File to_be_revised = new File(path+"to_be_revised.json");
        File done = new File(path+"done.json");
        File cards = new File(path+"cards.json");

        path = Paths.get("../projects/"+projectName+"/");
    }

    //CREAZIONE PROGETTO
    public void createDirectory(String projectName) {
        try {
            if(Files.exists(Path.of("../projects/" + projectName + "/"))){
                System.out.println("Project exists in the project folder");
            } else {
                Files.createDirectories(path);
                pathString = path.toString();
                System.out.println("Project created");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //METODI PER AGGIUNTA MEMBRO AL PROGETTO
    public void addMember(String username) {
        if(!memberList.contains(username)) {
            this.memberList.add(username);
            updateUserList();
        } else {
            System.out.println("username already present in the member list!");
        }
    }

    public boolean isInMemberList(String username) {

        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path+"/memberList.json"));
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            memberList = gson.fromJson(br, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return memberList.contains(username);
    }

    public void updateUserList() {
        Writer writer;
        try {
            writer = new FileWriter(path+"/memberList.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(memberList, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateTODOList() {
        Writer writer;
        try {
            writer = new FileWriter(path+"/todo_list.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(TODO_List, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readCardList() {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path+"/cards.json"));
            Type type = new TypeToken<ArrayList<Card>>() {
            }.getType();
            taskList = gson.fromJson(br, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param cardName the name of the card
     * @param cardDescription the description of the card
     */
    public void createCard(String cardName, String cardDescription) {

        for(Card c : taskList) {
            if(cardName.equals(c.getName())) {
                System.out.println("The task is already in the Card list");
            }
        }
        readCardList();
        //By default a card is added to TODO_LIST
        Card newCard = new Card(cardName, cardDescription);
        taskList.add(newCard);
        TODO_List.add(newCard);
        updateCardList();
        System.out.println("Card created");
    }

    public void updateCardList() {
        Writer writer;
        try {
            writer = new FileWriter(path+"/cards.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(taskList, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String showCards() {
        readCardList();
        StringBuilder output = new StringBuilder();
        for(Card c : taskList) {
            output.append(c.getName()).append("$");
        }
        return output.toString();
    }

    public String showCard(String cardInput) {
        readCardList();
        StringBuilder output = new StringBuilder();
        for(Card c : taskList) {
            if(c.getName().equals(cardInput)) {
                output.append("NAME:").append(c.getName()).append("$").append("STATUS:").append(c.getStatus()).append("$").append("DESCRIPTION:").append(c.getDescription());
            }
        }
        return output.toString();
    }

    public String showMembers() {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path+"/memberList.json"));
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            memberList = gson.fromJson(br, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StringBuilder output = new StringBuilder();
        for(String s : memberList) {
            output.append(s).append("$");
        }
        return output.toString();
    }

    public synchronized void addTodoList(Card c) {
        if(!TODO_List.contains(c)) {
            TODO_List.add(c);
        } else {
            System.out.println("Card already exists");
        }
    }

    public synchronized void readTodoList() {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path+"/todo_list.json"));
            Type type = new TypeToken<ArrayList<Card>>() {
            }.getType();
            TODO_List = gson.fromJson(br, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeTodoList() {
        Writer writer;
        try {
            writer = new FileWriter(path+"/todo_list.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(TODO_List, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public int moveCard(Card card, CardStatus status) {
//        if(taskList.contains(card)) {
//            card.changeStatus(status);
//            return 0;
//        }
//        return 1;
//    }
}
