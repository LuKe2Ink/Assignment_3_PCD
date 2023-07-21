package it.unibo.ppc.utils;

import java.util.Objects;

public class RangeClass {
    private int max;
    private int min;

    public RangeClass(int max, int min) {
        this.max = max;
        this.min = min;
    }

    public boolean inRange(final int n){
        return (n >=min && max == -1) || (n > min && n <=max);
    }

    public boolean lastRange(){
        return max == -1;
    }

    @Override
    public String toString() {
        return "[" +
                String.valueOf(min)+
                ", " + (max == -1 ? "inf[" : String.valueOf(max) + ']');
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RangeClass that = (RangeClass) o;
        return max == that.max && min == that.min;
    }

    @Override
    public int hashCode() {
        return Objects.hash(max, min);
    }
}

