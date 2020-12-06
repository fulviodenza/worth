package worth.server;

import java.util.ArrayList;

public class Member {

    private String username;
    private String password;
    private ArrayList<String> projectList;

    public Member(String username, String password) {
        this.username = username;
        this.password = password;
        projectList = new ArrayList<>();
    }

    public int addToProject(String projectName) {
        if(projectList.contains(projectName)) {
            System.out.println("The user is already in the selected project");
            return 1;
        }
        projectList.add(projectName);
        return 0;
    }
}
