package wang.xiunian.android.simplecode.urihandler;

import android.app.Application;

import wang.xiunian.android.simplecode.module.ModuleHandlerStore;
import wang.xiunian.android.simplecode.module.UriDispatcherHandler;

/**
 * Created by wangxiunian on 2016/11/2.
 */

public class MethodAStore extends ModuleHandlerStore {

    public MethodAStore(Application application) {
        super(application);
    }

    @Override
    protected <T extends UriDispatcherHandler> Class<T>[] registHandlers() {
        return new Class[]{
                HandlerA.class
        };
    }
}
