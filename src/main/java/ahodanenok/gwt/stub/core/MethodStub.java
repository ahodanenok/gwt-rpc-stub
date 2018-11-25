package ahodanenok.gwt.stub.core;

import ahodanenok.gwt.stub.core.util.ReflectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public final class MethodStub {

    private String id;
    private String serviceStubId;
    private Method method;
    private ReturnValueType returnValueType;
    private String returnValue;

    private String displayString;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceStubId() {
        return serviceStubId;
    }

    public void setServiceStubId(String serviceStubId) {
        this.serviceStubId = serviceStubId;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public String getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(String returnValue) {
        this.returnValue = returnValue;
    }

    public ReturnValueType getReturnValueType() {
        return returnValueType;
    }

    public void setReturnValueType(ReturnValueType returnValueType) {
        this.returnValueType = returnValueType;
    }

    public void setMethodInfo(String name, List<String> params) {
        if (method != null) {
            throw new IllegalStateException("Can't set info when method already set");
        }

        List<String> paramsSimple = new ArrayList<>(params.size());
        for (String param : params) {
            int lastDot = param.lastIndexOf('.');
            if (lastDot != -1) {
                paramsSimple.add(param.substring(lastDot + 1));
            } else {
                paramsSimple.add(param);
            }
        }

        this.displayString = name + "(" + StringUtils.join(paramsSimple, ",") + ")";
    }

    public boolean isBroken() {
        return method == null;
    }

    public String getDisplayString() {
        if (method != null) {
            return ReflectionUtils.toString(method);
        }

        return displayString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodStub stub = (MethodStub) o;

        return id.equals(stub.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
