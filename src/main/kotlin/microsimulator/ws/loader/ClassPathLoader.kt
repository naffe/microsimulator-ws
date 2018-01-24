package microsimulator.ws.loader

import microsimulator.ws.MicroSimulationContext
import mu.KotlinLogging
import org.apache.commons.io.IOUtils
import org.reflections.Reflections
import org.reflections.scanners.ResourcesScanner
import microsimulator.ws.YamlToSimulation.Companion.toSimulation
import java.io.IOException
import java.nio.charset.Charset
import java.util.regex.Pattern
import java.util.stream.Collectors

/**
 * Simulation Loader for loading [WSSimulation] resources from the classpath.
 */
class ClassPathLoader(val locations: Set<String>, filters: Collection<Filter>) : SimulationLoader(filters) {
    override fun load(): Collection<MicroSimulationContext> {
        return locations.stream()
                .flatMap({ attemptToLoadFrom(it).stream() }).collect(Collectors.toList())
    }

    private fun attemptToLoadFrom(classpathLocation: String): Set<MicroSimulationContext> {
        val reflections = Reflections(classpathLocation, ResourcesScanner())
        val candidateClasspathLocations = reflections.getResources(Pattern.compile(".*\\.yml"))
        val simulationContexts = mutableSetOf<MicroSimulationContext>()
        candidateClasspathLocations.forEach { location ->
            val simulation = toSimulation(readClasspathResourceQuietly(location))
            if (simulation != null)
                simulationContexts.add(MicroSimulationContext(location, simulation))
        }
        return simulationContexts

    }

    companion object {
        val logger = KotlinLogging.logger {}

        internal fun readClasspathResourceQuietly(packageLocation: String): String {
            try {
                val resourceAsStream = ClassPathLoader::class.java.classLoader.getResourceAsStream(packageLocation)
                return if (resourceAsStream == null) "" else IOUtils.toString(resourceAsStream, Charset.defaultCharset())
            } catch (e: IOException) {
                logger.info("Couldn'simulation read file {}", packageLocation)
            }
            return ""
        }
    }

}