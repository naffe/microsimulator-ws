package microsimulator.ws.loader

import microsimulator.ws.MicroSimulationContext
import java.util.stream.Collectors

abstract class Filter {
    abstract fun filter(simulations: Collection<MicroSimulationContext>): Collection<MicroSimulationContext>
}

/**
 * Exclude simulations by Name
 */
class ExcludeByNameFilter(val name: String) : Filter() {

    override fun filter(simulations: Collection<MicroSimulationContext>): Collection<MicroSimulationContext> {
        return simulations.stream()
                .filter({ t -> name != t.microSimulation.name }).collect(Collectors.toList())
    }
}

/**
 * Exclude simulations by Tags
 */
class ExcludeByTaqFilter(val tag: String) : Filter() {

    override fun filter(simulations: Collection<MicroSimulationContext>): Collection<MicroSimulationContext> {
        return simulations.stream()
                .filter({ !it.microSimulation.tag.split(",").contains(tag) }).collect(Collectors.toList())
    }
}

/**
 * Exclude simulations by Name
 */
class IncludeByNameFilter(val name: String) : Filter() {

    override fun filter(simulations: Collection<MicroSimulationContext>): Collection<MicroSimulationContext> {
        return simulations.stream()
                .filter({ t -> name == t.microSimulation.name }).collect(Collectors.toList())
    }
}

/**
 * Exclude simulations by Tags
 */
class IncludeByTaqFilter(val tag: String) : Filter() {

    override fun filter(simulations: Collection<MicroSimulationContext>): Collection<MicroSimulationContext> {
        return simulations.stream()
                .filter({ it.microSimulation.tag.split(",").contains(tag) }).collect(Collectors.toList())
    }
}


/**
 * Base Simulation Loader responsible for loading simulations.
 */
abstract class SimulationLoader(private val filters: Collection<Filter>) {
    fun process(): Collection<MicroSimulationContext> {
        val simulations = mutableListOf<MicroSimulationContext>(*load().toTypedArray())
        for (filter in filters) {
            simulations.removeAll(filter.filter(simulations))
        }
        return simulations
    }

    open fun load(): Collection<MicroSimulationContext> {
        return emptyList()
    }
}

