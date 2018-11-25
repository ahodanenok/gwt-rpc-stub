package ahodanenok.gwt.stub.core;

import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyProvider;

public class AllowAllSerializationPolicyProvider implements SerializationPolicyProvider {

    private AllowAllSerializationPolicy policy = new AllowAllSerializationPolicy();

    public SerializationPolicy getSerializationPolicy(String moduleBaseURL, String serializationPolicyStrongName) {
        return policy;
    }
}

