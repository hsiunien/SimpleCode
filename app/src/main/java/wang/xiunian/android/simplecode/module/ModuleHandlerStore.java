package wang.xiunian.android.simplecode.module;

import android.app.Application;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import wang.xiunian.android.L;
import wang.xiunian.android.simplecode.exception.BaseAppException;

/**
 * Created by wangxiunian on 2016/6/18.
 */
public abstract class ModuleHandlerStore implements UnProguardable {
    private static final Map<Class, ModuleHandlerStore> sRegistryMap = new HashMap<Class, ModuleHandlerStore>();
    private final HashMap<String, UriDispatcherHandler> mModuleHandlers = new HashMap<>();
    private Application mApplication;

    public static void addModule(Application application, Class<? extends ModuleHandlerStore>... moduleClasses) {
        synchronized (sRegistryMap) {
            for (Class<? extends ModuleHandlerStore> moduleClass : moduleClasses) {
                ModuleHandlerStore store = null;
                try {
                    store = getInstance(application, moduleClass);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                if (store != null) {
                    store.addModuleHandler(application, store.registHandlers());
                }
            }
        }
    }

    private static <T extends ModuleHandlerStore> T getInstance(Application application, final Class<T> clazz)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (!sRegistryMap.containsKey(clazz)) {
            synchronized (sRegistryMap) {
                if (!sRegistryMap.containsKey(clazz)) {
                    Constructor co = clazz.getDeclaredConstructor(Application.class);
                    return (T) co.newInstance(application);
                }
            }
        }
        return (T) sRegistryMap.get(clazz);
    }

    public ModuleHandlerStore(Application application) {
        this.mApplication = application;
        if (sRegistryMap.containsKey(this.getClass())) {
            throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "",
                    "Cannot construct instance for class " + this.getClass() + ", since an instance already exists!");
        } else {
            synchronized (sRegistryMap) {
                if (sRegistryMap.containsKey(this.getClass())) {
                    throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "",
                            "Cannot construct instance for class " + this.getClass() + ", since an instance already exists!");
                } else {
                    sRegistryMap.put(this.getClass(), this);
                }
            }
        }
//        L.d("sss");
    }

    protected Application getApplcation() {
        return mApplication;
    }

    @SuppressWarnings("unchecked")
    protected abstract <T extends UriDispatcherHandler> Class<T>[] registHandlers();

    static void dispatch2Module(UriWraper uri) {
        dispatch2Module(uri, null);
    }

    static void dispatch2Module(UriWraper uri, UriCallback callback) {
        synchronized (sRegistryMap) {
            boolean isSupported = false;
            for (Map.Entry<Class, ModuleHandlerStore> entry : sRegistryMap.entrySet()) {
                try {
                    entry.getValue().dispatcherUri(uri, callback);
                    isSupported = true;
                    break;
                } catch (NotSupportSchemeException e) {
                    L.v(e.getMessage());
                }
            }
            if (!isSupported) {
                throw new NotSupportSchemeException("all", uri.getHost());
            }
        }
    }


    void dispatcherUri(UriWraper uri, UriCallback callback) throws NotSupportSchemeException {
        if (uri == null) {
            return;
        }
        //return module name;
        String host = uri.getHost();
        if (mModuleHandlers.containsKey(host)) {
            UriDispatcherHandler handler = mModuleHandlers.get(host);
            handler.onReceiveUri(uri, callback);
        } else if (mModuleHandlers.containsKey("_" + host)) {
            UriDispatcherHandler handler = mModuleHandlers.get("_" + host);
            handler.onReceiveUri(uri, callback);
        } else {
            throw new NotSupportSchemeException(getClass().getSimpleName(), host);
        }
    }

    final void addModuleHandler(Application application, Class<? extends UriDispatcherHandler>... handlerClasses) {
        if (handlerClasses == null || handlerClasses.length == 0) {
            return;
        }
        UriDispatcherHandler[] handlers = initUriDispatcher(application, handlerClasses);
        for (UriDispatcherHandler handler : handlers) {
            if (handler == null) {
                continue;
            }
            String handlerName = handler.initModuleName();
            if (handlerName == null) {
                throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "",
                        "handler init name can not be empty ");
            }
            handlerName = handlerName.trim();
            if (mModuleHandlers.containsKey(handlerName) && handlerName.startsWith("_")) {
                L.e("Handlers Already Contain key:" + handlerName);
                UriDispatcherHandler cachedDispatcherHandler = mModuleHandlers.get(handlerName);
                if (cachedDispatcherHandler != handler) {
                    throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "", "Handler name["
                            + handlerName + "] have" + "repeated!!!");
                }
            } else {
                checkExisted(application, handlerName, handler);
                mModuleHandlers.put(handlerName, handler);
            }
        }
    }

    private void checkExisted(Application application, String handlerName, UriDispatcherHandler handler) {
        for (Class aClass : sRegistryMap.keySet()) {
            try {
                String clearHandlerName = handlerName.startsWith("_") ? handlerName.substring(1) : handlerName;
                String testHandlerName = "_" + clearHandlerName;
                if (getInstance(application, aClass).mModuleHandlers.containsKey(testHandlerName)) {
                    throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "",
                            "Handler name:" + handlerName + " have existed and couldn't override by " +
                                    "other module in App");
                }
                if (getInstance(application, aClass).mModuleHandlers.containsKey(clearHandlerName)) {
                    if (getInstance(application, aClass).mModuleHandlers.get(clearHandlerName) != handler) {
                        L.e(handlerName + " override by newest handler");
                    }
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    private UriDispatcherHandler[] initUriDispatcher(Application application, Class... uriHandlerClazz) {
        UriDispatcherHandler[] handlers = new UriDispatcherHandler[uriHandlerClazz.length];
        for (int i = 0; i < uriHandlerClazz.length; i++) {
            Class c = uriHandlerClazz[i];
            if (c == null) {
                continue;
            }
            try {
                handlers[i] = UriDispatcherHandler.getInstance(c, application);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return handlers;
    }
}
