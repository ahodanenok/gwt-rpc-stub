package ahodanenok.gwt.stub.core;

import ahodanenok.gwt.stub.core.util.JsonUtils;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.impl.AbstractSerializationStream;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.impl.ServerSerializationStreamWriter;

import java.util.List;
import java.util.logging.Logger;

public final class RPCStubRequestHandler {

    private static Logger LOGGER = Logger.getLogger(RPCStubRequestHandler.class.getName());

    private Stubs stubs;
    private AllowAllSerializationPolicy policy;
    private AllowAllSerializationPolicyProvider policyProvider;

    public RPCStubRequestHandler(Stubs stubs) {
        this.stubs = stubs;
        this.policy = new AllowAllSerializationPolicy();
        this.policyProvider = new AllowAllSerializationPolicyProvider();
    }

    public boolean canHandle(String path) throws StubsException {
        List<ServiceStub> serviceStubs = stubs.listServiceStubs();
        for (ServiceStub stub : serviceStubs) {
            LOGGER.fine("Matching path '" + path + "' against '" + stub.getUrl() + "'");
            if (stub.getUrl().equals(path)) {
                LOGGER.fine("Matched service stub: " + stub.getDisplayString());
                return true;
            }
        }

        return false;
    }

    public String handle(String request) throws StubsException {
        LOGGER.fine("GWT-RPC request: " + request);
        RPCRequest rpcRequest = stubs.decodeRequest(request, policyProvider);
        String response = getResponse(rpcRequest);
        LOGGER.fine("GWT-RPC response: " + response);
        return response;
    }

    private String getResponse(RPCRequest request) throws StubsException {
        MethodStub methodStub = stubs.getMethodStub(request.getMethod());
        if (methodStub == null) {
            return null;
        }

        LOGGER.fine("Stub WAS FOUND for method: " + request.getMethod().getName());

        if (methodStub.getReturnValueType() == ReturnValueType.GWT_RPC) {
            return methodStub.getReturnValue();
        } else if (methodStub.getReturnValueType() == ReturnValueType.JSON) {
            Class<?> returnType = request.getMethod().getReturnType();
            Object returnValue = JsonUtils.fromJson(methodStub.getReturnValue(), returnType);
            try {
                return encodeResponse(returnValue, true);
            } catch (SerializationException e) {
                throw new StubsException("Can't encode response", e);
            }
        } else {
            throw new IllegalStateException("Unknown type return data type: " + methodStub.getReturnValueType());
        }
    }

    private String encodeResponse(Object data, boolean statusOk) throws SerializationException {
        ServerSerializationStreamWriter stream = new ServerSerializationStreamWriter(policy);
        stream.setFlags(AbstractSerializationStream.DEFAULT_FLAGS);
        stream.prepareToWrite();
        if (data.getClass() != void.class) {
            stream.serializeValue(data, data.getClass());
        }

        if (statusOk) {
            return "//OK" + stream.toString();
        } else {
            return "//EX" + stream.toString();
        }
    }
}
