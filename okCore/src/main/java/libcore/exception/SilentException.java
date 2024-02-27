package libcore.exception;

/**
 * Created by Lucio on 2021/7/27.
 * <p>
 * 静默异常，捕获之后不做显示提示处理
 */
public class SilentException extends RuntimeException {

    public SilentException() {
    }

    public SilentException(String s) {
        super(s);
    }

    public SilentException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public SilentException(Throwable throwable) {
        super(throwable);
    }

}
