package bbbwd.bubbleworld.utils.func;

/** A cons that throws something. */
public interface ConsT<T, E extends Throwable>{
    void get(T t) throws E;
}
