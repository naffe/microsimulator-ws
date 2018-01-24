package microsimulator.ws.handler

import mu.KotlinLogging
import org.apache.commons.io.FilenameUtils
import org.apache.commons.lang3.RandomUtils
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import spark.Request
import spark.Response
import microsimulator.ws.loader.ClassPathLoader
import microsimulator.ws.MicroSimulation

import microsimulator.ws.MicroSimulation.RouteRequestFilterType.*
import microsimulator.ws.MicroSimulationContext
import java.util.*

/**
 * Base handler for all routes with the same httpmethod and path.
 *
 */
class BaseHandler {
    val logger = KotlinLogging.logger {}
    var runningHandlerCallCount: Int = 0
    private val wsSimulationContexts = ArrayList<MicroSimulationContext>()

    private val filterTypes = object : HashMap<MicroSimulation.RouteRequestFilterType, RouteRequestFilterer>() {
        init {
            put(contains, ContainsRouteRequestFilterer(false))
            put(absent, ContainsRouteRequestFilterer(true))
            put(none, NoneRouteRequestFilterer())
            put(callCount, RequestCallCountRouteRequestFilterer())
        }
    }

    fun addRoute(microSimulation: MicroSimulationContext) {
        wsSimulationContexts.add(microSimulation)
    }

    fun routeCount(): Int {
        return wsSimulationContexts.size
    }

    fun processRequest(request: Request, response: Response): String {
        logger.info("request {}, of content-type {}, with body:{}", request.contextPath(), request.contentType(), request.body())
        val wsSimulationContext = loadSimulationContext(request)
        return if (wsSimulationContext == null) {
            response.status(404)
            ""
        } else
            processRequestWithContext(wsSimulationContext, request, response)
    }

    private fun processRequestWithContext(microSimulationContext: MicroSimulationContext, request: Request, response: Response): String {
        microSimulationContext.simulationInvoked(request)
        runningHandlerCallCount++
        latencyCheck(microSimulationContext)
        response.status(microSimulationContext.microSimulation.response.statusCode)
        response.type(microSimulationContext.microSimulation.response.contentType)
        return if (microSimulationContext.microSimulation.response.body.isNotEmpty()) loadResponseContent(microSimulationContext) else ""
    }

    private fun latencyCheck(microSimulationContext: MicroSimulationContext) {
        val wsSimulation = microSimulationContext.microSimulation
        if (StringUtils.isNotEmpty(wsSimulation.latency)) {
            if (StringUtils.contains(wsSimulation.latency, "-")) {
                //do the range stuff
                val start = NumberUtils.toLong(StringUtils.substringBefore(wsSimulation.latency, "-"), 0)
                val end = NumberUtils.toLong(StringUtils.substringAfter(wsSimulation.latency, "-"), 0)
                val delay = RandomUtils.nextLong(start, end)
                sleepFor(delay)
            }
            sleepFor(NumberUtils.toLong(wsSimulation.latency, 0))
        }
    }

    private fun loadResponseContent(microSimulationContext: MicroSimulationContext): String {
        val wsSimulation = microSimulationContext.microSimulation
        if (!wsSimulation.response.body.startsWith("wssimulatorRequest:"))
            return wsSimulation.response.body
        val requestFile = wsSimulation.response.body.substringAfter("wssimulatorRequest:")
        var content = ClassPathLoader.readClasspathResourceQuietly(requestFile)
        if (content.isNotEmpty()) {
            val classpath = FilenameUtils.getFullPathNoEndSeparator(microSimulationContext.classpathContext)
            content = ClassPathLoader.readClasspathResourceQuietly(classpath + "/" + requestFile)
        }
        return content
    }

    private fun sleepFor(sleepFor: Long) {
        Thread.sleep(sleepFor)
    }

    private fun loadSimulationContext(request: Request): MicroSimulationContext? {
        //filter out all elements that don't meet the filter criteria.

        val candidates = wsSimulationContexts
                .filter { filterTypes[it.microSimulation.request.filterType]!!.filter(this, it, request) }
                .sortedByDescending { wsSimulationContext -> wsSimulationContext.microSimulation.priority }
        if (candidates.any { it.microSimulation.request.filterType == contains }) {
            return candidates.first { wsSimulationContext -> wsSimulationContext.microSimulation.request.filterType == contains }
        }
        return candidates.firstOrNull()
    }
}