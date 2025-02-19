package bbbwd.bubbleworld.utils;

public class Pair<P, Q> {
    public P first;
    public Q second;

    public Pair(P first, Q second) {
        this.first = first;
        this.second = second;
    }

    public static <P, Q> Pair<P, Q> of(P first, Q second) {
        return new Pair<>(first, second);
    }
}
