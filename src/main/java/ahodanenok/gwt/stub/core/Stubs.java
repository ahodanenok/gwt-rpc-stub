package ahodanenok.gwt.stub.core;

import ahodanenok.gwt.stub.core.storage.StubsFileStorage;
import ahodanenok.gwt.stub.core.storage.StubsStorageException;

import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.SerializationPolicyProvider;

import java.lang.reflect.Method;
import java.util.*;

public final class Stubs {

    public interface ServiceStubSaveListener {
        void onSave(ServiceStub stub);
    }

    public interface ServiceStubDeleteListener {
        void onDelete(ServiceStub stub);
    }

    public interface MethodStubSaveListener {
        void onSave(MethodStub stub);
    }

    public interface MethodStubDeleteListener {
        void onDelete(MethodStub stub);
    }

    public interface ProfileSaveListener {
        void onSave(Profile profile);
    }

    public interface ProfileChangeListener {
        void onChange(Profile newProfile, Profile oldProfile);
    }

    public interface ConfigSaveListener {
        void onSave(Config config);
    }

    private List<ServiceStubSaveListener> serviceStubSaveListeners;
    private List<ServiceStubDeleteListener> serviceStubDeleteListeners;
    private List<MethodStubSaveListener> methodStubSaveListeners;
    private List<MethodStubDeleteListener> methodStubDeleteListeners;
    private List<ProfileChangeListener> profileChangeListeners;
    private List<ProfileSaveListener> profileSaveListeners;
    private List<ConfigSaveListener> configSaveListeners;

    private StubsFileStorage storage;
    private Map<Profile, Cache> profileCache;
    private Profile activeProfile;

    public Stubs(StubsFileStorage storage) throws StubsException {
        this.storage = storage;
        this.profileCache = new HashMap<>();
    }

    public StubsClassLoader getStubsClassLoader() {
        return storage.getStubsClassLoader(activeProfile.getId());
    }

    public Class<?> loadServiceClass(String name) throws ClassNotFoundException {
        return Class.forName(name, true, storage.getStubsClassLoader(activeProfile.getId()));
    }

