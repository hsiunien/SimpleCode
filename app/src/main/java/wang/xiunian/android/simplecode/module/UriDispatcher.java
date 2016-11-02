package wang.xiunian.android.simplecode.module;

import android.net.Uri;

import org.json.JSONObject;

import wang.xiunian.android.L;
import wang.xiunian.android.simplecode.exception.BaseAppException;

/**
 * Created by wangxiunian on 2016/6/18.
 */
public class UriDispatcher {
    public static void dispatcher(UriWraper uri, UriCallback callback) throws NotSupportSchemeException {
        if (checkSupport(uri.getUri())) {
            ModuleHandlerStore.dispatch2Module(uri, callback);
        }
    }

    public static void dispatcher(UriWraper uri) throws NotSupportSchemeException {
        dispatcher(uri, new UriCallback() {
            @Override
            public void onSuccess(JSONObject result) {
                L.i("receive result,but not process it");
            }

            @Override
            public void onFailed(BaseAppException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 检查是否是wallet 协议 或者做调用权限校验
     *
     * @param uri
     * @return
     */
    public static boolean checkSupport(Uri uri) {
        if (uri == null) {
            return false;
        }

        return true;
    }
}
