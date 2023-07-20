package it.unibo.ppc.utilities;

public class Flag {
    private boolean flag;

    @Override
    public String toString() {
        return "Flag{" +
                "flag=" + flag +
                '}';
    }

    public Flag() {
        flag = false;
    }

    public synchronized void reset() {
        flag = false;
    }

    public synchronized void set() {
        flag = true;
    }

    public synchronized boolean isSet() {
        return flag;
    }
}