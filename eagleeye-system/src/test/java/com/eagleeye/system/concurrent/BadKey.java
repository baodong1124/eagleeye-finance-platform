package com.eagleeye.system.concurrent;

public class BadKey {
    int id;

    BadKey(int id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return 1;
    } // 所有对象 Hash 都是 1

    @Override
    public boolean equals(Object o) {
        return this.id == ((BadKey) o).id;
    }
}
