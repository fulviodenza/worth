package worth.server;

import com.google.gson.*;

import worth.MemberStatus;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Member {

    private String username;
    private String password;
    private MemberStatus status;
    private ArrayList<String> projectList;

    public Member(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = MemberStatus.OFFLINE;
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
            return obj;
        }
    }

    public static class MemberJsonDeserializer implements JsonDeserializer<Member> {

        public Member deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            return new Member(
                    obj.getAsJsonPrimitive("username").getAsString(),
                    obj.getAsJsonPrimitive("password").getAsString()
            );
        }
    }
}
