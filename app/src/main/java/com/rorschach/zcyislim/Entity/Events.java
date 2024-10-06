package com.rorschach.zcyislim.Entity;

public class Events<T> {
    int type;
    T data;

    public Events(int type, T data) {
        this.type = type;
        this.data = data;
    }

    public Events(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Events{" +
                "type=" + type +
                ", data=" + data +
                '}';
    }
}
