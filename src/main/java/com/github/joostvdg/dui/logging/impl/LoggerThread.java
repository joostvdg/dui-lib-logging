package com.github.joostvdg.dui.logging.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class LoggerThread extends Thread {
    private transient boolean shutDown = false;
    private transient int queued;
    private transient final BlockingQueue<String> logQueue;
    private transient final Object synchronizer;

    LoggerThread(Object synchronizer) {
        this.synchronizer = synchronizer;
        this.logQueue = new LinkedBlockingQueue<>();
    }

    public void queue(){
        ++queued;
    }

    public void log(String logMessage) throws InterruptedException {
        logQueue.put(logMessage);
    }

    @Override
    public void run(){
        while(true) {
            try {
                synchronized (synchronizer) {
                    if (shutDown && queued == 0) {
                        break; // we're done now
                    }
                }

                String message = logQueue.take();
                synchronized (synchronizer) {
                    --queued; // we've taken up a message, less people queue'd for sure
                }

                if (message.startsWith(Constants.ERROR_FLAG_PREFIX)) {
                    System.err.println(message.substring(Constants.ERROR_FLAG_PREFIX.length()));
                } else {
                    System.out.println(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
