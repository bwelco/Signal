package com.bwelco.signal;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by bwelco on 2016/12/14.
 */

public class AsyncSender implements Runnable, EventHandler {

    Signal signal;
    LinkedBlockingQueue<PendingEvent> queue;

    public AsyncSender(Signal signal) {
        this.signal = signal;
        queue = new LinkedBlockingQueue<PendingEvent>();
    }

    @Override
    public void handleEvent(Event event, RegisterInfo registerInfo) {
        PendingEvent pendingEvent = PendingEvent.obtainPendingPost(event, registerInfo);

        try {
            queue.put(pendingEvent);
        } catch (InterruptedException e) {
            throw new IllegalStateException("pending queue is full.");
        }

        signal.getExecutorService().submit(this);
    }

    @Override
    public void run() {

        PendingEvent pendingEvent = null;

        try {
            pendingEvent = queue.take();
        } catch (InterruptedException e) {
            throw new IllegalStateException("can't get pending event from LinkedBlockingQueue.");
        }

        try {
            Thread.sleep(pendingEvent.event.getDelayMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        signal.invokeRegister(pendingEvent.registerInfo, pendingEvent.event.getParams());
        PendingEvent.releasePendingEvent(pendingEvent);
    }
}
