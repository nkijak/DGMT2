package com.kinnack.dgmt2.option;

import java.util.Collection;

public class Joiner {
    private String _joint;
    private Joiner(String joint) {
        _joint = joint;
    }
    public static Joiner on(String joint) {
        return new Joiner(joint);
    }
    public String join(Iterable<?> items) {
        StringBuffer buffer = new StringBuffer();
        for(Object o : items) {
            buffer.append(o.toString());
            buffer.append(_joint);
        }
        buffer.delete(buffer.length() - _joint.length(), buffer.length());
        return buffer.toString();
    }
    public<T> String join(T[] items) {
        StringBuffer buffer = new StringBuffer();
        for(Object o : items) {
            buffer.append(o.toString());
            buffer.append(_joint);
        }
        buffer.delete(buffer.length() - _joint.length(), buffer.length());
        return buffer.toString();
    }
}
