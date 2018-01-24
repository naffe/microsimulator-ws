package microsimulator.ws

import microsimulator.ws.loader.SimulationLoader
import mu.KotlinLogging


/**
 * Bootstrap class for the simulator.
 */
public class MicroSimulator(port: Int) {
    private val microSimulatorHandlerService = MicroSimulatorHandlerService(port)
    private val logger = KotlinLogging.logger {}

    fun loadSimulation(microSimulation: MicroSimulation): MicroSimulator {
        startup(listOf(MicroSimulationContext(System.getProperty("user.dir"), microSimulation)))
        return this
    }


    fun loadSimulation(yamlString: String): MicroSimulator {
        val microSimulation = YamlToSimulation.toSimulation(yamlString) ?: throw SimulationInvalidException()
        startup(listOf(MicroSimulationContext(System.getProperty("user.dir"), microSimulation)))
        return this
    }


    fun loadSimulations(loader: SimulationLoader): MicroSimulator {
        loadSimulations(listOf(loader))
        return this
    }


    fun loadSimulations(vararg loaders: SimulationLoader): MicroSimulator {
        loadSimulations(loaders.toList())
        return this
    }

    fun loadSimulations(loaders: Collection<SimulationLoader>): MicroSimulator {
        loaders.forEach { loader ->
            val loadedSimulations = loader.process()
            startup(loadedSimulations)
        }
        return this
    }

    private fun startup(simulations: Collection<MicroSimulationContext>) {
        simulations.forEach { microSimulatorHandlerService.addSimulation(it) }
    }

    /**
     * Number of loaded simulationContext
     * @return the loaded simulation count
     */
    fun simulationCount(): Int {
        return microSimulatorHandlerService.simulationCount()
    }

    /**
     * Shut down the simulator
     */
    fun shutdown() {
        logger.info("Shutting down server")
        microSimulatorHandlerService.shutdownAll()
    }

    /**
     * Return simulation path based on its logical path and [httpMethod]
     *
     * @param path       the path for the simulation
     * @param httpMethod the [httpMethod] of the specification
     * @return the found [MicroSimulation] or null if none found
     */
    fun findSimulation(path: String, httpMethod: MicroSimulatorHandlerService.HttpMethod): MicroSimulationContext? {
        return microSimulatorHandlerService.findSimulationContextByPath(path, httpMethod)
    }

    /**
     * Return simulation based on its name
     *
     * @param name the name for this simulation
     * @return the found [MicroSimulation] or null if none found
     */
    fun findSimulationByName(name: String): MicroSimulationContext? {
        return microSimulatorHandlerService.findSimulationContextByName(name)
    }

    companion object {
        val defaultPort = 9999

        /**
         * New [MicroSimulator] on port 9999
         * @param A new simulator instance.
         */
        @JvmStatic
        fun simulator(): MicroSimulator {
            return simulator(defaultPort)
        }

        /**
         * Start the simulator on a specified port
         * @param port tcp port (1 to 65536
         */
        @JvmStatic
        fun simulator(port: Int): MicroSimulator = MicroSimulator(port)
    }

}