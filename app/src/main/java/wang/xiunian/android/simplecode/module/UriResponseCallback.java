package wang.xiunian.android.simplecode.module;

import org.json.JSONObject;

import wang.xiunian.android.L;
import wang.xiunian.android.simplecode.exception.BaseAppException;

/**
 * Created by wangxiunian on 2016/7/8.
 */
public class UriResponseCallback implements UriCallback {
    UriCallback mUriCallback;
    UriResponseListener mUriResponseListener;

    UriResponseCallback(UriCallback uriCallback, UriResponseListener responseListener) {
        this.mUriCallback = uriCallback;
        this.mUriResponseListener = responseListener;
    }

    public void onSuccess(JSONObject result) {
        if (result != null) {
            L.d("result", result.toString());
        }
        if (mUriCallback != null) {
            this.mUriCallback.onSuccess(result);
        } else {
            L.e("catched callback is null");
        }
        if (mUriResponseListener != null) {
            mUriResponseListener.onResponsed();
        } else {
            L.e("catched listener is null");
        }
    }

    public void onFailed(BaseAppException e) {
        if (mUriCallback != null) {
            this.mUriCallback.onFailed(e);
        } else {
            L.e("catched callback is null");
        }
        if (mUriResponseListener != null) {
            mUriResponseListener.onResponsed();
        } else {
            L.e("catched listener is null");
        }
    }

    interface UriResponseListener {
        void onResponsed();
    }

}
