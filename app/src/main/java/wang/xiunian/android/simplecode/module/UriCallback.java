package wang.xiunian.android.simplecode.module;

import org.json.JSONObject;

import wang.xiunian.android.simplecode.exception.BaseAppException;

/**
 * 当uri 发送的后由发送者处理的回调 Created by wangxiunian on 2016/7/7.
 */
public interface UriCallback {
    /**
     * 处理成功之后回调，此回调不一定会在主线程，因此不能在内部直接访问ui
     */
    void onSuccess(JSONObject result);

    void onFailed(BaseAppException e);
}
