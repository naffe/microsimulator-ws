package microsimulator.ws

import spock.lang.Specification

import static microsimulator.ws.Helpers.excludeByName
import static microsimulator.ws.Helpers.onClassPath
import static MicroSimulator.simulator

class SimulatorLoadingSpecification extends Specification {

    def "Test loading a single simulation from the classpath"() {
        setup:
        MicroSimulator simulator = simulator(TestUtils.randomPort())
        when:
        simulator.loadSimulations(onClassPath("loaders/loc1/loc1valid1.yml"))
        then:
        simulator.simulationCount() == 1
        cleanup:
        simulator.shutdown()
    }

    def "Test loading a two simulations from the classpath"() {
        setup:
        MicroSimulator simulator = simulator(TestUtils.randomPort())
        when:
        simulator.loadSimulations(onClassPath("loaders/loc1/loc1valid1.yml", "loaders/loc1/loc1valid2.yml"))
        then:
        simulator.simulationCount() == 2
        cleanup:
        simulator.shutdown()
    }


    def "Test loading a single directory on the classpath"() {
        setup:
        MicroSimulator simulator = simulator(TestUtils.randomPort())
        when:
        simulator.loadSimulations(onClassPath("loaders/loc1"))
        then:
        simulator.simulationCount() == 2
        cleanup:
        simulator.shutdown()
    }

    def "Test loading two directories on the classpath"() {
        setup:
        MicroSimulator simulator = simulator(TestUtils.randomPort())
        when:
        simulator.loadSimulations(onClassPath("loaders/loc1", "loaders/loc2"))
        then:
        simulator.simulationCount() == 4
        cleanup:
        simulator.shutdown()
    }

    def "Test loading two incorrect directories on the classpath"() {
        setup:
        MicroSimulator simulator = simulator(TestUtils.randomPort())
        when:
        simulator.loadSimulations(onClassPath("loaders/na", "loaders/na2", "loaders/na3"))
        then:
        simulator.simulationCount() == 0
        cleanup:
        simulator.shutdown()
    }

    def "Test loading two directories on the classpath as a list and repeating the list"() {
        setup:
        MicroSimulator simulator = simulator(TestUtils.randomPort())
        when:
        simulator.loadSimulations(onClassPath(["loaders/loc1", "loaders/loc2", "loaders/loc1", "loaders/loc2"]))
        then:
        simulator.simulationCount() == 4
        cleanup:
        simulator.shutdown()
    }

    def "Test loading 4 valid simulations but exclude 2 by name"() {
        setup:
        MicroSimulator simulator = simulator(TestUtils.randomPort())
        when:
        simulator.loadSimulations(onClassPath("loaders/loc1", "loaders/loc2", excludeByName("one")))
        then:
        simulator.simulationCount() == 2
        cleanup:
        simulator.shutdown()
    }

    def "Test invalid files won't load"(){
        setup:
        MicroSimulator simulator = simulator(TestUtils.randomPort())
        when:
        simulator.loadSimulations(onClassPath("loaders/loc1/loc1invalid.txt", "loaders/loc1/loc1invalid2.txt", "loaders/loc2/loc2invalid.txt", "loaders/loc2/locinvalid2.txt" ))
        then:
        simulator.simulationCount() == 0
        cleanup:
        simulator.shutdown()
    }
}

