package com.songoda.ultimatestacker.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Async {

    private static ExecutorService executor;

    public static void start() {
        executor = Executors.newFixedThreadPool(5);
    }

    public static void run(Runnable runnable) {
        executor.execute(runnable);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
