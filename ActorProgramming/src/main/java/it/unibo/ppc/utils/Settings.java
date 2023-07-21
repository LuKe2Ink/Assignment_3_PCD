package it.unibo.ppc.utils;

public class Settings {
    private int maxLines;
    private int intervals;
    private int nFiles;

    public Settings(int maxLines, int intervals, int nFiles) {
        this.maxLines = maxLines;
        this.intervals = intervals;
        this.nFiles = nFiles;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public int getIntervals() {
        return intervals;
    }

    public int getnFiles() {
        return nFiles;
    }
}
