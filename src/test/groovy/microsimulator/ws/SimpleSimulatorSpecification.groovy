package microsimulator.ws

import spock.lang.Specification

import java.util.concurrent.TimeUnit

import static io.restassured.RestAssured.given
import static microsimulator.ws.Helpers.onClassPath
import static org.hamcrest.core.IsEqual.equalTo
import static MicroSimulator.simulator

class SimpleSimulatorSpecification extends Specification {

    def "Simple simulator Test"() {
        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)

        when:
        simulator.loadSimulations(onClassPath("simple/valid1.yml"))

        then:
        given().port(port).get("/hello").then().assertThat()
                .statusCode(200).and().body(equalTo("hello world"))
        given().port(port).get("/hello").then().assertThat()
                .statusCode(200).and().body(equalTo("hello world"))
        simulator.simulationCount() == 1
        simulator.findSimulation("/hello", MicroSimulatorHandlerService.HttpMethod.get).callCount() == 2
        simulator.findSimulationByName("test.simple").callCount() == 2

        cleanup:
        simulator.shutdown()
    }

    def "Simple simulator Test using file: simple.yml with countdown hatch"() {
        setup:
        int port = TestUtils.randomPort()
        def simulator = simulator(port)
        when:
        simulator.loadSimulations(onClassPath("simple/valid1.yml"))
        then:
        new Timer().runAfter(10000) {
            given().port(port).get("/hello").then().assertThat()
                    .statusCode(200).and().body(equalTo("hello world"))
        }
        def simulation = simulator.findSimulation("/hello", MicroSimulatorHandlerService.HttpMethod.get)
        simulation.blockUntilCalled(12, TimeUnit.SECONDS)
        simulator.simulationCount() == 1
        simulation.callCount() == 1
        cleanup:
        simulator.shutdown()
    }
}

