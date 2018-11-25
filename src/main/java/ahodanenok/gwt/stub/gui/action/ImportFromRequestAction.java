package ahodanenok.gwt.stub.gui.action;

import ahodanenok.gwt.stub.core.MethodStub;
import ahodanenok.gwt.stub.core.Stubs;
import ahodanenok.gwt.stub.gui.dialog.ImportFromRequestDialog;
import ahodanenok.gwt.stub.core.ServiceStub;
import ahodanenok.gwt.stub.gui.StubsExceptionHandler;
import ahodanenok.gwt.stub.core.AllowAllSerializationPolicyProvider;

import com.google.gwt.user.server.rpc.RPCRequest;

import javafx.event.ActionEvent;

public final class ImportFromRequestAction extends Action<MethodStub> {

    private Stubs stubs;

    public ImportFromRequestAction(Stubs stubs, StubsExceptionHandler exceptionHandler) {
        super(exceptionHandler);
        this.stubs = stubs;
    }

    @Override
    protected MethodStub doHandle(ActionEvent event) throws Exception {
        ImportFromRequestDialog.ImportData data =
                new ImportFromRequestDialog(stubs).getImportData();
        if (data != null) {
            RPCRequest request = stubs.decodeRequest(data.getRequest(), new AllowAllSerializationPolicyProvider());

            // automatically create service stub if it doesn't exist
            ServiceStub serviceStub = stubs.getServiceStub(request.getMethod().getDeclaringClass());
            if (serviceStub == null) {
                serviceStub = new ServiceStub();
                serviceStub.setServiceClass(request.getMethod().getDeclaringClass());
                serviceStub.setUrl(data.getServiceUrl());
                serviceStub.setProfileId(stubs.getActiveProfile().getId());
                stubs.saveServiceStub(serviceStub);
            }

            MethodStub stub = new MethodStub();
            stub.setServiceStubId(serviceStub.getId());
            stub.setMethod(request.getMethod());
            stub.setReturnValueType(data.getResponseType());
            stub.setReturnValue(data.getResponse());
            stubs.saveMethodStub(stub);
            return stub;
        } else {
            return null;
        }
    }
}
