package ahodanenok.gwt.stub.core;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.SerializationPolicy;

import java.util.HashSet;
import java.util.Set;

public class AllowAllSerializationPolicy extends SerializationPolicy {

    private static Set<String> BLACKLIST = new HashSet<>();
    static {
        BLACKLIST.add("java.lang.Object");
    }

    public boolean shouldDeserializeFields(Class<?> clazz) {
        return isSerializationAllowed(clazz);
    }

    public boolean shouldSerializeFields(Class<?> clazz) {
        return isSerializationAllowed(clazz);
    }

    private boolean isSerializationAllowed(Class<?> clazz) {
        return clazz != null && !BLACKLIST.contains(clazz.getName());
    }

    public void validateDeserialize(Class<?> clazz) throws SerializationException {
        // no-op
    }

    public void validateSerialize(Class<?> clazz) throws SerializationException {
        // no-op
    }
}
