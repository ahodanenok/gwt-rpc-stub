package ahodanenok.gwt.stub.core;

import java.net.URL;
import java.net.URLClassLoader;

public class StubsClassLoader extends URLClassLoader {

    public StubsClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