    public RPCRequest decodeRequest(String request, SerializationPolicyProvider policyProvider) {
        if (Thread.currentThread().getContextClassLoader() instanceof StubsClassLoader) {
            return RPC.decodeRequest(request, null, policyProvider);
        } else {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(getStubsClassLoader());
                return RPC.decodeRequest(request, null, policyProvider);
            } finally {
                Thread.currentThread().setContextClassLoader(cl);
            }
        }
    }

    // ~ Config ========================================================================================================
    public Config getConfig() {
        if (activeProfile == null) {
            return null;
        }

        return storage.getConfig(activeProfile.getId());
    }

    public void saveConfig(Config config) throws StubsException {
        try {
            storage.saveConfig(config, activeProfile.getId());
            fireConfigSaved(config);
        } catch (StubsStorageException e) {
            throw new StubsException("Can't load config", e);
        }
    }
    // =================================================================================================================


    // ~ Profile =======================================================================================================
    public void setActiveProfile(Profile profile) throws StubsException {
        if (profile != null && !storage.existsProfile(profile.getId())) {
            throw new StubsException("Unknown profile: " + profile);
        }

        if (activeProfile == profile || activeProfile != null && activeProfile.equals(profile)) {
            return;
        }

        Profile oldProfile = activeProfile;
        this.activeProfile = profile;
        fireProfileChanged(activeProfile, oldProfile);
    }

    public Profile getActiveProfile() {
        return activeProfile;
    }

    public boolean existsProfile(String profileId) {
        return storage.existsProfile(profileId);
    }

    public Profile getProfile(String profileId) throws StubsException {
        try {
            return storage.getProfile(profileId);
        } catch (StubsStorageException e) {
            throw new StubsException("Can't load profiles", e);
        }
    }

    public List<Profile> listProfiles() throws StubsException {
        try {
            return storage.listProfiles();
        } catch (StubsStorageException e) {
            throw new StubsException("Can't load profiles", e);
        }
    }

    public void saveProfile(Profile profile) throws StubsException {
        try {
            storage.saveProfile(profile);
            fireProfileSaved(profile);
        } catch (StubsStorageException e) {
            throw new StubsException("Can't create profile", e);
        }
    }

    public void deleteProfile(Profile profile) throws StubsException {
        try {
            storage.deleteProfile(profile);
            if (activeProfile.equals(profile)) {
                // todo: select first profile?
                setActiveProfile(null);
            }
        } catch (StubsStorageException e) {
            throw new StubsException("Can't delete profile", e);
        }
    }
    // =================================================================================================================


    // ~ Service Stubs =================================================================================================
    public ServiceStub getServiceStub(Class<?> serviceClass) throws StubsException {
        // TODO: cache

        List<ServiceStub> stubs = listServiceStubs();
        for (ServiceStub stub : stubs) {
            if (serviceClass.equals(stub.getServiceClass())) {
                return stub;
            }
        }

        return null;
    }

    public ServiceStub getServiceStub(String serviceStubId) throws StubsException {
        if (getCache().isCachedServiceStub(serviceStubId)) {
            return getCache().getServiceStub(serviceStubId);
        }

        ServiceStub stub;
        try {
            stub = storage.getServiceStub(serviceStubId, activeProfile.getId());
        } catch (StubsStorageException e) {
            throw new StubsException("Can't load service stub", e);
        }

        getCache().put(stub);
        return stub;
    }

    public List<ServiceStub> listServiceStubs() throws StubsException {
        if (activeProfile == null) {
            return Collections.emptyList();
        }

        if (getCache().isCachedAllServiceStubs()) {
            return getCache().getServiceStubs();
        }

        List<ServiceStub> stubs;
        try {
            stubs = storage.listServiceStubs(activeProfile.getId());
        } catch (StubsStorageException e) {
            throw new StubsException("Can't load service stub", e);
        }

        getCache().putAllServiceStubs(stubs);
        return stubs;
    }

    public void saveServiceStub(ServiceStub stub) throws StubsException {
        try {
            storage.saveServiceStub(stub);
            getCache().invalidate();
            fireServiceStubSaved(stub);
        } catch (StubsStorageException e) {
            throw new StubsException("Can't save service stub", e);
        }
    }

    public void deleteServiceStub(String serviceStubId) throws StubsException {
        try {
            ServiceStub stub = getServiceStub(serviceStubId);
            storage.deleteServiceStub(serviceStubId, activeProfile.getId());
            getCache().invalidate();
            fireServiceStubDeleted(stub);
        } catch (StubsStorageException e) {
            throw new StubsException("Can't remove service stub", e);
        }
    }
    // =================================================================================================================


    // ~ Method Stubs ==================================================================================================
    public MethodStub getMethodStub(Method method) throws StubsException {
        // TODO: cache

        List<MethodStub> methodStubs = listMethodStubs(method.getDeclaringClass());
        for (MethodStub m : methodStubs) {
            if (method.equals(m.getMethod())) {
                return m;
            }
        }

        return null;
    }

    public MethodStub getMethodStub(String serviceStubId, String methodStubId) throws StubsException {
        if (getCache().isCachedMethodStub(methodStubId)) {
            return getCache().getMethodStub(methodStubId);
        }

        MethodStub stub;
        try {
            stub = storage.getMethodStub(serviceStubId, methodStubId, activeProfile.getId());
        } catch (StubsStorageException e) {
            throw new StubsException("Can't load service stub", e);

        }

        getCache().put(stub);
        return stub;
    }

    public List<MethodStub> listMethodStubs(Class<?> serviceClass) throws StubsException {
        // TODO: cache

        ServiceStub stub = getServiceStub(serviceClass);
        if (stub == null) {
            return Collections.emptyList();
        }

        return listMethodStubs(stub.getId());
    }

    public List<MethodStub> listMethodStubs(String serviceStubId) throws StubsException {
        if (getCache().isCachedMethodStubs(serviceStubId)) {
            return getCache().getMethodStubs(serviceStubId);
        }

        List<MethodStub> stubs;
        try {
            stubs = storage.listMethodStubs(serviceStubId, activeProfile.getId());
        } catch (StubsStorageException e) {
            throw new StubsException("Can't load service's method stubs", e);
        }

        getCache().put(serviceStubId, stubs);
        return stubs;
    }

    public void saveMethodStub(MethodStub stub) throws StubsException {
        try {
            storage.saveMethodStub(stub, activeProfile.getId());
            getCache().invalidate();
            fireMethodStubSaved(stub);
        } catch (StubsStorageException e) {
            throw new StubsException("Can't save method stub", e);
        }
    }

    public final void removeMethodStub(String serviceStubId, String methodStubId) throws StubsException {
        try {
            MethodStub stub = getMethodStub(serviceStubId, methodStubId);
            storage.deleteMethodStub(serviceStubId, methodStubId, activeProfile.getId());
            getCache().invalidate();
            fireMethodStubDeleted(stub);
        } catch (StubsStorageException e) {
            throw new StubsException("Can't delete method stub", e);
        }
    }
    // =================================================================================================================


    // ~ Listeners =====================================================================================================
    public void addServiceStubSaveListener(ServiceStubSaveListener listener) {
        if (serviceStubSaveListeners == null) {
            serviceStubSaveListeners = new ArrayList<>();
        }

        serviceStubSaveListeners.add(listener);
    }

    public void addServiceStubDeleteListener(ServiceStubDeleteListener listener) {
        if (serviceStubDeleteListeners == null) {
            serviceStubDeleteListeners = new ArrayList<>();
        }

        serviceStubDeleteListeners.add(listener);
    }

    public void addMethodStubSaveListener(MethodStubSaveListener listener) {
        if (methodStubSaveListeners == null) {
            methodStubSaveListeners = new ArrayList<>();
        }

        methodStubSaveListeners.add(listener);
    }

    public void addMethodStubDeleteListener(MethodStubDeleteListener listener) {
        if (methodStubDeleteListeners == null) {
            methodStubDeleteListeners = new ArrayList<>();
        }

        methodStubDeleteListeners.add(listener);
    }

    public void addProfileChangeListener(ProfileChangeListener listener) {
        if (profileChangeListeners == null) {
            profileChangeListeners = new ArrayList<>();
        }

        profileChangeListeners.add(listener);
    }

    public void addProfileSaveListener(ProfileSaveListener listener) {
        if (profileSaveListeners == null) {
            profileSaveListeners = new ArrayList<>();
        }

        profileSaveListeners.add(listener);
    }

    public void addConfigSaveListener(ConfigSaveListener listener) {
        if (configSaveListeners == null) {
            configSaveListeners = new ArrayList<>();
        }

        configSaveListeners.add(listener);
    }

    private void fireServiceStubSaved(ServiceStub stub) {
        if (serviceStubSaveListeners != null) {
            for (ServiceStubSaveListener listener : serviceStubSaveListeners) {
                listener.onSave(stub);
            }
        }
    }

    private void fireServiceStubDeleted(ServiceStub stub) {
        if (serviceStubDeleteListeners != null) {
            for (ServiceStubDeleteListener listener : serviceStubDeleteListeners) {
                listener.onDelete(stub);
            }
        }
    }

    private void fireMethodStubSaved(MethodStub stub) {
        if (methodStubSaveListeners != null) {
            for (MethodStubSaveListener listener : methodStubSaveListeners) {
                listener.onSave(stub);
            }
        }
    }

    private void fireMethodStubDeleted(MethodStub stub) {
        if (methodStubDeleteListeners != null) {
            for (MethodStubDeleteListener listener : methodStubDeleteListeners) {
                listener.onDelete(stub);
            }
        }
    }

    private void fireProfileChanged(Profile newProfile, Profile oldProfile) {
        if (profileChangeListeners != null) {
            for (ProfileChangeListener listener : profileChangeListeners) {
                listener.onChange(newProfile, oldProfile);
            }
        }
    }

    private void fireProfileSaved(Profile profile) {
        if (profileSaveListeners != null) {
            for (ProfileSaveListener listener : profileSaveListeners) {
                listener.onSave(profile);
            }
        }
    }

    private void fireConfigSaved(Config config) {
        if (configSaveListeners != null) {
            for (ConfigSaveListener listener : configSaveListeners) {
                listener.onSave(config);
            }
        }
    }
    // =================================================================================================================


    private Cache getCache() {
        if (!profileCache.containsKey(activeProfile)) {
            profileCache.put(activeProfile, new Cache());
        }

        return profileCache.get(activeProfile);
    }

    private static class Cache {

        private boolean allServiceStubs;
        private Map<String, ServiceStub> serviceStubsCache;
        private Map<String, MethodStub> methodStubsCache;
        private Map<String, List<MethodStub>> serviceMethodStubsCache;

        private void put(ServiceStub stub) {
            if (serviceStubsCache == null) {
                serviceStubsCache = new WeakHashMap<>();
            }

            serviceStubsCache.put(stub.getId(), stub);
        }

        private void putAllServiceStubs(List<ServiceStub> stubs) {
            if (serviceStubsCache == null) {
                serviceStubsCache = new WeakHashMap<>();
            }

            for (ServiceStub stub : stubs) {
                serviceStubsCache.put(stub.getId(), stub);
            }
            allServiceStubs = true;
        }

        private void put(MethodStub stub) {
            if (methodStubsCache == null) {
                methodStubsCache = new WeakHashMap<>();
            }

            methodStubsCache.put(stub.getId(), stub);
        }

        private void put(String serviceStubId, List<MethodStub> stubs) {
            if (serviceMethodStubsCache == null) {
                serviceMethodStubsCache = new WeakHashMap<>();
            }

            serviceMethodStubsCache.put(serviceStubId, stubs);
        }

        private boolean isCachedAllServiceStubs() {
            return allServiceStubs;
        }

        private boolean isCachedServiceStub(String serviceStubId) {
            return serviceStubsCache != null && serviceStubsCache.containsKey(serviceStubId);
        }

        private List<ServiceStub> getServiceStubs() {
            return new ArrayList<>(serviceStubsCache.values());
        }

        private ServiceStub getServiceStub(String serviceStubId) {
            return serviceStubsCache.get(serviceStubId);
        }

        private boolean isCachedMethodStubs(String serviceStubId) {
            return serviceMethodStubsCache != null && serviceMethodStubsCache.containsKey(serviceStubId);
        }

        private boolean isCachedMethodStub(String methodStubId) {
            return methodStubsCache != null && methodStubsCache.containsKey(methodStubId);
        }

        private List<MethodStub> getMethodStubs(String serviceStubId) {
            return Collections.unmodifiableList(serviceMethodStubsCache.get(serviceStubId));
        }


        private MethodStub getMethodStub(String methodStubId) {
            return methodStubsCache.get(methodStubId);
        }

        private void invalidate() {
            allServiceStubs = false;
            serviceStubsCache = null;
            serviceMethodStubsCache = null;
            methodStubsCache = null;
        }
    }
}
