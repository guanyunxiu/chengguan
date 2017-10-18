package com.wbapp.omxvideo;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zheng on 2/1/16.
 */
public class ThreadPoolService {

    private static ExecutorService executor;
    private static Object lock = new Object();

    private static ExecutorService videoPlayExecutor;

    // get a default Executor that serves for the long-running tasks.
    public static Executor getThreadPoolExecutor() {
        synchronized (lock) {
            if (executor == null)
                executor = new ThreadPoolExecutor(10 /*core*/, 50 /*max*/, 60 /*timeout*/,
                            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        }
        return executor;
    }

    public static synchronized Executor getVideoPlayExecutor() {
        if (videoPlayExecutor == null)
            videoPlayExecutor = new ThreadPoolExecutor(1 /*core*/, 2 /*max*/, 60 /*timeout*/,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        return videoPlayExecutor;
    }
}