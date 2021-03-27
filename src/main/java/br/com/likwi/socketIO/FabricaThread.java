package br.com.likwi.socketIO;

import java.util.concurrent.ThreadFactory;

public class FabricaThread implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {

        final Thread thread = new Thread(r);
        thread.setUncaughtExceptionHandler(new CaughtThreadException());
        return thread;
    }
}
