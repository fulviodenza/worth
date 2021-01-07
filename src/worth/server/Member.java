package worth.server;

import com.google.gson.*;

import worth.MemberStatus;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Member {

    private String username;
    private String password;
    private MemberStatus status;
    public ArrayList<String> projectList;

    public Member(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = MemberStatus.OFFLINE;
        projectList = new ArrayList<>();
    }

    public Member(String username, String password, ArrayList<String> projectList) {
        this.username = username;
        this.password = password;
        this.status = MemberStatus.OFFLINE;
        this.projectList = projectList;
    }

    public int addToProject(String projectName) {
        if(projectList.contains(projectName)) {
            System.out.println("The user is already in the selected project");
            return 1;
        }
        projectList.add(projectName);

        return 0;
    }

    public boolean isInProject(String projectName) {
        if(projectList.contains(projectName)) {
            return true;
        } else {
            return false;
        }
    }

    public ArrayList<String> getProjectList() {
        return projectList;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setMemberStatus(MemberStatus newStatus) {
        status = newStatus;
    }

    public MemberStatus getMemberStatus() {
        return status;
    }

    public static class MemberJsonSerializer implements JsonSerializer<Member> {

        public JsonElement serialize(Member user, Type src, JsonSerializationContext context) {
            JsonObject obj = new JsonObject();
            obj.add("username", new JsonPrimitive(user.getUsername()));
            obj.add("password", new JsonPrimitive(user.getPassword()));
            JsonArray projects = new JsonArray(user.getProjectList().size());
            user.getProjectList().forEach(projects::add);
            obj.add("projectList", projects);
            return obj;
        }
    }

    public static class MemberJsonDeserializer implements JsonDeserializer<Member> {

        public Member deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            JsonArray json_projectList = obj.getAsJsonArray("projectList");
            ArrayList<String> projectList = new ArrayList<>();

            for(JsonElement j : json_projectList) {
                projectList.add(j.getAsString());
            }
            return new Member(
                    obj.getAsJsonPrimitive("username").getAsString(),
                    obj.getAsJsonPrimitive("password").getAsString(),
                    projectList
            );
        }
    }
}
