package Model;

import java.util.ArrayList;

public class Group {
    String id;
    String name;
    ArrayList<String> membersList;


    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Group(String id, String name, ArrayList<String> workerIdArrayList) {
        this.id = id;
        this.name = name;
        this.membersList = workerIdArrayList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
