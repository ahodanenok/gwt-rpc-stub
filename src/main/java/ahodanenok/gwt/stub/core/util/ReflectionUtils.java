package ahodanenok.gwt.stub.core.util;

import ahodanenok.gwt.stub.core.Config;
import ahodanenok.gwt.stub.core.StubsClassLoader;

import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ReflectionUtils {

    private static final Logger LOGGER = Logger.getLogger(ReflectionUtils.class.getName());

    private ReflectionUtils() { }

    public static StubsClassLoader createStubsClassLoader(Config config) {
        List<String> classPath = config.getClassPath();
        URL[] urls = new URL[classPath.size()];
        for (int i = 0; i < classPath.size(); i++) {
            try {
                urls[i] = new File(classPath.get(i)).toURI().toURL();
            } catch (MalformedURLException e) {
                LOGGER.log(Level.WARNING, "URL skipped: " + classPath.get(i), e);
            }
        }

        return new StubsClassLoader(urls, Thread.currentThread().getContextClassLoader());
    }

    public static List<Class<?>> toClasses(List<String> classNames, ClassLoader classLoader) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (String name : classNames) {
            if ("double".equals(name)) {
                classes.add(double.class);
            } else if ("float".equals(name)) {
                classes.add(float.class);
            } else if ("byte".equals(name)) {
                classes.add(byte.class);
            } else if ("short".equals(name)) {
                classes.add(short.class);
            } else if ("int".equals(name)) {
                classes.add(int.class);
            } else if ("long".equals(name)) {
                classes.add(long.class);
            } else if ("boolean".equals(name)) {
                classes.add(boolean.class);
            } else {
                classes.add(Class.forName(name, true, classLoader));
            }
        }

        return classes;
    }

    public static List<String> toNames(Class<?>[] parameterTypes) {
        List<String> names = new ArrayList<>();
        for (Class<?> parameterType : parameterTypes) {
            names.add(parameterType.getName());
        }

        return names;
    }

    public static boolean paramsMatch(Method method, List<Class<?>> matchClasses) {
        Class<?>[] paramClasses = method.getParameterTypes();
        if (paramClasses.length != matchClasses.size()) {
            return false;
        }

        for (int i = 0; i < paramClasses.length; i++) {
            if (!paramClasses[i].equals(matchClasses.get(i))) {
                return false;
            }
        }

        return true;
    }

    public static Method getMethod(Class<?> fromClass, String methodName, List<String> params) throws NoSuchMethodException {
        try {
            Class<?>[] paramClasses = toClasses(params, fromClass.getClassLoader()).toArray(new Class<?>[0]);
            return fromClass.getMethod(methodName, paramClasses);
        } catch (ClassNotFoundException e) {
            throw new NoSuchMethodException();
        }
    }

    public static List<Method> getMethods(Class<?> fromClass) {
        Method[] methodsArray = fromClass.getMethods();
        List<Method> methods = new ArrayList<>(methodsArray.length);
        Collections.addAll(methods, methodsArray);

        return methods;
    }

    public static String toString(Method m) {
        StringBuilder sb = new StringBuilder();

        if (m.getModifiers() != 0) {
            sb.append(Modifier.toString(m.getModifiers())).append(' ');
        }

        sb.append(m.getReturnType().getSimpleName()).append(' ');
        sb.append(m.getName());

        sb.append('(');
        separateWithCommas(m.getParameterTypes(), sb);
        sb.append(')');

        if (m.getExceptionTypes().length > 0) {
            sb.append(" throws ");
            separateWithCommas(m.getExceptionTypes(), sb);
        }

        return sb.toString();
    }

    private static void separateWithCommas(Class<?>[] types, StringBuilder sb) {
        for (int j = 0; j < types.length; j++) {
            sb.append(types[j].getSimpleName());
            if (j < (types.length - 1))
                sb.append(",");
        }
    }
}
