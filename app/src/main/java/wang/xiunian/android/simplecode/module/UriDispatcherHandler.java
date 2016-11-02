package wang.xiunian.android.simplecode.module;

import android.app.Application;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import wang.xiunian.android.simplecode.exception.BaseAppException;
import wang.xiunian.android.simplecode.utils.StringUtils;

/**
 * Created by wangxiunian on 2016/6/17.
 */

public abstract class UriDispatcherHandler implements UriResponseCallback.UriResponseListener, UnProguardable {
    private static final Map<String, UriDispatcherHandler> registryMap = new HashMap<String, UriDispatcherHandler>();

    protected Application mApplication;

    public static <T extends UriDispatcherHandler> T getInstance(final Class<T> clazz,
                                                                 Application application)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String clazzName = clazz.getName();
        if (!registryMap.containsKey(clazzName)) {
            synchronized (registryMap) {

                if (!registryMap.containsKey(clazzName)) {
                    Constructor co = clazz.getDeclaredConstructor(Application.class);
                    return (T) co.newInstance(application);
                }
            }
        }
        return (T) registryMap.get(clazzName);
    }

    public UriDispatcherHandler(Application application) {
        mApplication = application;
        String clazzName = this.getClass().getName();
        if (registryMap.containsKey(clazzName)) {
            throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "",
                    "Cannot construct instance for class " + clazzName + ", since an instance already exists!");
        } else {
            synchronized (registryMap) {
                if (registryMap.containsKey(clazzName)) {
                    throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "",
                            "Cannot construct instance for class " + clazzName + ", since an instance already exists!");
                } else {
                    registryMap.put(clazzName, this);
                }
            }
        }
    }


    /**
     * 初始化的时候必须实现
     *
     * @return moduleName; 对于应用中唯一存在,不区分大小写
     * 以"_"开头标识此方法不支持覆盖。其他模块如果覆盖会报异常。否则可以被其他模块同名方法覆盖
     * 取决于后面添加的模块覆盖前面添加的同名模块
     */
    abstract protected String initModuleName();

    /**
     * 当有消息收到的时候以uri传递 数据
     */
    abstract public void onReceiveUri(UriWraper uri, UriResponseCallback result);

    /**
     * 在onReceiveUri之前调用，如果抛出异常，则不再继续调用onReceiveUri
     *
     * @param uri
     * @throws BaseAppException
     */
    protected void preReceiveUri(UriWraper uri) throws BaseAppException {
    }

    /**
     * 方法接收的入口
     * @param uri
     * @param result
     */
    final synchronized void onReceiveUri(UriWraper uri, UriCallback result) {
        UriResponseCallback responseCallback = new UriResponseCallback(result, this);
        try {
            preReceiveUri(uri);
            onReceiveUri(uri, responseCallback);
        } catch (BaseAppException e) {
            e.printStackTrace();
            responseCallback.onFailed(e);
        }
    }


    /**
     * 通用校验context是否为空，用于判断uriwraper中context 为空的情况
     *
     * @param uriWraper
     * @throws BaseAppException
     */
    protected void checkContext(UriWraper uriWraper) throws BaseAppException {
        if (uriWraper == null) {
            throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "", "uriWraper is null");
        }
        if (uriWraper.getSendSource() == null) {
            throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "", "call resource is null,please call UriWraper.from(String,Activity)");
        }
    }

    /**
     * @param uriWraper wraper
     * @param key       key
     * @return 如果值为空 则会抛出异常
     * @throws BaseAppException
     */
    protected String getNotNullParameter(UriWraper uriWraper, String key) throws BaseAppException {
        String value = uriWraper.getQueryParameter(key);
        if (StringUtils.isEmpty(value)) {
            throw new BaseAppException(BaseAppException.Kind.UNEXPECTED, "1001", key + " is null");
        }
        return value;
    }

}
