package com.ai.paas.ipaas.ccs.zookeeper;

import org.apache.curator.framework.recipes.locks.InterProcessLock;

import java.util.concurrent.TimeUnit;

/**
 * Created by astraea on 2015/4/28.
 */
public class MutexLock {

    private InterProcessLock processLock;

    public MutexLock(InterProcessLock processLock) {
        this.processLock = processLock;
    }

    public void acquire() throws Exception {
        processLock.acquire();
    }

    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return processLock.acquire(time, unit);
    }

    public void release() throws Exception {
        processLock.release();
    }

    public boolean isAcquiredInThisProcess() {
        return processLock.isAcquiredInThisProcess();
    }
}
