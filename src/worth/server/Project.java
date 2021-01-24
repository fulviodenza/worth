package worth.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
    String ipAddress;

    public Project(String projectName) {

        this.projectName = projectName;
        memberList = new ArrayList<>();
        taskList = new ArrayList<>();
        ipAddress = IPGenerator.generateIPAddress();

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
    public int createDirectory(String projectName) {
        try {
            if(Files.exists(path)){
                System.out.println("Project exists in the project folder");
                return 1;
            } else {
                Writer writer;
                Files.createDirectories(path);
                String pathIP = path+"/ip_address.json";
                Files.createFile(Path.of(pathIP));
                writer = new FileWriter(pathIP);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(ipAddress, writer);
                writer.flush();
                writer.close();
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getIpAddress() {
        Gson gson = new Gson();
        BufferedReader br;

        try {
            File f = new File(path+"/ip_address.json");
            if(!f.exists()) {
                f.createNewFile();
            }
            br = new BufferedReader(new FileReader(path+"/ip_address.json"));
            Type type = new TypeToken<String>() {
            }.getType();
            ipAddress = gson.fromJson(br, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ipAddress;
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

    public boolean isInCardsList(String cardName) {

        Card card = new Card(cardName, null);
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

        if(taskList == null) {
            return false;
        } else {
            return taskList.contains(card);
        }
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

    public void readCardList() {
        Gson gson = new Gson();
        BufferedReader br;

        try {
            File f = new File(path+"/cards.json");
            if(!f.exists()) {
                f.createNewFile();
            }
            br = new BufferedReader(new FileReader(path+"/cards.json"));
            Type type = new TypeToken<ArrayList<Card>>() {
            }.getType();
            taskList = gson.fromJson(br, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param cardName the name of the card
     * @param cardDescription the description of the card
     */
    public void createCard(String cardName, String cardDescription) {

        if(taskList == null) {
            taskList = new ArrayList<>();
        }
        for(Card c : taskList) {
            if(cardName.equals(c.getName())) {
                System.out.println("The task is already in the Card list");
                return;
            }
        }
//        readCardList();
        for(Card c : taskList) {
            System.out.println(c.getName());
        }
        //By default a card is added to TODO_LIST
        Card newCard = new Card(cardName, cardDescription);
        taskList.add(newCard);
        TODO_List.add(newCard);
        updateCardList();
        writeTodoList();
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

    public boolean areAllCardsDone() {
        readCardList();
        boolean allDone = true;

        for(Card c : taskList) {
            if(c.getStatus() != CardStatus.DONE) {
                allDone = false;
                break;
            }
        }
        return allDone;
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

    public synchronized void moveCard(String cardName, String oldStatus, String newStatus) {
        readCardList();
        Card card = null;
        boolean found = false;
        if(oldStatus.equals("todo") && newStatus.equals("in_progress")) {
            readTodoList();
            for(Card c : TODO_List) {
                if(c.getName().equals(cardName)) {
                    card = c;
                    found = true;
                    break;
                }
            }
            if(found) {

                taskList.get(taskList.indexOf(card)).addToCardHistory("in_progress");
                taskList.get(taskList.indexOf(card)).setStatus(CardStatus.IN_PROGRESS);
                TODO_List.remove(card);
                readInProgressList();
                IN_PROGRESS_List.add(card);
                card.addToCardHistory("in_progress");
                card.setStatus(CardStatus.IN_PROGRESS);
                writeTodoList();
                writeInProgressList();
                updateCardList();
                System.out.println("MOVED");
            } else {
                System.out.println("Card not found in TODO list");
            }
        } else if(oldStatus.equals("in_progress") && newStatus.equals("to_be_revised")) {
            readCardList();
            readInProgressList();
            for(Card c : IN_PROGRESS_List) {
                if(c.getName().equals(cardName)) {
                    card = c;
                    found = true;
                    break;
                }
            }
            if(found) {
                taskList.get(taskList.indexOf(card)).addToCardHistory("to_be_revised");
                taskList.get(taskList.indexOf(card)).setStatus(CardStatus.TO_BE_REVISED);
                IN_PROGRESS_List.remove(card);
                readToBeRevisedList();
                TO_BE_REVISED_List.add(card);
                card.addToCardHistory("to_be_revised");
                card.setStatus(CardStatus.TO_BE_REVISED);
                writeInProgressList();
                writeToBeRevisedList();
                updateCardList();
                System.out.println("MOVED");
            } else {
                System.out.println("Card not found in IN_PROGRESS list");
            }
        } else if(oldStatus.equals("to_be_revised") && newStatus.equals("in_progress")) {
            readCardList();
            readToBeRevisedList();
            for(Card c : TO_BE_REVISED_List) {
                if(c.getName().equals(cardName)) {
                    card = c;
                    found = true;
                    break;
                }
            }
            if(found) {
                taskList.get(taskList.indexOf(card)).addToCardHistory("in_progress");
                taskList.get(taskList.indexOf(card)).setStatus(CardStatus.IN_PROGRESS);
                TO_BE_REVISED_List.remove(card);
                readInProgressList();
                IN_PROGRESS_List.add(card);
                card.addToCardHistory("in_progress");
                card.setStatus(CardStatus.IN_PROGRESS);
                writeToBeRevisedList();
                writeInProgressList();
                updateCardList();
                System.out.println("MOVED");
            } else {
                System.out.println("Card not found in IN_PROGRESS list");
            }
        } else if(oldStatus.equals("in_progress") && newStatus.equals("done")) {
            readCardList();
            readInProgressList();
            for(Card c : IN_PROGRESS_List) {
                if(c.getName().equals(cardName)) {
                    card = c;
                    found = true;
                    break;
                }
            }
            if(found) {
                taskList.get(taskList.indexOf(card)).addToCardHistory("done");
                taskList.get(taskList.indexOf(card)).setStatus(CardStatus.DONE);
                IN_PROGRESS_List.remove(card);
                readDoneList();
                DONE_List.add(card);
                card.addToCardHistory("done");
                card.setStatus(CardStatus.DONE);
                writeInProgressList();
                writeDoneList();
                updateCardList();
                System.out.println("MOVED");
            } else {
                System.out.println("Card not found in IN_PROGRESS list");
            }
        } else if(oldStatus.equals("to_be_revised") && newStatus.equals("done")) {
            readCardList();
            readToBeRevisedList();
            for(Card c : TO_BE_REVISED_List) {
                if(c.getName().equals(cardName)) {
                    card = c;
                    found = true;
                    break;
                }
            }
            if(found) {
                taskList.get(taskList.indexOf(card)).addToCardHistory("done");
                taskList.get(taskList.indexOf(card)).setStatus(CardStatus.DONE);
                TO_BE_REVISED_List.remove(card);
                readDoneList();
                DONE_List.add(card);
                card.addToCardHistory("done");
                card.setStatus(CardStatus.DONE);
                writeToBeRevisedList();
                writeDoneList();
                updateCardList();
                System.out.println("MOVED");
            } else {
                System.out.println("Card not found in IN_PROGRESS list");
            }
        }
    }

    public synchronized String cardHistory(String card) {
        readCardList();
        boolean found = false;
        int index = 0;

        StringBuilder output = new StringBuilder();

        for(Card c : taskList) {
            if (c.getName().equals(card)) {
                found = true;
                index = taskList.indexOf(c);
                break;
            }
        }

        if(found) {
            for(String s : taskList.get(index).getCardHistory()){
                output.append(s).append("->:");
            }
        } else {
            output.append("No card found");
        }

        return output.toString();

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

    public synchronized void readInProgressList() {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path+"/in_progress_list.json"));
            Type type = new TypeToken<ArrayList<Card>>() {
            }.getType();
            IN_PROGRESS_List = gson.fromJson(br, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeInProgressList() {
        Writer writer;
        try {
            writer = new FileWriter(path+"/in_progress_list.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(IN_PROGRESS_List, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void readToBeRevisedList() {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path+"/to_be_revised_list.json"));
            Type type = new TypeToken<ArrayList<Card>>() {
            }.getType();
            TO_BE_REVISED_List = gson.fromJson(br, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeToBeRevisedList() {
        Writer writer;
        try {
            writer = new FileWriter(path+"/to_be_revised_list.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(TO_BE_REVISED_List, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void readDoneList() {
        Gson gson = new Gson();
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(path+"/done.json"));
            Type type = new TypeToken<ArrayList<Card>>() {
            }.getType();
            DONE_List = gson.fromJson(br, type);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeDoneList() {
        Writer writer;
        try {
            writer = new FileWriter(path+"/done.json");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(DONE_List, writer);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }
}
