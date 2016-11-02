package wang.xiunian.android.simplecode.module;

import android.app.Activity;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import wang.xiunian.android.simplecode.utils.StringUtils;

/**
 * Created by wangxiunian on 2016/7/8.
 */
public class UriWraper {
    private Activity mSendSource;
    private Uri mUri;

    /**
     * 如果调用的方法要依赖Activity 或者需要跳转页面的话，则调用这个方法
     */
    public static UriWraper from(String scheme, Activity activity) {
        if (StringUtils.isEmpty(scheme)) {
            throw new NullUriException();
        }
        Uri uri = Uri.parse(scheme);
        UriWraper uriWraper = new UriWraper(uri);
        uriWraper.setSendSource(activity);
        return uriWraper;
    }

    public static UriWraper from(String scheme) {
        return from(scheme.trim(), null);
    }

    /**
     * @param name   新的scheme 名称
     * @param scheme 需要替换的scheme名称
     * @return 新的scheme
     */
    public static UriWraper replaceMethodName(String name, UriWraper scheme) {
        scheme.mUri = scheme.mUri.buildUpon().authority(name).build();
        return scheme;
    }

    private UriWraper(Uri uri) {
        this.mUri = uri;
    }

    public void addParameter(String key, String value) {
        mUri = mUri.buildUpon().appendQueryParameter(key, value).build();
    }

    public void addParameter(String key, int value) {
        addParameter(key, String.valueOf(value));
    }

    void addParameter(JSONObject jsonObject) {
        Iterator<String> iterator = jsonObject.keys();
        Uri.Builder builder = mUri.buildUpon();
        while (iterator.hasNext()) {
            String key = iterator.next();
            builder.appendQueryParameter(key, jsonObject.optString(key));
        }
        mUri = builder.build();
    }

    public String getQueryParameter(String key) {
        return mUri.getQueryParameter(key);
    }

    public <T> T getQueryParameter(String key, T defaultValue) {
        String value = mUri.getQueryParameter(key);
        if (value == null) {
            return defaultValue;
        }
        Type type = defaultValue.getClass();
        try {
            if (type.equals(Float.class)) {
                Float f = Float.parseFloat(value);
                return (T) f;
            } else if (type.equals(Integer.class)) {
                Integer f = Integer.parseInt(value);
                return (T) f;
            } else if (type.equals(Boolean.class)) {
                Boolean b = Boolean.parseBoolean(value);
                return (T) b;
            } else {
                try {
                    return (T) value;
                } catch (Exception e) {
                    e.printStackTrace();
                    return defaultValue;
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public JSONObject getQueryParameter() {
        JSONObject jsonObject = new JSONObject();
        Set<String> keys = getQueryParameterNames();
        for (String key : keys) {
            try {
                jsonObject.put(key, getQueryParameter(key));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public Set<String> getQueryParameterNames() {

        String query = mUri.getEncodedQuery();
        if (query == null) {
            return Collections.emptySet();
        }
        Set<String> names = new LinkedHashSet<String>();
        int start = 0;
        do {
            int next = query.indexOf('&', start);
            int end = (next == -1) ? query.length() : next;

            int separator = query.indexOf('=', start);
            if (separator > end || separator == -1) {
                separator = end;
            }

            String name = query.substring(start, separator);
            names.add(Uri.decode(name));
            // Move start to end of name.
            start = end + 1;
        } while (start < query.length());

        return Collections.unmodifiableSet(names);
    }

    public String getHost() {
        return mUri.getHost();
    }

    /**
     * 从那个activity中跳转过来的
     */
    public Activity getSendSource() {
        return mSendSource;
    }

    public void setSendSource(Activity sendSource) {
        mSendSource = sendSource;
    }

    public Uri getUri() {
        return mUri;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }
}
