package wang.xiunian.android.simplecode.exception;

import java.io.IOException;

/**
 * Created by wangxiunian on 2016/11/2.
 */

public class BaseAppException extends RuntimeException {
    private Object mAttachObject;

    public enum Kind {
        /**
         * 当服务器通讯时, {@link IOException} 发生 .
         */
        NETWORK,
        /**
         * 　当(反)序列化时抛出的异常
         */
        CONVERSION,
        /**
         * 　接受到的不是200的状态码 A non-200 HTTP status code was received from the server.
         */
        HTTP,
        /**
         * 　执行请求时引发的内容错误, 最佳实践是重新抛出异常由应用程序来处理
         */
        UNEXPECTED,
        /**
         * 　和服务器通讯成功, 但是返回的
         * 中的 {@code resultCode} 为失败状态, 也应该重新抛出由应用程序处理
         */
        BUSINESS
    }

    private Kind kind;

    private String code;

    private String message;

    public BaseAppException(Kind kind, String code, String message) {
        this(kind, code, message, null);
    }

    public BaseAppException(Kind kind, String code, String message, Object attachObject) {
        super("errorCode:" + code + ", errorMessage:" + message);
        this.kind = kind;
        this.code = code;
        this.message = message;
        this.mAttachObject = attachObject;
    }

    public Kind getKind() {
        return kind;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }


}
