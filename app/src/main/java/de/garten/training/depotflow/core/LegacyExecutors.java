package de.garten.training.depotflow.core;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LegacyExecutors {

    private final ExecutorService diskIo = Executors.newSingleThreadExecutor();
    private final ExecutorService networkIo = Executors.newFixedThreadPool(3);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public void disk(Runnable runnable) {
        diskIo.execute(runnable);
    }

    public void network(Runnable runnable) {
        networkIo.execute(runnable);
    }

    public void main(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public void shutdown() {
        diskIo.shutdownNow();
        networkIo.shutdownNow();
    }
}
