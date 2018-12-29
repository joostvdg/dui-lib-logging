package com.github.joostvdg.dui.logging.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SimpleLoggerThread extends Thread {
    private boolean shutDown = false;
    private int queued;
    private final BlockingQueue<String> logQueue;
    private final Object synchronizer;
    private boolean outputDisabled = false;

    SimpleLoggerThread(Object synchronizer) {
        this.synchronizer = synchronizer;
        this.logQueue = new LinkedBlockingQueue<>();
    }

    public void disableOutput() {
        this.outputDisabled = true;
    }

    public void queue(){
        ++queued;
    }

    public void log(String logMessage) throws InterruptedException {
        logQueue.put(logMessage);
    }

    /**
     * This is for testing and debug purposes only.
     * Do not use for normal processing.
     * @return a message from the queue. Or a message "N.A." if there was none.
     */
    public String copyAMessage() {
        String message = "N.A.";
        synchronized (synchronizer) {
            try {
                if(!logQueue.isEmpty()) {
                    message = logQueue.take();
                    logQueue.add(message);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return message;
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

                if (outputDisabled) {
                    sleep(100);
                } else {
                    String message = logQueue.take();
                    synchronized (synchronizer) {
                        --queued; // we've taken up a message, less people queue'd for sure
                    }

                    if (message.startsWith(Constants.ERROR_FLAG_PREFIX)) {
                        System.err.println(message.substring(Constants.ERROR_FLAG_PREFIX.length()));
                    } else {
                        System.out.println(message);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
