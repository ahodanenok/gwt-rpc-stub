package ahodanenok.gwt.stub.core;

public final class ServiceStub {

    private String id;
    private String profileId;
    private Class<?> serviceClass;
    private String url;

    private String displayString;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public Class<?> getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public void setServiceClassName(String className) {
        if (serviceClass != null) {
            throw new IllegalStateException("Can't set name when class already set");
        }

        this.displayString = className;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isBroken() {
        return serviceClass == null;
    }

    public String getDisplayString() {
        if (serviceClass != null) {
            return serviceClass.getName();
        }

        return displayString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceStub that = (ServiceStub) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
