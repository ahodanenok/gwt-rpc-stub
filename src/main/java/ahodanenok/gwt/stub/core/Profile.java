package ahodanenok.gwt.stub.core;

public class Profile {

    private String id;
    private String displayName;

    public Profile() { }

    public Profile(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        return id.equals(profile.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
