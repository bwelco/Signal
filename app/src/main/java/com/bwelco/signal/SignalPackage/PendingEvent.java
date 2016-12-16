package com.bwelco.signal.SignalPackage;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by bwelco on 2016/12/16.
 */

public class PendingEvent {

    // 对象池 防止频繁创建对象
    public final static ArrayList<PendingEvent> PENDING_EVENT_POOL = new ArrayList<PendingEvent>();

    private final static int POOL_SIZE = 10;

    private final static Stack<Integer> free_index = new Stack<Integer>();

    // 对象池个数
    static {
        for (int i = 0; i < POOL_SIZE; i++) {
            PENDING_EVENT_POOL.add(new PendingEvent(null, null));
            free_index.add(i);
        }
    }

    Event event;
    RegisterInfo registerInfo;
    PendingEvent next;
    private int index;

    private PendingEvent(Event event, RegisterInfo registerInfo) {
        this.event = event;
        this.registerInfo = registerInfo;
    }

    static PendingEvent obtainPendingPost(Event event, RegisterInfo registerInfo) {
        synchronized (PENDING_EVENT_POOL) {
            // 池里面对象不够用了
            if (free_index.size() == 0) return new PendingEvent(event, registerInfo);
            else {
                int index = free_index.pop();
                PendingEvent pendingEvent = PENDING_EVENT_POOL.get(index);
                pendingEvent.event = event;
                pendingEvent.registerInfo = registerInfo;
                pendingEvent.index = index;
                pendingEvent.next = null;
                return pendingEvent;
            }
        }
    }

    static void releasePendingEvent(PendingEvent pendingEvent) {
        synchronized (PENDING_EVENT_POOL) {
            pendingEvent.event = null;
            pendingEvent.registerInfo = null;
            pendingEvent.next = null;
            free_index.push(pendingEvent.index);
        }
    }

}
