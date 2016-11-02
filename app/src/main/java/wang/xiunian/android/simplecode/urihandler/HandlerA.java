package wang.xiunian.android.simplecode.urihandler;

import android.app.Application;

import wang.xiunian.android.L;
import wang.xiunian.android.simplecode.module.UriDispatcherHandler;
import wang.xiunian.android.simplecode.module.UriResponseCallback;
import wang.xiunian.android.simplecode.module.UriWraper;

/**
 * Created by wangxiunian on 2016/11/2.
 */

public class HandlerA extends UriDispatcherHandler {
    public HandlerA(Application application) {
        super(application);
    }

    @Override
    protected String initModuleName() {
        return "methodA";
    }

    @Override
    public void onReceiveUri(UriWraper uri, UriResponseCallback result) {
        L.d("received");
    }

    @Override
    public void onResponsed() {

    }
}
