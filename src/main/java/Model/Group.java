package Model;

public class Group {
    String group_id;
    String name;

    public Group(String group_id, String name) {
        this.group_id = group_id;
        this.name = name;
    }

    public String getId() {
        return group_id;
    }

    public void setId(String group_id) {
        this.group_id = group_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
