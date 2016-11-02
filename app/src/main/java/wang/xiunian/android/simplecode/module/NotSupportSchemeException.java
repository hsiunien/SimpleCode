package wang.xiunian.android.simplecode.module;


import wang.xiunian.android.simplecode.exception.BaseAppException;

/**
 * Created by wangxiunian on 2016/6/18.
 */
public class NotSupportSchemeException extends BaseAppException {
    public NotSupportSchemeException(String moduleName, String hostName) {
        super(Kind.UNEXPECTED, "", moduleName + " module : not support " + hostName + "!");
    }
}
