/*
 * Decompiled with CFR 0.1.0 (FabricMC a830a72d).
 * 
 * Could not load the following classes:
 *  com.mojang.logging.LogUtils
 *  java.lang.CharSequence
 *  java.lang.IllegalStateException
 *  java.lang.InterruptedException
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.Thread
 *  java.lang.Throwable
 *  java.util.Arrays
 *  java.util.Objects
 *  java.util.concurrent.Semaphore
 *  java.util.concurrent.locks.Lock
 *  java.util.concurrent.locks.ReentrantLock
 *  java.util.stream.Collectors
 *  java.util.stream.Stream
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 */
package net.minecraft.util;

import com.mojang.logging.LogUtils;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import org.slf4j.Logger;

public class ThreadingDetector {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String name;
    private final Semaphore lock = new Semaphore(1);
    private final Lock stackTraceLock = new ReentrantLock();
    @Nullable
    private volatile Thread threadThatFailedToAcquire;
    @Nullable
    private volatile ReportedException fullException;

    public ThreadingDetector(String $$0) {
        this.name = $$0;
    }

    public void checkAndLock() {
        block6: {
            boolean $$0 = false;
            try {
                this.stackTraceLock.lock();
                if (this.lock.tryAcquire()) break block6;
                this.threadThatFailedToAcquire = Thread.currentThread();
                $$0 = true;
                this.stackTraceLock.unlock();
                try {
                    this.lock.acquire();
                }
                catch (InterruptedException $$1) {
                    Thread.currentThread().interrupt();
                }
                throw this.fullException;
            }
            finally {
                if (!$$0) {
                    this.stackTraceLock.unlock();
                }
            }
        }
    }

    public void checkAndUnlock() {
        try {
            this.stackTraceLock.lock();
            Thread $$0 = this.threadThatFailedToAcquire;
            if ($$0 != null) {
                ReportedException $$1;
                this.fullException = $$1 = ThreadingDetector.makeThreadingException(this.name, $$0);
                this.lock.release();
                throw $$1;
            }
            this.lock.release();
        }
        finally {
            this.stackTraceLock.unlock();
        }
    }

    public static ReportedException makeThreadingException(String $$0, @Nullable Thread $$1) {
        String $$2 = (String)Stream.of((Object[])new Thread[]{Thread.currentThread(), $$1}).filter(Objects::nonNull).map(ThreadingDetector::stackTrace).collect(Collectors.joining((CharSequence)"\n"));
        String $$3 = "Accessing " + $$0 + " from multiple threads";
        CrashReport $$4 = new CrashReport($$3, (Throwable)new IllegalStateException($$3));
        CrashReportCategory $$5 = $$4.addCategory("Thread dumps");
        $$5.setDetail("Thread dumps", $$2);
        LOGGER.error("Thread dumps: \n" + $$2);
        return new ReportedException($$4);
    }

    private static String stackTrace(Thread $$0) {
        return $$0.getName() + ": \n\tat " + (String)Arrays.stream((Object[])$$0.getStackTrace()).map(Object::toString).collect(Collectors.joining((CharSequence)"\n\tat "));
    }
}