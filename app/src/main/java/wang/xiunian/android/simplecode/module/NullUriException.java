package wang.xiunian.android.simplecode.module;

import wang.xiunian.android.simplecode.exception.BaseAppException;

/**
 *
 */
public class NullUriException extends BaseAppException {
    public NullUriException() {
        super(Kind.UNEXPECTED, "", "uri is null");
    }
}
