package microsimulator.ws

import mu.KotlinLogging
import spark.Route
import spark.Service
import microsimulator.ws.handler.BaseHandler
import java.util.*

class MicroSimulatorHandlerService(onPort: Int) {
    private val logger = KotlinLogging.logger {}

    private val handlers = HashMap<String, BaseHandler>()
    private val validSimulations = mutableListOf<MicroSimulationContext>()
    private val sparkService = igniteService(onPort, 10)


    fun addSimulation(microSimulationContext: MicroSimulationContext) {
        validSimulations.add(microSimulationContext)
        setupRoute(microSimulationContext)
    }


    private fun igniteService(port: Int, threads: Int): Service {
        return Service.ignite()
                .port(port).threadPool(threads)
    }

    private fun lookupHandler(microSimulationContext: MicroSimulationContext): BaseHandler {
        val baseHandler: BaseHandler = handlers.computeIfAbsent(microSimulationContext.microSimulation.path + "::" + microSimulationContext.microSimulation.httpMethod) { s -> BaseHandler() }
        baseHandler.addRoute(microSimulationContext)
        return baseHandler
    }


    private fun setupRoute(simulationContext: MicroSimulationContext) {
        val handler = lookupHandler(simulationContext)
        if (handler.routeCount() == 1) {
            when (simulationContext.microSimulation.httpMethod) {
                HttpMethod.get -> {
                    sparkService.get(simulationContext.microSimulation.path, simulationContext.microSimulation.request.contentType, Route(handler::processRequest))
                    logger.info("GET {} now listening", simulationContext.microSimulation.path)
                }
                HttpMethod.post -> {
                    sparkService.post(simulationContext.microSimulation.path, simulationContext.microSimulation.request.contentType, Route(handler::processRequest))
                    logger.info("POST {} now listening", simulationContext.microSimulation.path)
                }
                HttpMethod.put -> {
                    sparkService.put(simulationContext.microSimulation.path, simulationContext.microSimulation.request.contentType, Route(handler::processRequest))
                    logger.info("PUT {} now listening", simulationContext.microSimulation.path)
                }
                HttpMethod.patch -> {
                    sparkService.patch(simulationContext.microSimulation.path, simulationContext.microSimulation.request.contentType, Route(handler::processRequest))
                    logger.info("PATCH {} now listening", simulationContext.microSimulation.path)
                }
                HttpMethod.delete -> {
                    sparkService.delete(simulationContext.microSimulation.path, simulationContext.microSimulation.request.contentType, Route(handler::processRequest))
                    logger.info("DELETE {} now listening", simulationContext.microSimulation.path)
                }
                HttpMethod.head -> {
                    sparkService.head(simulationContext.microSimulation.path, simulationContext.microSimulation.request.contentType, Route(handler::processRequest))
                    logger.info("HEAD {} now listening", simulationContext.microSimulation.path)
                }
            }
        }
    }

    enum class HttpMethod {
        get, post, put, patch, delete, head
    }

    /**
     * Shuts down the [MicroSimulation].
     */
    fun shutdownAll() {
        sparkService.stop()
        validSimulations.clear()
        handlers.clear()
    }

    /**
     * returns a simulation if available (by logical path).
     *
     * @param simulationId The id of the simulation.
     * @return the [MicroSimulation] or null if not found.
     */
    fun findSimulationContextByPath(path: String, httpMethod: HttpMethod): MicroSimulationContext? {
        val filterValues = validSimulations.filter { e -> e.microSimulation.path.equals(path, true) && e.microSimulation.httpMethod.equals(httpMethod) }
        return filterValues.firstOrNull();
    }

    /**
     * returns a simulation if available (by simulation name).
     *
     * @param name The name of the simulation.
     * @return the [MicroSimulation] or null if not found.
     */
    fun findSimulationContextByName(name: String): MicroSimulationContext? {
        val filterValues = validSimulations.filter { e -> e.microSimulation.name.equals(name, true) }
        return filterValues.firstOrNull();
    }

    /**
     * Obtain the number of loaded [MicroSimulation]'s
     * @return total number of valid simulations.
     */
    fun simulationCount(): Int {
        return validSimulations.size
    }
}