package microsimulator.ws

import mu.KotlinLogging
import spark.Request
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Context object that provides a running view of the simulation.
 */
class MicroSimulationContext(val classpathContext: String, val microSimulation: MicroSimulation, val id: String = UUID.randomUUID().toString()) {
    private val logger = KotlinLogging.logger {}

    private val messages = ArrayList<Request>()
    private var countDownLatch = ResettableCountDownLatch(1);

    /**
     * Holds the request just received for this simulation
     *
     * @param request - the request
     */
    fun simulationInvoked(request: Request) {
        messages.add(request)
        countDownLatch.countDown()
    }

    /**
     * The number of times this simulation has been called.
     *
     * @return the number of times called
     */
    fun callCount(): Int {
        return messages.size
    }

    /**
     * Last request body to reach the simulation
     *
     * @return the last request body or null if no last request
     */
    fun lastRequestBody(): String? {
        return messages.lastOrNull()?.body()
    }

    /**
     * Last request to reach the simulation
     *
     * @return the last message
     */
    fun lastRequest(): Request? {
        return messages.lastOrNull()
    }

    /**
     * All received requests to reach the simulation (by receipt order ascending)
     * @return the last message
     */
    fun allRequests(): List<Request> {
        return messages
    }

    /**
     * Blocks the current thread until this simulation is called.
     */
    fun blockUntilCalled() {
        blockUntilCalled(1, Integer.MAX_VALUE, TimeUnit.DAYS)
    }

    /**
     * Blocks the current thread until this [MicroSimulation]is called or the timeout parameter delay is met .
     * @param timeout  the timeout length
     * @param timeUnit the unit of time to lock for
     */
    fun blockUntilCalled(timeout: Int, timeUnit: TimeUnit) {
        blockUntilCalled(1, timeout, timeUnit)
    }

    /**
     * Blocks the current thread until this [MicroSimulation]is called  and won't release this thread until count or time is reached
     * @param count  the number of time to be called.
     * @param timeout  the timeout length
     * @param timeUnit the unit of time to lock for
     */
    fun blockUntilCalled(count: Int, timeout: Int, timeUnit: TimeUnit) {
        countDownLatch = ResettableCountDownLatch(count)
        countDownLatch.await(timeout.toLong(), timeUnit)
        countDownLatch.reset()
    }

}