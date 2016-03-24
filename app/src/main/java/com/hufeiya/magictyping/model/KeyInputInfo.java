package com.hufeiya.magictyping.model;

/**
 * Created by hufeiya on 16/3/24.
 */
public class KeyInputInfo {
    private char key;
    private long time;

    public KeyInputInfo(char key, long time) {
        this.key = key;
        this.time = time;
    }

    public char getKey() {
        return key;
    }

    public void setKey(char key) {
        this.key = key;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
