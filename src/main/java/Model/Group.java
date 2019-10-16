package Model;

public class Group {
    private String id;
    private String name;
    private String groupLink;

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
        this.groupLink = null;
    }

    public Group(String id, String name, String groupLink) {
        this.id = id;
        this.name = name;
        this.groupLink = groupLink;
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

    public String getGroupLink() {
        return groupLink;
    }

    public void setGroupLink(String groupLink) {
        this.groupLink = groupLink;
    }
}
