package ahodanenok.gwt.stub.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Config {

    public static final Config DEFAULT;
    static {
        DEFAULT = new Config();
        DEFAULT.classPath = new ArrayList<>();
        DEFAULT.staticResourcesPath = "";
        DEFAULT.serverPort = 8080;
    }

    private int serverPort;
    private String staticResourcesPath;
    private List<String> classPath;

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getStaticResourcesPath() {
        return staticResourcesPath;
    }

    public void setStaticResourcesPath(String staticResourcesPath) {
        this.staticResourcesPath = staticResourcesPath;
    }

    public List<String> getClassPath() {
        return classPath;
    }

    public void setClassPath(List<String> classPath) {
        this.classPath = Collections.unmodifiableList(classPath);
    }
}
