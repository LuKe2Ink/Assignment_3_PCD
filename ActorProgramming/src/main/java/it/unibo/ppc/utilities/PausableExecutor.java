package it.unibo.ppc.utilities;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class PausableExecutor extends ScheduledThreadPoolExecutor {
    private Continue cont;

    public PausableExecutor(int corePoolSize, ThreadFactory threadFactory, Continue c) {
        super(corePoolSize, threadFactory);
        cont = c;
    }

    protected void beforeExecute(Thread t, Runnable r) {
        try {
            cont.checkIn();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        super.beforeExecute(t, r);
    }
}