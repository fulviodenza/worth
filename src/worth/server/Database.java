package worth.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Database {
    private static volatile Database database;
    private static Path dbFile;
    private static final ConcurrentHashMap<String, Member> db = new ConcurrentHashMap<>();

    public static Gson createGsonBuilder() {
        return new GsonBuilder()
                .registerTypeAdapter(Member.class, new Member.MemberJsonSerializer())
                .registerTypeAdapter(Member.class, new Member.MemberJsonDeserializer())
                .setPrettyPrinting()
                .create();
    }

    public Database() throws IOException {

        dbFile = Paths.get("../database.json");
        if(!Files.exists(dbFile)) {
            System.out.println("Server: Creating database file");
            Files.createFile(dbFile);
        }

        Gson gson = createGsonBuilder();

        try {
            Type type = new TypeToken<List<Member>>() {
            }.getType();
            Collection<Member> memberList = gson.fromJson(new String(Files.readAllBytes(dbFile)),
                    new TypeToken<Collection<Member>>(){}.getType());
            if (memberList != null) {
                memberList.forEach((Member member) -> db.put(member.getUsername(), member));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

//    public static int register(String username, String password) {
//        try {
//            Member m = new Member(username, password);
//            if (userDB == null) {
//                userDB = new ArrayList<>();
//            }
//            if (!userDB.contains(m)) {
//                userDB.add(m);
//            }
//
//            Writer writer = new FileWriter("database/db.json");
//            Gson gson = new GsonBuilder().setPrettyPrinting().create();
//            gson.toJson(userDB, writer);
//            writer.flush();
//            writer.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
    public static Database getDatabase() {
        try {
            if (database == null) {
                synchronized (Database.class) {
                    if (database == null) {
                        database = new Database();
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        return database;
    }

    public static synchronized void updateMember(Member user) {
        try {
            db.put(user.getUsername(), user);
            BufferedWriter writer = Files.newBufferedWriter(dbFile, StandardCharsets.UTF_8);
            writer.write(createGsonBuilder().toJson(db.values()));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}