package microsimulator.ws

import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.AbstractQueuedSynchronizer

class ResettableCountDownLatch
/**
 * Constructs a `CountDownLatch` initialized with the given count.
 *
 * @param count the number of times [.countDown] must be invoked
 * before threads can pass through [.await]
 * @throws IllegalArgumentException if `count` is negative
 */
(count: Int) {

    private val sync: Sync

    /**
     * Returns the current count.
     *
     *
     *
     * This method is typically used for debugging and testing purposes.
     *
     * @return the current count
     */
    val count: Long
        get() = sync.count.toLong()

    /**
     * Synchronization control For CountDownLatch.
     * Uses AQS state to represent count.
     */
    private class Sync internal constructor(val startCount: Int) : AbstractQueuedSynchronizer() {

        internal val count: Int
            get() = state

        init {
            state = startCount
        }

        public override fun tryAcquireShared(acquires: Int): Int {
            return if (state == 0) 1 else -1
        }

        public override fun tryReleaseShared(releases: Int): Boolean {
            // Decrement count; signal when transition to zero
            while (true) {
                val c = state
                if (c == 0)
                    return false
                val nextc = c - 1
                if (compareAndSetState(c, nextc))
                    return nextc == 0
            }
        }

        fun reset() {
            state = startCount
        }

        companion object {
            private val serialVersionUID = 4982264981922014374L
        }
    }

    init {
        if (count < 0) throw IllegalArgumentException("count < 0")
        this.sync = Sync(count)
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is [interrupted][Thread.interrupt].
     *
     *
     *
     * If the current count is zero then this method returns immediately.
     *
     *
     *
     * If the current count is greater than zero then the current
     * thread becomes disabled for thread scheduling purposes and lies
     * dormant until one of two things happen:
     *
     *  * The count reaches zero due to invocations of the
     * [.countDown] method; or
     *  * Some other thread [interrupts][Thread.interrupt]
     * the current thread.
     *
     *
     *
     *
     * If the current thread:
     *
     *  * has its interrupted status set on entry to this method; or
     *  * is [interrupted][Thread.interrupt] while waiting,
     *
     * then [InterruptedException] is thrown and the current thread's
     * interrupted status is cleared.
     *
     * @throws InterruptedException if the current thread is interrupted
     * while waiting
     */
    @Throws(InterruptedException::class)
    fun await() {
        sync.acquireSharedInterruptibly(1)
    }

    fun reset() {
        sync.reset()
    }

    /**
     * Causes the current thread to wait until the latch has counted down to
     * zero, unless the thread is [interrupted][Thread.interrupt],
     * or the specified waiting time elapses.
     *
     *
     *
     * If the current count is zero then this method returns immediately
     * with the value `true`.
     *
     *
     *
     * If the current count is greater than zero then the current
     * thread becomes disabled for thread scheduling purposes and lies
     * dormant until one of three things happen:
     *
     *  * The count reaches zero due to invocations of the
     * [.countDown] method; or
     *  * Some other thread [interrupts][Thread.interrupt]
     * the current thread; or
     *  * The specified waiting time elapses.
     *
     *
     *
     *
     * If the count reaches zero then the method returns with the
     * value `true`.
     *
     *
     *
     * If the current thread:
     *
     *  * has its interrupted status set on entry to this method; or
     *  * is [interrupted][Thread.interrupt] while waiting,
     *
     * then [InterruptedException] is thrown and the current thread's
     * interrupted status is cleared.
     *
     *
     *
     * If the specified waiting time elapses then the value `false`
     * is returned.  If the time is less than or equal to zero, the method
     * will not wait at all.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the `timeout` argument
     * @return `true` if the count reached zero and `false`
     * if the waiting time elapsed before the count reached zero
     * @throws InterruptedException if the current thread is interrupted
     * while waiting
     */
    @Throws(InterruptedException::class)
    fun await(timeout: Long, unit: TimeUnit): Boolean {
        return sync.tryAcquireSharedNanos(1, unit.toNanos(timeout))
    }

    /**
     * Decrements the count of the latch, releasing all waiting threads if
     * the count reaches zero.
     *
     *
     *
     * If the current count is greater than zero then it is decremented.
     * If the new count is zero then all waiting threads are re-enabled for
     * thread scheduling purposes.
     *
     *
     *
     * If the current count equals zero then nothing happens.
     */
    fun countDown() {
        sync.releaseShared(1)
    }

    /**
     * Returns a string identifying this latch, as well as its state.
     * The state, in brackets, includes the String `"Count ="`
     * followed by the current count.
     *
     * @return a string identifying this latch, as well as its state
     */
    override fun toString(): String {
        return super.toString() + "[Count = " + sync.count + "]"
    }
}